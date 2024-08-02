package com.example.practice_app.db

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//Define the ApiService interface for network calls
interface ApiService {
    //POST request for user login
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    //POST request for user signup
    @POST("api/auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>
}

//Data class for signup request
data class SignupRequest(
    val username: String,
    val password: String,
    val email: String
)

//data class for signup response
data class SignupResponse(
    val message: String
)

//Data class for login request
data class LoginRequest(val username: String, val password: String)

//Data class for login response
data class LoginResponse(val token: String)