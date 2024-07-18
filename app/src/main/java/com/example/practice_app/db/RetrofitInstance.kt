package com.example.practice_app.db

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//responsible for creating and providing an instance of the
// ApiService interface using Retrofit.
object RetrofitInstance {
    private const val BASE_URL = "https://2806066a-8784-47ee-9a68-f2732d7842d8.mock.pstmn.io"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}