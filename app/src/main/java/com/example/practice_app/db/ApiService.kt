package com.example.practice_app.db

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

//specifies methods for interacting with your backend API using Retrofit.
interface ApiService {

    //Endpoint from postman
    @GET("get")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<Map<String, Any>>
}