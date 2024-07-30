package com.example.practice_app.db

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/post")
    suspend fun googleLogin(@Body request: LoginRequestGoogle): Response<LoginResponseGoogle>

    @POST("api/auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>
}

data class SignupRequest(
    val username: String,
    val password: String
)

data class SignupResponse(
    val message: String
)

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String)

data class LoginRequestGoogle(val idToken: String)

data class LoginResponseGoogle(val idTokenResponse: String)