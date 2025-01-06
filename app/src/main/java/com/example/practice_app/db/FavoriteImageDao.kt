package com.example.practice_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.models.FavoriteImage
import kotlinx.coroutines.flow.Flow

@Dao
// This interface defines the DAO (Data Access Object) for interacting with the favorites_table in the Room database.
// Room generates the implementation for this interface automatically.
// Each function is a database operation like inserting, querying, or deleting data.
interface FavoriteImageDao {

    // Insert a list of favorites into the favorites_table.
    // onConflict = REPLACE means if a favorite with the same ID already exists, it will be replaced.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteImage>)

    // Insert a single favorite into the favorites_table.
    // Also uses REPLACE to overwrite any existing entry with the same primary key.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteImage)  // For inserting a single favorite

    // Delete a favorite from the database by its image ID.
    // This removes the record from favorites_table where the allImagesId matches the provided imageId.\
    @Query("DELETE FROM favorites_table WHERE allImagesId = :imageId")
    suspend fun deleteFavoriteById(imageId: Long): Int  // Return the number of rows affected
    @Query("DELETE FROM favorites_table")
    suspend fun clearFavorites()  // Clear all favorites from the local Room database

    // Retrieve all favorites for a specific user from the favorites_table.
    // This will return a list of FavoriteImage objects filtered by the userId.
    @Query("SELECT * FROM favorites_table WHERE userId = :userId")
    suspend fun getFavoritesForUser(userId: Long): List<FavoriteImage>

    // Retrieve a specific favorite by the image ID from the favorites_table.
    // Returns null if no favorite is found for the given image ID.
    @Query("SELECT * FROM favorites_table WHERE allImagesId = :imageId")
    suspend fun getFavoriteById(imageId: Long): FavoriteImage?  // Check if a specific image is favorited

    @Update
    suspend fun updateFavorite(favorite: FavoriteImage)

}
