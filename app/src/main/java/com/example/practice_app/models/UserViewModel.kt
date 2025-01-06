package com.example.practice_app.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.db.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define UserViewModel class that extends ViewModel and takes a UserRepository as a parameter
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    // Mutable state for username
    val username = mutableStateOf("")
    // Mutable state for email
    var email = mutableStateOf("")
    // Mutable state for password
    var password = mutableStateOf("")
    // Mutable state for confirm password
    var confirmPassword = mutableStateOf("")

    fun updateUsername(newUsername: String) {
        username.value = newUsername
    }

    fun getGoogleAccountId(context: Context): String? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account?.id
    }

    init {
        // Load the logged-in username when ViewModel is created
        username.value = userRepository.getLoggedInUsername()
    }

    private val _isDarkModeEnabled = MutableStateFlow(userRepository.isDarkModeEnabled())
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    // Toggle dark mode and save the preference
    fun toggleDarkMode(enabled: Boolean) {
        _isDarkModeEnabled.value = enabled
        userRepository.saveDarkModePreference(enabled)
    }

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
        return userRepository.getLoggedInUsername()
    }

    // Function to login with Google
    // Function to login with Google
//    fun loginWithGoogle(context: Context) {
//        val googleUserId = userRepository.getGoogleAccountId(context)
//        if (googleUserId != null) {
//            // Fetch the Google ID token using the repository method
//            val googleIdToken = userRepository.getGoogleIdToken(context)
//            if (googleIdToken != null) {
//                // Now you can use googleIdToken for whatever purpose, like saving it or verifying it with your backend
//                Log.d("UserViewModel", "Google ID Token: $googleIdToken")
//            } else {
//                Log.e("UserViewModel", "Failed to fetch Google ID Token")
//            }
//
//            // Proceed with Google Sign-In logic
//            userRepository.handleGoogleSignIn(context)  // Handle Google Sign-In in Repository
//            userRepository.saveLoginState(true, isGoogleSignIn = true)
//            userRepository.saveGoogleUserId(googleUserId.hashCode().toLong())  // Save Google user ID if needed
//            Log.d("UserViewModel", "Successfully logged in with Google: $googleUserId")
//        } else {
//            Log.e("UserViewModel", "Failed to retrieve Google User ID")
//        }
//    }


    // Function to check if the current login is a Google sign-in
//    fun isGoogleSignIn(): Boolean {
//        return userRepository.isGoogleSignIn()
//    }

    // Function to log out a Google-signed-in user
//    fun logoutUserGoogle() {
//        userRepository.saveLoginState(false, false) // Reset login state
//        username.value = "" // Clear username state
//        email.value = ""  // Clear email if needed
//    }

    // Function to log out the user
    suspend fun logoutUser(username: String) {
            userRepository.updateLoginStatus(username, false)
            userRepository.saveLoginState(false)
            this@UserViewModel.username.value = "" // Clear username
    }

    // Function to get the logged-in user
    fun getLoggedInUser() {
        viewModelScope.launch {
            val user = userRepository.getLoggedInUser()
            user?.let {
                username.value = it.username ?: ""
            }
        }
    }

    // Suspend function to login with credentials
    suspend fun loginWithCredentials(username: String, password: String): Boolean {
        return userRepository.loginUser(username, password)
    }

    // Function to get the saved token from SharedPreferences (if JWT is used)
//    fun getToken(): String? {
//        return userRepository.getToken()
//    }

    // Suspend function to update a user's password
    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String) {
        userRepository.getUserByPassword(username, oldPassword, newPassword)
    }

    // For Google Sign-In
//    fun updateGoogleUser(account: GoogleSignInAccount) {
//        val googleToken = account.idToken
//        val user = User(
//            username = account.displayName ?: "Guest",
//            googleToken = googleToken,
//            password = null,  // No password needed for Google sign-in
//            confirmPassword = null,  // No confirmPassword needed
//            email = account.email  // You can use the Google account email if needed
//        )
//        viewModelScope.launch {
//            userRepository.insert(user)
//        }
//    }

}
