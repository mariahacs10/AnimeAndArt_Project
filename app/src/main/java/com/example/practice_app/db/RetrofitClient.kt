package com.example.practice_app.db

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Define the base URL for the API. This should be changed to your actual API URL in production. but for now this
    //is fine
    private const val BASE_URL = "https://artworkdockerimage-v1.onrender.com"
    // local     private const val BASE_URL = "http://192.168.68.118:8686"


    // Create a Gson instance with lenient parsing to handle malformed JSON more gracefully
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Create a logging interceptor to log network requests and responses for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Log the entire body of requests and responses
    }

    // Create an OkHttpClient and add the logging interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Create a Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // Set the base URL for all API calls
        .client(client)  // Use the OkHttpClient we created with the logging interceptor
        .addConverterFactory(GsonConverterFactory.create(gson))  // Use Gson for JSON parsing
        .build()

    // Create an instance of the API service interface
    // 'by lazy' ensures this is only created when first accessed, and then cached
    val createApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}