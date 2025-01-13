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
interface FavoriteImageDao {
    // Insert operations - keep as is since they handle full objects with userId
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteImage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteImage)

    @Update
    suspend fun updateFavorite(favorite: FavoriteImage)

    // Queries that already handle userId correctly - keep as is
    @Query("SELECT * FROM favorites_table WHERE allImagesId = :imageId AND userId = :userId")
    suspend fun getFavoriteByIdAndUser(imageId: Long, userId: Long): FavoriteImage?

    @Query("DELETE FROM favorites_table WHERE allImagesId = :imageId AND userId = :userId")
    suspend fun deleteFavoriteByIdAndUser(imageId: Long, userId: Long): Int

    @Query("SELECT * FROM favorites_table WHERE userId = :userId")
    suspend fun getFavoritesForUser(userId: Long): List<FavoriteImage>

    @Query("DELETE FROM favorites_table WHERE userId = :userId")
    suspend fun clearFavoritesForUser(userId: Long)

    // Modified queries to include userId
    @Query("DELETE FROM favorites_table WHERE allImagesId = :imageId AND userId = :userId")
    suspend fun deleteFavoriteById(imageId: Long, userId: Long): Int

    @Query("SELECT * FROM favorites_table WHERE allImagesId = :imageId AND userId = :userId")
    suspend fun getFavoriteById(imageId: Long, userId: Long): FavoriteImage?

    // Special cleanup operations
    @Query("DELETE FROM favorites_table WHERE userId IS NULL OR userId <= 0")
    suspend fun clearInvalidUserFavorites()

    // Only use this during complete database reset
    @Query("DELETE FROM favorites_table")
    suspend fun clearFavorites()
}