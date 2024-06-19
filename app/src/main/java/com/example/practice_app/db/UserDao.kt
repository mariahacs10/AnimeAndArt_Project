package com.example.practice_app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Declare the UserDao interface and annotate it with @Dao
@Dao
interface UserDao {

    // Declare a suspend function for inserting a user into the database
    // Annotate it with @Insert to specify the insert operation
    @Insert
    suspend fun insert(user: User)

    // Declare a suspend function for retrieving a user by username from the database
    // Annotate it with @Query and provide the SQL query to select the user based on the username
    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUser(username: String): User?

    // Declare a suspend function for updating a user's password in the database
    // Annotate it with @Query and provide the SQL query to update the password based on the username and old password
    @Query("UPDATE user SET password = :newPassword WHERE username = :username AND password = :oldPassword")
    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String)

    // Declare a suspend function for updating a user's login status in the database
    // Annotate it with @Query and provide the SQL query to update the login status based on the username
    @Query("UPDATE user SET isLoggedIn = :isLoggedIn WHERE username = :username")
    suspend fun updateLoginStatus(username: String, isLoggedIn: Boolean)

    // Declare a suspend function for retrieving the logged-in user from the database
    // Annotate it with @Query and provide the SQL query to select the user where isLoggedIn is 1, limiting the result to 1 row
    @Query("SELECT * FROM user WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): User?
}