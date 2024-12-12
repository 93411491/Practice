package com.example.practice.network

import State
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.bytedance.tools.codelocator.BuildConfig
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




const val TAG = "GrowthSdk.NetworkManager"

object NetworkManager {
    private const val HTTP_PROTOCOL = "https://"

    private lateinit var applicationContext: Context

    private const val DELAY_TIME = 1000L

    private lateinit var host: String

    fun init(context: Context, host: String) {
        applicationContext = context
        this.host = host
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

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(HTTP_PROTOCOL + host)
            .client(client) // 添加 OkHttp 客户端
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    internal val apiService by lazy {
        createApiService(ApiService::class.java)
    }

    private fun <T> createApiService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    //网络检查工具
    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(): Boolean {
        if (NetworkManager::applicationContext.isInitialized.not()) {
            return false
        }
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager
            .getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    @PublishedApi
    internal suspend fun <T> safeApiCall(apiCall: suspend () -> T): State<T> {
        return withContext(Dispatchers.IO) {
            repeat(3) { retryIndex ->
                try {
                    val result = apiCall()
                    return@withContext State.success(result)
                } catch (e: Exception) {
                    if (e is IllegalArgumentException) {
                        "参数异常，异常详细信息为:  $e ".logE(TAG)
                        throw e
                    }
                    "请求异常: ${DELAY_TIME / 1000} s 后，重试请求 $retryIndex ,$e".logI(TAG)
                    delay(DELAY_TIME)
                }
            }
            State.error(Exception("something wrong"))
        }
    }

    internal suspend inline fun <reified T : BaseResponse> post(
        url: String,
        request: Any
    ): State<T> {
        return safeApiCall {
            debugLog("发送请求")
            val response = apiService.post(url, request)
            if (response.isSuccessful) {
                debugLog("请求成功,${response.body()}")
                val responseBody =
                    response.body() ?: throw IllegalArgumentException("response body is null")
                val body = parseResponseBody<T>(responseBody)
                debugLog("请求解码成功,${body}")
                if (body.isSuccess()) {
                    body
                } else {
                    throw IOException(body.getFailedReason())
                }
            } else {
                val errorInfo = response.errorBody()?.string()
                throw IOException(errorInfo)
            }
        }
    }

    inline fun <reified T : BaseResponse> parseResponseBody(responseBody: ResponseBody): T {
        val gson = Gson()
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(responseBody.charStream(), type)
    }

    private fun debugLog(msg:String){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "debugLog: $msg")
        }
    }
}


