package com.example.practice_app.db

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("post")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}

data class LoginResponse(
    val args: Map<String, String>,
    val data: Map<String, String>,
    val files: Map<String, String>,
    val form: Map<String, String>,
    val headers: Map<String, String>,
    val json: LoginRequest?,
    val url: String
)

data class LoginRequest(val username: String, val password: String)