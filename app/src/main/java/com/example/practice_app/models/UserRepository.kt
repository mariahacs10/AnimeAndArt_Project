package com.example.practice_app.models

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.practice_app.db.ApiService
import com.example.practice_app.db.AppDatabase
import com.example.practice_app.db.LoginRequest
import com.example.practice_app.db.LoginRequestGoogle
import com.example.practice_app.db.RetrofitClient
import com.example.practice_app.db.SignupRequest
import com.example.practice_app.db.User

class UserRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUser(username: String): User? {
        return userDao.getUser(username)
    }

    suspend fun getUserByPassword(username: String, oldPassword: String, newPassword: String) {
        userDao.updatePassword(username, oldPassword, newPassword)
    }

    suspend fun updateLoginStatus(username: String, isLoggedIn: Boolean) {
        userDao.updateLoginStatus(username, isLoggedIn)
    }

    suspend fun getLoggedInUser(): User? {
        return userDao.getLoggedInUser()
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveLoginState(isLoggedIn: Boolean, isGoogleSignIn: Boolean = false, username: String = "") {
        sharedPreferences.edit()
            .putBoolean("isLoggedIn", isLoggedIn)
            .putBoolean("isGoogleSignIn", isGoogleSignIn)
            .putString("loggedInUsername", username)
            .apply()
    }
    suspend fun loginWithGoogleToken(idToken: String): Boolean {
        try {
            Log.d("UserRepository", "Attempting to login with Google token: $idToken")
            val response = apiService.googleLogin(LoginRequestGoogle(idToken))
            Log.d("UserRepository", "Response received: ${response.code()}")
            if (response.isSuccessful && response.body() != null) {
                val googleLoginResponse = response.body()!!
                if (googleLoginResponse.idTokenResponse.isNotEmpty()) { // Change to check token directly
                    saveToken(googleLoginResponse.idTokenResponse)
                    saveLoginState(true, isGoogleSignIn = true)
                    return true
                } else {
                    Log.e("UserRepository", "Login failed")
                }
            } else {
                Log.e("UserRepository", "Login failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error during Google login", e)
        }
        return false
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun getLoggedInUsername(): String {
        return sharedPreferences.getString("loggedInUsername", "") ?: ""
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun isGoogleSignIn(): Boolean {
        return sharedPreferences.getBoolean("isGoogleSignIn", false)
    }

    private val apiService: ApiService = RetrofitClient.createApiService()

    suspend fun signupUser(username: String, password: String, confirmPassword: String): Boolean {
        try {
            val response = apiService.signup(SignupRequest(username, password))
            if (response.isSuccessful) {
                insert(User(username = username, password = password, confirmPassword = confirmPassword))
                return true
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error during signup", e)
        }
        return false
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                saveToken(token)
                updateLoginStatus(username, true)
                saveLoginState(true, false, username)
                return true
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error during login", e)
        }
        return false
    }
}