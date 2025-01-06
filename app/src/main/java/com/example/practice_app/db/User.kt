package com.example.practice_app.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="user")
data class User(
    //The primary key for the entity, autoGenerate=true means
    //a unique id will be auto generated for each user.

    /**Just a little side note on autoGenerate=true

    If you manually set primary key(autoGenerate=false), you have to make sure the values are unique
    With Auto generated IDs, Room handles uniqueness for you.

    Also you dont have to implement logic to create new unique ids every time you insert
    Room handles it automatically.
     */
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    //The username property. A non-null String
    val username: String,
    //The password property. a non-null String
    val password: String?,
    //Optional confirm password property,Can be null
    val confirmPassword: String?,
    val email: String?,
    @ColumnInfo(name = "google_token") val googleToken: String? = null, // This will store the Google ID token
    val isLoggedIn: Boolean = false
)