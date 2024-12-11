package com.example.practice.network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    suspend fun post(@Url url: String, @Body request: Any): retrofit2.Response<ResponseBody>
}