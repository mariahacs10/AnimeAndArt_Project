package com.example.practice_app.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Annotated with @Entity to represent a table in the Room database named "favorites_table".
// Each instance of FavoriteImage will be a row in the favorites_table.
@Entity(tableName = "favorites_table")
data class FavoriteImage(
    @PrimaryKey(autoGenerate = false)
    val allImagesId: Long, // Unique ID of the image
    val allImageUrl: String, // URL of the image
    val allImageDescriptions: String, // Description of the image
    val category: String, // Category of the image
    val userId: Long // ID of the user who favorited the image
)
