package com.example.practice_app.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.db.User
import kotlinx.coroutines.launch

// Define UserViewModel class that extends ViewModel and takes a UserRepository as a parameter
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    // Mutable state for username
    var username = mutableStateOf("")
    // Mutable state for email
    var email = mutableStateOf("")
    // Mutable state for password
    var password = mutableStateOf("")
    // Mutable state for confirm password
    var confirmPassword = mutableStateOf("")

    // Suspend function to handle sign up click
    suspend fun onSignUpClick() {
        // Create a User object with current state values
        val user = User(username = username.value, password = password.value, confirmPassword = confirmPassword.value, email = email.value)
        // Insert the user into the repository
        userRepository.insert(user)
        // Reset all input fields
        username.value = ""
        password.value = ""
        confirmPassword.value = ""
        email.value = ""
    }

    // Suspend function to sign up a user
    suspend fun signupUser(username: String, password: String, confirmPassword: String, email: String): Boolean {
        // Call repository's signupUser function and return its result
        return userRepository.signupUser(username, password, confirmPassword, email)
    }

    // Function to get the logged-in username
    fun getLoggedInUsername(): String {
        // Return the logged-in username from the repository
        return userRepository.getLoggedInUsername()
    }

    // Function to handle Google sign-in
    fun loginWithGoogle() {
        // Save login state as Google sign-in
        userRepository.saveLoginState(true, isGoogleSignIn = true)
    }

    // Function to check if the current login is a Google sign-in
    fun isGoogleSignIn(): Boolean {
        // Return whether the current login is a Google sign-in from the repository
        return userRepository.isGoogleSignIn()
    }

    // Function to log out a Google-signed-in user
    fun logoutUserGoogle() {
        // Save login state as logged out and not Google sign-in
        userRepository.saveLoginState(false, false)
    }

    // Function to log out a user
    fun logoutUser(username: String) {
        // Launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            // Update login status to false in the repository
            userRepository.updateLoginStatus(username, false)
            // Save login state as logged out
            userRepository.saveLoginState(false)
        }
    }

    // Function to get the logged-in user
    fun getLoggedInUser() {
        // Launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            // Get the logged-in user from the repository
            val user = userRepository.getLoggedInUser()
            // If user exists, update the username state
            user?.let {
                username.value = it.username ?: ""
            }
        }
    }

    // Suspend function to get a username
    suspend fun getUsername(inputUsername: String): String? {
        // Get user from repository and return their username
        val user = userRepository.getUser(inputUsername)
        return user?.username
    }

    // Suspend function to login with credentials
    suspend fun loginWithCredentials(username: String, password: String): Boolean {
        // Call repository's loginUser function and return its result
        return userRepository.loginUser(username, password)
    }

    // Suspend function to get a user's password
    suspend fun getPassword(inputUsername: String): String? {
        // Get user from repository and return their password
        val user = userRepository.getUser(inputUsername)
        return user?.password
    }

    // Suspend function to update a user's password
    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String) {
        // Call repository's function to update the password
        userRepository.getUserByPassword(username, oldPassword, newPassword)
    }
}