package com.example.practice_app.models

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.db.RetrofitInstance
import com.example.practice_app.db.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Declare the UserViewModel class that extends ViewModel
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    // Declare mutable state variables for username, password, and confirmPassword
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    private val apiService = RetrofitInstance.apiService

    //This method simulates sending a mock token to a
    // backend API and verifying its validity.
    //It's  used for testing purposes to ensure your token
    // verification logic works as expected.
    suspend fun verifyMockToken() {
        val mockToken = "MOCK_GOOGLE_AUTH_TOKEN_FOR_TESTING_123"
        try {
            val response = apiService.verifyToken("Bearer $mockToken")
            if (response.isSuccessful) {
                val headers = response.body()?.get("headers") as? Map<*, *>
                val receivedToken = headers?.get("authorization") as? String
                if (receivedToken == "Bearer $mockToken") {
                    Log.d("TokenVerification", "Mock token sent and verified successfully!")
                } else {
                    Log.e("TokenVerification", "Token mismatch or not found in response")
                }
            } else {
                Log.e("TokenVerification", "Request failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("TokenVerification", "Error verifying token", e)
        }
    }

    // Declare a suspend function for handling the sign-up click event
    suspend fun onSignUpClick() {
        // Create a User object with the current values of username, password, and confirmPassword
        val user = User(username = username.value, password = password.value, confirmPassword = confirmPassword.value)
        // Insert the user into the user repository
        userRepository.insert(user)
        // Reset the values of username, password, and confirmPassword to empty strings
        username.value = ""
        password.value = ""
        confirmPassword.value = ""
    }

    fun getLoggedInUsername(): String {
        return userRepository.getLoggedInUsername()
    }

    //Gooogle sign in method for saving  login state
    fun loginWithGoogle() {
        userRepository.saveLoginState(true, isGoogleSignIn = true)
    }

    //gettingg the google sign in information
    fun isGoogleSignIn(): Boolean {
        return userRepository.isGoogleSignIn()
    }

    //loggingg out the user from google sign in
    fun logoutUserGoogle() {
        userRepository.saveLoginState(false, false)
    }

    // Declare a suspend function for logging in a user
    suspend fun loginUser(username: String) {
        val user = userRepository.getUser(username)
        if (user != null) {
            userRepository.updateLoginStatus(username, true)
            userRepository.saveLoginState(
                isLoggedIn = true,
                isGoogleSignIn = false,
                username = username
            )
            this.username.value = username
        }
        verifyMockToken()
    }



    // Declare a suspend function for logging out a user
    suspend fun logoutUser(username: String) {
        // Update the login status of the user to false in the user repository
        userRepository.updateLoginStatus(username, false)
        // Save the login state as false in the user repository
        userRepository.saveLoginState(false)
    }

    // Declare a suspend function for getting the logged-in user
    suspend fun getLoggedInUser(): User? {
        // Get the logged-in user from the user repository
        return userRepository.getLoggedInUser()
    }

    // Declare a suspend function for getting the username based on the input username
    suspend fun getUsername(inputUsername: String): String? {
        // Get the user from the user repository based on the input username
        val user = userRepository.getUser(inputUsername)
        // Return the username of the user if it exists, otherwise return null
        return user?.username
    }

    // Declare a suspend function for getting the password based on the input username
    suspend fun getPassword(inputUsername: String): String? {
        // Get the user from the user repository based on the input username
        val user = userRepository.getUser(inputUsername)
        // Return the password of the user if it exists, otherwise return null
        return user?.password
    }

    // Declare a suspend function for updating the password
    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String) {
        // Call the getUserByPassword function from the user repository with the provided parameters
        userRepository.getUserByPassword(username, oldPassword, newPassword)
    }
}