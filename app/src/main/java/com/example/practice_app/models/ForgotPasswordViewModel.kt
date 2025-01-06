package com.example.practice_app.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.db.ForgotPasswordRequest
import com.example.practice_app.db.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

// ViewModel class to handle the forgot password functionality.
// This class holds the state for the email input, loading status, success message, and error message.
class ForgotPasswordViewModel : ViewModel() {
    // Holds the email entered by the user.
    // Using mutableStateOf allows it to be observed and updated in a Jetpack Compose UI.
    var email by mutableStateOf("")

    // Tracks whether the request is currently loading.
    var isLoading by mutableStateOf(false)

    // Holds any error message that occurs during the forgot password process.
    // Null by default, meaning no error.
    var errorMessage by mutableStateOf<String?>(null)

    // Holds a success message if the request is successful.
    // Null by default, meaning no success message initially.
    var successMessage by mutableStateOf<String?>(null)


    // Function to send the forgot password request to the server.
    // This function launches a coroutine using viewModelScope to perform the network operation asynchronously.
    fun sendForgotPasswordRequest() {
        viewModelScope.launch {
            // Set loading to true when the request starts.
            isLoading = true
            // Reset any previous error messages.
            errorMessage = null
            successMessage = null

            try {
                // Send the forgot password request using the Retrofit API service.
                val response: ResponseBody = RetrofitClient.createApiService.forgotPassword(ForgotPasswordRequest(email))
                // If successful, store the server response in successMessage.
                successMessage = response.string()
                // Clear the email field after a successful request.
                email = ""
            } catch (e: HttpException) {
                // Handle HTTP exceptions (e.g., 4xx or 5xx server errors).
                errorMessage = "Server error: ${e.code()}"
            } catch (e: IOException) {
                // Handle network errors, such as no internet connection.
                errorMessage = "Network error: ${e.message}"
            } catch (e: Exception) {
                // Catch any other unexpected exceptions and display a generic error message.
                errorMessage = "An unexpected error occurred: ${e.message}"
            } finally {
                // Set loading to false after the request is completed (whether successful or not).
                isLoading = false
            }
        }
    }
}