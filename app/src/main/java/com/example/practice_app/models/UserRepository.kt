package com.example.practice_app.models

import android.content.Context
import android.content.SharedPreferences
import com.example.practice_app.db.AppDatabase
import com.example.practice_app.db.User

// Declare the UserRepository class with a context parameter
class UserRepository(context: Context) {
    // Get an instance of the AppDatabase using the provided context
    private val db = AppDatabase.getDatabase(context)
    // Get an instance of the UserDao from the AppDatabase
    private val userDao = db.userDao()

    // Declare a suspend function for inserting a user into the database
    suspend fun insert(user: User) {
        // Call the insert function of UserDao to insert the user
        userDao.insert(user)
    }

    // Declare a suspend function for retrieving a user by username from the database
    suspend fun getUser(username: String): User? {
        // Call the getUser function of UserDao to retrieve the user by username
        return userDao.getUser(username)
    }

    // Declare a suspend function for updating a user's password in the database
    suspend fun getUserByPassword(username: String, oldPassword: String, newPassword: String) {
        // Call the updatePassword function of UserDao to update the user's password
        userDao.updatePassword(username, oldPassword, newPassword)
    }

    // Declare a suspend function for updating a user's login status in the database
    suspend fun updateLoginStatus(username: String, isLoggedIn: Boolean) {
        // Call the updateLoginStatus function of UserDao to update the user's login status
        userDao.updateLoginStatus(username, isLoggedIn)
    }

    // Declare a suspend function for retrieving the logged-in user from the database
    suspend fun getLoggedInUser(): User? {
        // Call the getLoggedInUser function of UserDao to retrieve the logged-in user
        return userDao.getLoggedInUser()
    }

    // Get an instance of SharedPreferences with the name "MyPrefs" and private mode
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveLoginState(isLoggedIn: Boolean, isGoogleSignIn: Boolean = false, username: String = "") {
        sharedPreferences.edit()
            .putBoolean("isLoggedIn", isLoggedIn)
            .putBoolean("isGoogleSignIn", isGoogleSignIn)
            .putString("loggedInUsername", username)
            .apply()
    }

    fun getLoggedInUsername(): String {
        return sharedPreferences.getString("loggedInUsername", "") ?: ""
    }


    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    //adding google signin to save logifc
    fun isGoogleSignIn(): Boolean {
        return sharedPreferences.getBoolean("isGoogleSignIn", false)
    }
}