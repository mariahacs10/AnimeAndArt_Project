package com.example.practice_app.db

import androidx.compose.runtime.MutableState
import com.example.practice_app.dataForAllImages.AllImages
import com.example.practice_app.dataForAllImages.AllImagesItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

//This ApiService defines all the API interactions for your app, including login, signup, fetching images,
// adding/removing favorites, and password reset functionality. The corresponding  request and response data classes are
// simple, each handling specific data for their respective API calls.
interface ApiService {

    // Function to handle user login.
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // Function for user signup.
    @POST("api/auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    // Function to fetch all images.
    @GET("/allImages")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun getAllImages(): Response<List<AllImagesItem>>

    // Function to get images by category.
    @GET("/allImages/category/{category}")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun getImagesByCategory(
        @Path("category") category: String
    ): Response<List<AllImagesItem>>

    // Function to add a favorite image.
    @POST("/favorites/addFav")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun addFavorite(
        @Body favoriteRequest: FavoriteRequest
    ): Response<ResponseBody>
    // Function to get all favorite images of a user with authType.
    @GET("/favorites/user/{userId}")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun getUserFavorites(
        @Path("userId") userId: Long
    ): Response<List<AllImagesItem>>

    // Function to delete a favorite image.
    @DELETE("/favorites/delete/{userId}/{imageId}")
    @Headers("X-Api-Key: ${com.example.practice_app.BuildConfig.API_KEY}")
    suspend fun deleteFavorite(
        @Path("userId") userId: Long,
        @Path("imageId") imageId: Long
    ): Response<ResponseBody>

    // Function to handle forgot password requests.
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ResponseBody
}




// Data class for signup request. Contains username, password, and email fields.
data class SignupRequest(
    val username: String,
    val password: String,
    val email: String
)

// Data class for signup response. Usually contains a success message.
data class SignupResponse(
    val message: String,
    val userId: Long
)


// Data class for login request. Contains username and password.
data class LoginRequest(val username: String, val password: String)

// Data class for login response. Contains token and userId.
data class LoginResponse(val token: String, val userId: Long)

// Data class for forgot password request. Contains just the email field.
data class ForgotPasswordRequest(val email: String)

// Data class for favorite request. Contains the imageId and userId, used when adding a favorite.
data class FavoriteRequest(
    // The ID of the image to be favorited.
    val imageId: Long,
    // The ID of the user making the request.
    val userId: Long
)