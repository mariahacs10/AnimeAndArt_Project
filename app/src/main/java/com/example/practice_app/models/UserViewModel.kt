package com.example.practice_app.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.db.FavoriteImageDao
import com.example.practice_app.db.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel managing user authentication, preferences, and account state
 * Implements MVVM pattern with Compose state management
 */
class UserViewModel(
    private val userRepository: UserRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    // Compose state holders for form fields
    val username = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")

    // Theme state management using StateFlow
    private val _isDarkModeEnabled = MutableStateFlow(userRepository.isDarkModeEnabled())
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    // Initialize with logged in user data
    init {
        username.value = userRepository.getLoggedInUsername()
    }

    // Handles user logout with favorites cleanup
    suspend fun logoutUser(favoritesViewModel: FavoritesViewModel) {
        userRepository.logout()
        favoritesViewModel.clearFavoritesOnLogout()
    }

    // Handles login with favorites refresh
    suspend fun loginWithCredentials(username: String, password: String): Result<Boolean> {
        val loginResult = userRepository.loginUser(username, password)
        return if (loginResult.isSuccess) {
            val userId = userRepository.getUserId()
            if (userId != null && userId > 0) {
                favoritesRepository.refreshFavoritesForUser(userId)
                Result.success(true)
            } else {
                Result.failure(Exception("Invalid user ID after login"))
            }
        } else {
            loginResult
        }
    }

    // Form field updater with state management
    fun updateUsername(newUsername: String) {
        username.value = newUsername
    }

    // Google Sign-In integration
    fun getGoogleAccountId(context: Context): String? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account?.id
    }

    // Theme toggle with persistence
    fun toggleDarkMode(enabled: Boolean) {
        _isDarkModeEnabled.value = enabled
        userRepository.saveDarkModePreference(enabled)
    }

    // Sign up form submission handler
    suspend fun onSignUpClick() {
        val user = User(
            username = username.value,
            password = password.value,
            confirmPassword = confirmPassword.value,
            email = email.value
        )
        userRepository.insert(user)
        resetFormFields()
    }

    // Form field reset helper
    private fun resetFormFields() {
        username.value = ""
        password.value = ""
        confirmPassword.value = ""
        email.value = ""
    }

    // User registration with validation
    suspend fun signupUser(
        username: String,
        password: String,
        confirmPassword: String,
        email: String
    ): Result<Boolean> {
        return try {
            if (password != confirmPassword) {
                Result.failure(Exception("Passwords do not match"))
            } else {
                val success = userRepository.signupUser(username, password, confirmPassword, email)
                if (success) Result.success(true) else Result.failure(Exception("Signup failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // User data accessors
    fun getLoggedInUsername(): String = userRepository.getLoggedInUsername()
    fun getLoggedInUserId(): Long? = userRepository.getUserId()
    suspend fun getLoggedInUser(): User? = userRepository.getLoggedInUser()

    // Password update handler
    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String) {
        userRepository.getUserByPassword(username, oldPassword, newPassword)
    }
}


//// Define UserViewModel class that extends ViewModel and takes a UserRepository as a parameter
//class UserViewModel(private val userRepository: UserRepository,
//                    private val favoritesRepository: FavoritesRepository // Add this dependency
//) : ViewModel() {
//    // Mutable state for username
//    val username = mutableStateOf("")
//    // Mutable state for email
//    var email = mutableStateOf("")
//    // Mutable state for password
//    var password = mutableStateOf("")
//    // Mutable state for confirm password
//    var confirmPassword = mutableStateOf("")
//
//    suspend fun logoutUser(favoritesViewModel: FavoritesViewModel) {
//        userRepository.logout() // Clear user data
//        favoritesViewModel.clearFavoritesOnLogout() // Clear ViewModel state
//    }
//
//
//    suspend fun loginWithCredentials(username: String, password: String): Result<Boolean> {
//        val loginResult = userRepository.loginUser(username, password)
//        return if (loginResult.isSuccess) {
//            val userId = userRepository.getUserId()
//            if (userId != null && userId > 0) {
//                favoritesRepository.refreshFavoritesForUser(userId) // Directly refresh favorites
//                Result.success(true)
//            } else {
//                Result.failure(Exception("Invalid user ID after login"))
//            }
//        } else {
//            loginResult
//        }
//    }
//
//    fun updateUsername(newUsername: String) {
//        username.value = newUsername
//    }
//
//    fun getGoogleAccountId(context: Context): String? {
//        val account = GoogleSignIn.getLastSignedInAccount(context)
//        return account?.id
//    }
//
//    init {
//        // Load the logged-in username when ViewModel is created
//        username.value = userRepository.getLoggedInUsername()
//    }
//
//    private val _isDarkModeEnabled = MutableStateFlow(userRepository.isDarkModeEnabled())
//    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()
//
//    // Toggle dark mode and save the preference
//    fun toggleDarkMode(enabled: Boolean) {
//        _isDarkModeEnabled.value = enabled
//        userRepository.saveDarkModePreference(enabled)
//    }
//
//    // Suspend function to handle sign up click
//    suspend fun onSignUpClick() {
//        // Create a User object with current state values
//        val user = User(username = username.value, password = password.value, confirmPassword = confirmPassword.value, email = email.value)
//        // Insert the user into the repository
//        userRepository.insert(user)
//        // Reset all input fields
//        username.value = ""
//        password.value = ""
//        confirmPassword.value = ""
//        email.value = ""
//    }
//
//    // Suspend function to sign up a user
//    suspend fun signupUser(username: String, password: String, confirmPassword: String, email: String): Result<Boolean> {
//        return try {
//            if (password != confirmPassword) {
//                Result.failure(Exception("Passwords do not match"))
//            } else {
//                val success = userRepository.signupUser(username, password, confirmPassword, email)
//                if (success) Result.success(true) else Result.failure(Exception("Signup failed"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//
//    // Function to get the logged-in username
//    fun getLoggedInUsername(): String {
//        return userRepository.getLoggedInUsername()
//    }
//
//    // Function to login with Google
//    // Function to login with Google
////    fun loginWithGoogle(context: Context) {
////        val googleUserId = userRepository.getGoogleAccountId(context)
////        if (googleUserId != null) {
////            // Fetch the Google ID token using the repository method
////            val googleIdToken = userRepository.getGoogleIdToken(context)
////            if (googleIdToken != null) {
////                // Now you can use googleIdToken for whatever purpose, like saving it or verifying it with your backend
////                Log.d("UserViewModel", "Google ID Token: $googleIdToken")
////            } else {
////                Log.e("UserViewModel", "Failed to fetch Google ID Token")
////            }
////
////            // Proceed with Google Sign-In logic
////            userRepository.handleGoogleSignIn(context)  // Handle Google Sign-In in Repository
////            userRepository.saveLoginState(true, isGoogleSignIn = true)
////            userRepository.saveGoogleUserId(googleUserId.hashCode().toLong())  // Save Google user ID if needed
////            Log.d("UserViewModel", "Successfully logged in with Google: $googleUserId")
////        } else {
////            Log.e("UserViewModel", "Failed to retrieve Google User ID")
////        }
////    }
//
//
//    // Function to check if the current login is a Google sign-in
////    fun isGoogleSignIn(): Boolean {
////        return userRepository.isGoogleSignIn()
////    }
//
//    // Function to log out a Google-signed-in user
////    fun logoutUserGoogle() {
////        userRepository.saveLoginState(false, false) // Reset login state
////        username.value = "" // Clear username state
////        email.value = ""  // Clear email if needed
////    }
//
//    // Function to log out the user
////    suspend fun logoutUser(username: String) {
////        userRepository.updateLoginStatus(username, false)
////        userRepository.saveLoginState(false)
////    }
//    // Function to get the logged-in user ID
//    fun getLoggedInUserId(): Long? {
//        return userRepository.getUserId()
//    }
//
//    // Function to log out the user
////    suspend fun logoutUser(favoritesViewModel: FavoritesViewModel) {
////        val userId = userRepository.getUserId()
////        if (userId != null && userId > 0) {
////            favoritesViewModel.clearFavoritesOnLogout(userId) // Clear cached favorites
////        }
////        userRepository.logout() // Clear session state
////    }
////
////    suspend fun loginWithCredentials(
////        username: String,
////        password: String,
////        favoritesViewModel: FavoritesViewModel
////    ): Boolean {
////        val loginSuccess = userRepository.loginUser(username, password)
////        if (loginSuccess) {
////            val userId = userRepository.getUserId()
////            if (userId != null && userId > 0) {
////                // Signal FavoritesViewModel to fetch fresh favorites for the logged-in user
////                favoritesViewModel.fetchFavorites(userId)
////            }
////        }
////        return loginSuccess
////    }
//
//    // Function to get the logged-in user
//    suspend fun getLoggedInUser(): User? {
//        return userRepository.getLoggedInUser()
//    }
//
//    // Suspend function to login with credentials
////    suspend fun loginWithCredentials(username: String, password: String): Boolean {
////
////        return userRepository.loginUser(username, password)
////
////    }
//
//    // Function to get the saved token from SharedPreferences (if JWT is used)
////    fun getToken(): String? {
////        return userRepository.getToken()
////    }
//
//    // Suspend function to update a user's password
//    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String) {
//        userRepository.getUserByPassword(username, oldPassword, newPassword)
//    }
//
//    // For Google Sign-In
////    fun updateGoogleUser(account: GoogleSignInAccount) {
////        val googleToken = account.idToken
////        val user = User(
////            username = account.displayName ?: "Guest",
////            googleToken = googleToken,
////            password = null,  // No password needed for Google sign-in
////            confirmPassword = null,  // No confirmPassword needed
////            email = account.email  // You can use the Google account email if needed
////        )
////        viewModelScope.launch {
////            userRepository.insert(user)
////        }
////    }
//
//}

