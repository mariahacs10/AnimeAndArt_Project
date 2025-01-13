package com.example.practice_app.models

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.practice_app.db.ApiService
import com.example.practice_app.db.AppDatabase
import com.example.practice_app.db.FavoriteImageDao
import com.example.practice_app.db.LoginRequest
import com.example.practice_app.db.RetrofitClient
import com.example.practice_app.db.SignupRequest
import com.example.practice_app.db.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//Define a UserRepository class that takes  a Context as a parameter
class UserRepository(context: Context, private val favoriteImageDao: FavoriteImageDao) {
    //get an instance of the AppDatabase
    private val db = AppDatabase.getDatabase(context)
    //Get the UserDao from the database
    private val userDao = db.userDao()


    //suspend function to insert a user into the database
    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    //suspend function to get a user by username from the database
    suspend fun getUser(username: String): User? {
        return userDao.getUser(username)
    }

    //suspend function to update a user's password in the database
    suspend fun getUserByPassword(username: String, oldPassword: String, newPassword: String) {
        userDao.updatePassword(username, oldPassword, newPassword)
    }

    //suspend function to update a user's login status in the database
    suspend fun updateLoginStatus(username: String, isLoggedIn: Boolean) {
        userDao.updateLoginStatus(username, isLoggedIn)
    }

    //Function to save login state in shared preferences
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    //Function to save the login state in SharedPreferences
    fun saveLoginState(isLoggedIn: Boolean, username: String = "") {
        sharedPreferences.edit()
            .putBoolean("isLoggedIn", isLoggedIn)
            .putString("loggedInUsername", username)
            .apply()
    }

    fun saveDarkModePreference(isDarkModeEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean("isDarkModeEnabled", isDarkModeEnabled)
            .apply()
    }

    // Retrieve dark mode preference
    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("isDarkModeEnabled", false) // default to light mode
    }
    //Function to save JWT token in SharedPreferences
    // Save JWT token in SharedPreferences
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
    }

    // Retrieve the JWT token from SharedPreferences
    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }


    //Function to get logged in username from sSharedpreferences
    fun getLoggedInUsername(): String {
        return sharedPreferences.getString("loggedInUsername", "") ?: ""
    }

    //Function to check if a user is logged in from SharedPreferences
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    //Function to check if the user signed in with Google from SharedPreferences
//    fun isGoogleSignIn(): Boolean {
//        return sharedPreferences.getBoolean("isGoogleSignIn", false)
//    }

    //Create an instance of ApiService using RetrofitClient
    private val apiService: ApiService = RetrofitClient.createApiService

    suspend fun signupUser(username: String, password: String, confirmPassword: String, email: String): Boolean {
        return try {
            // Make an API call to sign up the user
            val response = apiService.signup(SignupRequest(username, password, email))
            if (response.isSuccessful && response.body() != null) {
                val signupResponse = response.body()!!

                // Save the userId from the response
                saveUserId(signupResponse.userId)

                // Insert the user into the local database
                insert(User(username = username, password = password, confirmPassword = confirmPassword, email = email))

                Log.d("UserRepository", "Signup successful: ${signupResponse.message}")
                true
            } else {
                // Log the error response
                Log.e("UserRepository", "Signup failed: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            // Log any errors during the signup process
            Log.e("UserRepository", "Error during signup", e)
            false
        }
    }


    // Save userId in SharedPreferences
    fun saveUserId(userId: Long) {
        sharedPreferences.edit().putLong("user_id", userId).apply()
    }
    fun initializeUserState() {
        val userId = getUserId()
        if (userId != null && userId > 0) {
            saveLoginState(true, getLoggedInUsername())
        }
    }

    // Fixed implementation
    fun getUserId(): Long? {
        val userId = sharedPreferences.getLong("user_id", -1L)
        return if (userId > 0) userId else null
    }

//    suspend fun loginUser(username: String, password: String): Boolean {
//        try {
//            val response = apiService.login(LoginRequest(username, password))
//            if (response.isSuccessful && response.body() != null) {
//                val loginResponse = response.body()!!
//                val token = loginResponse.token
//                val userId = loginResponse.userId  // Retrieve the userId
//
//                saveToken(token)
//                saveUserId(userId)  // Save userId for future API calls
//                updateLoginStatus(username, true)
//                saveLoginState(true, username)
//                return true
//            } else {
//                Log.e("UserRepository", "Login failed: ${response.errorBody()?.string()}")
//                return false
//            }
//        } catch (e: Exception) {
//            Log.e("UserRepository", "Error during login", e)
//            return false
//        }
//    }

    fun isValidUser(): Boolean {
        val userId = getUserId()
        return userId != null && userId > 0 && !getToken().isNullOrEmpty()
    }

    private val _userState = MutableStateFlow<Pair<Long?, Boolean>>(null to false)
    val userState = _userState.asStateFlow()
    init {
        // Initialize state
        updateUserState()
    }

    private fun updateUserState() {
        val userId = getUserId()
        val isLoggedIn = isUserLoggedIn()
        _userState.value = userId to isLoggedIn
    }

    suspend fun loginUser(username: String, password: String): Result<Boolean> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                saveToken(loginResponse.token)
                saveUserId(loginResponse.userId)
                updateLoginStatus(username, true)
                saveLoginState(true, username)
                updateUserState()  // Update state after login
                Result.success(true)
            } else {
                Result.failure(Exception("Login failed: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


//    fun logout() {
//        saveLoginState(false)
//        saveUserId(-1) // Clear the stored userId
//        Log.d("UserRepository", "User logged out: userId cleared")
//    }

    suspend fun logout() {
        try {
            val userId = getUserId()
            // Clear favorites only if userId exists
            userId?.let {
                favoriteImageDao.clearFavoritesForUser(it)
            }

            // Clear all user-related data atomically
            sharedPreferences.edit().apply {
                remove("user_id")
                remove("jwt_token")
                remove("isLoggedIn")
                remove("loggedInUsername")
                apply()
            }
            updateUserState()  // Update state after logout
            Log.d("UserRepository", "User logged out: userId and preferences cleared")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error during logout", e)
            // Ensure preferences are cleared even if favorites clearing fails
            sharedPreferences.edit().clear().apply()
            updateUserState()  // Update state after logout
        }
    }

    // Add validation method
//    fun isValidUser(): Boolean {
//        return getUserId() != null && getUserId()!! > 0
//    }

//    suspend fun loginUser(username: String, password: String): Boolean {
//        try {
//            val response = apiService.login(LoginRequest(username, password))
//            if (response.isSuccessful && response.body() != null) {
//                val loginResponse = response.body()!!
//                val token = loginResponse.token
//                val userId = loginResponse.userId  // Retrieve the userId
//
//                saveToken(token)
//                saveUserId(userId)  // Save userId for future API calls
//                updateLoginStatus(username, true)
//                saveLoginState(true, username)
//                return true
//            } else {
//                Log.e("UserRepository", "Login failed: ${response.errorBody()?.string()}")
//                return false
//            }
//        } catch (e: Exception) {
//            Log.e("UserRepository", "Error during login", e)
//            return false
//        }
//    }
    suspend fun getLoggedInUser(): User? {
        return userDao.getLoggedInUser() // Or fetch from SharedPreferences if not using Room
    }
//    fun getGoogleAccountId(context: Context): String? {
//        val account = GoogleSignIn.getLastSignedInAccount(context)
//        return account?.id
//    }
//
////    fun saveGoogleUserId(userId: Long) {
////        sharedPreferences.edit().putLong("google_user_id", userId).apply()
////    }
////
////    fun getSavedGoogleUserId(): Long? {
////        val userId = sharedPreferences.getLong("google_user_id", -1L)
////        Log.d("UserRepository", "Retrieved Google user ID from SharedPreferences: $userId")
////        return if (userId != -1L) userId else null
////    }

//    fun handleGoogleSignIn(context: Context) {
//        val googleUserId = getGoogleAccountId(context) // Get the Google Account ID
//        if (googleUserId != null) {
//            val userId = kotlin.math.abs(googleUserId.hashCode().toLong()) // Ensure positive ID
//            saveGoogleUserId(userId) // Save the userId in SharedPreferences
//            Log.d("UserRepository", "Google User ID: $googleUserId, Saved userId: $userId")
//        } else {
//            Log.e("UserRepository", "Google Account ID is null")
//        }
//    }// Inside UserRepository
//    fun getGoogleIdToken(context: Context): String? {
//        val account = GoogleSignIn.getLastSignedInAccount(context)
//        return account?.idToken
//    }

}