package com.example.practice.net

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface BaseApiService {
    @POST
    fun post(@Url url: String, @Body body: Any): retrofit2.Response<ResponseBody>
}