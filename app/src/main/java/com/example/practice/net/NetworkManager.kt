package com.example.practice.net

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.practice.ext.logE
import com.example.practice.ext.logI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

internal const val TAG = "NetworkManager"

class NetworkManager {

    internal lateinit var applicationContext: Context

    companion object {
        private const val HTTP_PROTOCOL = "https://"
        private const val BASE_URL = "base.url.com" //自定义的baseurl
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(HTTP_PROTOCOL + BASE_URL)
            .client(client) // 添加 OkHttp 客户端
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //配置okhttp 客户端
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)    // 设置连接超时
            .readTimeout(15, TimeUnit.SECONDS)       // 设置读取超时
            .writeTimeout(15, TimeUnit.SECONDS)      // 设置写入超时
            .retryOnConnectionFailure(true)         // 允许失败重试
            .addInterceptor { chain ->
                if (!isNetworkAvailable()) {
                    return@addInterceptor Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(599)
                        .message("No network available")
                        .body(ResponseBody.create(null, ByteArray(0)))
                        .build()
                }
                chain.proceed(chain.request())
            }
            .build()
    }

    val apiService by lazy {
        createApiService(BaseApiService::class.java)
    }

    private fun <T> createApiService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    fun init(context: Context, baseUrl: String) {
        applicationContext = context
    }

    fun isInitialized() = ::applicationContext.isInitialized

    suspend inline fun  <T : BaseRequest, reified R : BaseResponse> post(url: String, body: T): State<R> {
        return safeApiCall {
            val response = apiService.post(url, body)
            if (response.isSuccessful) {
                val body = response.body() ?: throw IllegalStateException("Response body is null")
                val result = parseResponseBody<R>(body)
                if (result.isSuccess()) {
                    result
                } else {
                    throw IOException(result.getFailedReason())
                }
            } else {
                throw IOException(response.errorBody()?.toString())
            }
        }
    }

     inline fun <reified T : BaseResponse> parseResponseBody(responseBody: ResponseBody): T {
         val gson = Gson()
         val type = object : TypeToken<T>() {}.type
         return gson.fromJson(responseBody.charStream(), type)
    }

    @PublishedApi
    internal suspend fun <T> safeApiCall(apiCall: suspend () -> T): State<T> {
        return withContext(Dispatchers.IO) {
            repeat(3) {
                try {
                    val result = apiCall()
                    return@withContext State.success(result)
                } catch (e: Exception) {
                    if (e is IllegalArgumentException) {
                        "参数异常，e.message: ${e.message}".logE(TAG)
                        throw e
                    }
                    "safeApiCall: ${e.message}".logI(TAG)
                    delay(1000)
                }
            }
            State.error(Exception("something wrong"))
        }
    }

}

//网络检查工具
@SuppressLint("MissingPermission")
fun NetworkManager.isNetworkAvailable(): Boolean {
    if (isInitialized().not()) {
        "网络不可用".logI(TAG)
        return false
    }
    val connectivityManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager
        .getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

