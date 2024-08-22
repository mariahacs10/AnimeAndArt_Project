package com.example.practice_app.db

import com.example.practice_app.dataForAllImages.AllImagesItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

//Define the ApiService interface for network calls
interface ApiService {
    //POST request for user login
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    //POST request for user signup
    @POST("api/auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    @GET("/allImages")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun getAllImages(

    ):Response<List<AllImagesItem>>

    @GET("/allImages/category/{category}")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun getImagesByCategory(
        @Path("category") category: String
    ): Response<List<AllImagesItem>>
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