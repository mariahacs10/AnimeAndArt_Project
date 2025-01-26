package com.example.practice_app.models
import android.util.Log
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.db.ApiService
import com.example.practice_app.db.FavoriteImageDao
import com.example.practice_app.db.FavoriteRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// FavoritesRepository is a repository class responsible for handling favorite-related data.
// It acts as a mediator between the API (remote data source) and the local Room database (local data source).
// It abstracts away the details of managing the data from the rest of the application.

//class FavoritesRepository(
//    private val apiService: ApiService,
//    private val favoriteImageDao: FavoriteImageDao,
//    private val userRepository: UserRepository // Add this dependency
//) {
//
//    // Fix: Modify to properly handle null case
//    private suspend fun getCurrentUserId(): Long? {
//        return userRepository.getUserId()
//    }
//
//    suspend fun refreshFavoritesForUser(userId: Long) {
//        if (userId <= 0) {
//            Log.e("FavoritesRepository", "Invalid userId: $userId. Skipping refresh.")
//            return
//        }
//        withContext(Dispatchers.IO) {
//            try {
//                val favoritesFromServer = getFavoritesFromServer(userId)
//                syncFavoritesWithLocalDb(userId, favoritesFromServer)
//            } catch (e: Exception) {
//                Log.e("FavoritesRepository", "Error refreshing favorites: ${e.message}")
//            }
//        }
//    }
//
//
//
//    suspend fun getCachedFavorites(userId: Long): List<AllImagesItem> {
//        return withContext(Dispatchers.IO) {
//            try {
//                favoriteImageDao.getFavoritesForUser(userId).map { favoriteImage ->
//                    AllImagesItem(
//                        allImagesId = favoriteImage.allImagesId.toInt(),
//                        allImageUrl = favoriteImage.allImageUrl ?: "",
//                        allImageDescriptions = favoriteImage.allImageDescriptions,
//                        category = favoriteImage.category,
//                        favorites = emptyList()
//                    )
//                }
//            } catch (e: Exception) {
//                Log.e("FavoritesRepository", "Error fetching cached favorites: ${e.message}")
//                emptyList()
//            }
//        }
//    }
//
//
////    suspend fun getCachedFavorites(userId: Long): List<AllImagesItem> {
////        return try {
////            val cachedFavorites = favoriteImageDao.getFavoritesForUser(userId)
////            Log.d("FavoritesDebug", "Cached favorites for userId $userId: ${cachedFavorites.size}")
////            cachedFavorites.map { favoriteImage ->
////                AllImagesItem(
////                    allImagesId = favoriteImage.allImagesId.toInt(),
////                    allImageUrl = favoriteImage.allImageUrl ?: "",
////                    allImageDescriptions = favoriteImage.allImageDescriptions,
////                    category = favoriteImage.category,
////                    favorites = emptyList()
////                )
////            }
////        } catch (e: Exception) {
////            Log.e("FavoritesRepository", "Error fetching cached favorites: ${e.message}")
////            emptyList() // Fallback to empty list
////        }
////    }
//
//    // And update the init block to handle nullable userId
//    init {
//        CoroutineScope(Dispatchers.IO).launch {
//            val currentUserId = getCurrentUserId()
//            if (currentUserId == null || currentUserId <= 0) {
//                favoriteImageDao.clearFavorites()
//            }
//        }
//    }
////    private fun getCurrentUserId(): Long {
////        return userRepository.getUserId() ?: -1L
////    }
//
//    // Update isFavoriteCached to handle nullable userId
//    suspend fun isFavoriteCached(imageId: Long): Boolean {
//        val currentUserId = getCurrentUserId() ?: return false
//        val favorite = favoriteImageDao.getFavoriteById(imageId, currentUserId)  // Updated to use userId
//        return favorite != null
//    }
////
////    suspend fun refreshFavoritesForUser(userId: Long) {
////        try {
////            // Fetch favorites from the backend
////            val favoritesFromServer = getFavoritesFromServer(userId)
////            // Cache them locally
////            cacheFavoritesLocally(favoritesFromServer, userId)
////        } catch (e: Exception) {
////            Log.e("FavoritesRepository", "Error refreshing favorites: ${e.message}")
////            throw e // Re-throw for ViewModel to handle
////        }
////    }
//
//
////    suspend fun refreshFavoritesForUser(userId: Long) {
////        Log.d("FavoritesDebug", "Refreshing favorites for userId: $userId")
////        val favorites = getFavoritesFromServer(userId)
////        Log.d("FavoritesDebug", "Fetched favorites: ${favorites.size}")
////        cacheFavoritesLocally(favorites, userId)
////    }
//
//
//    //    // Function to add a favorite image to both the server and the local Room database.
//    // Takes the imageId, userId, imageUrl, and description as parameters.
//    suspend fun addFavorite(imageId: Long, userId: Long, imageUrl: String, description: String): Result<Boolean> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiService.addFavorite(FavoriteRequest(imageId, userId))
//                if (response.isSuccessful) {
//                    favoriteImageDao.insertFavorite(
//                        FavoriteImage(
//                            allImagesId = imageId,
//                            allImageUrl = imageUrl,
//                            allImageDescriptions = description,
//                            category = "default",
//                            userId = userId
//                        )
//                    )
//
//                    // Then refresh from server to ensure consistency
//                    refreshFavoritesForUser(userId)
//
//                    Result.success(true)
//                } else {
//                    Result.failure(Exception("Failed to add favorite on server"))
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//    }
//
//    suspend fun removeFavorite(userId: Long, imageId: Long): Result<Boolean> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiService.deleteFavorite(userId, imageId)
//                if (response.isSuccessful) {
//                    favoriteImageDao.deleteFavoriteById(imageId, userId)
//                    Result.success(true)
//                } else {
//                    Result.failure(Exception("Failed to remove favorite on server"))
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//    }
//
//    private suspend fun syncFavoritesWithLocalDb(userId: Long, favorites: List<AllImagesItem>) {
//        withContext(Dispatchers.IO) {
//            // First clear only this user's favorites
//            favoriteImageDao.clearFavoritesForUser(userId)
//
//            // Then insert new favorites
//            val favoriteImages = favorites.map {
//                FavoriteImage(
//                    allImagesId = it.allImagesId.toLong(),
//                    allImageUrl = it.allImageUrl,
//                    allImageDescriptions = it.allImageDescriptions,
//                    category = it.category,
//                    userId = userId  // Ensure userId is set
//                )
//            }
//            favoriteImageDao.insertFavorites(favoriteImages)
//        }
//    }
//
//    private suspend fun getFavoritesFromServer(userId: Long): List<AllImagesItem> {
//        val response = apiService.getUserFavorites(userId)
//        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
//    }
//
////
////    suspend fun getFavoritesForGoogleUser(googleUserId: String): List<AllImagesItem> {
////        // Retrieve the consistent userId from SharedPreferences
////        val userId = googleUserId.hashCode().toLong()
////
////        // Fetch data from the Room database
////        return favoriteImageDao.getFavoritesForUser(userId).map { favoriteImage ->
////            AllImagesItem(
////                allImagesId = favoriteImage.allImagesId.toInt(),
////                allImageUrl = favoriteImage.allImageUrl,
////                allImageDescriptions = favoriteImage.allImageDescriptions,
////                category = favoriteImage.category,
////                favorites = emptyList() // Adjust if needed
////            )
////        }
////    }
//
//    suspend fun addFavoriteLocal(imageId: Long, userId: Long, imageUrl: String, description: String) {
//        val favoriteImage = FavoriteImage(
//            allImagesId = imageId,
//            allImageUrl = imageUrl,
//            allImageDescriptions = description,
//            category = "local", // Optionally mark as "local"
//            userId = userId
//        )
//        favoriteImageDao.insertFavorite(favoriteImage) // Insert into Room database
//    }
//
//    // Function to get cached favorites from the local Room database for a specific user.
//    // Maps the FavoriteImage objects from the database to AllImagesItem objects.
////    suspend fun getCachedFavorites(userId: Long): List<AllImagesItem> {
////        val cachedFavorites = favoriteImageDao.getFavoritesForUser(userId)
////        Log.d("FavoritesDebug", "Cached favorites for userId $userId: ${cachedFavorites.size}")
////        return cachedFavorites.map { favoriteImage ->
////            AllImagesItem(
////                allImagesId = favoriteImage.allImagesId.toInt(),
////                allImageUrl = favoriteImage.allImageUrl ?: "",
////                allImageDescriptions = favoriteImage.allImageDescriptions,
////                category = favoriteImage.category,
////                favorites = emptyList()
////            )
////        }
////    }
//
//
//    // Function to check if a specific image is already favorited in the local Room database.
//    // Returns true if the image is found, otherwise false.
////    suspend fun isFavoriteCached(imageId: Long): Boolean {
////        return favoriteImageDao.getFavoriteById(imageId) != null
////    }
//
//    // Function to fetch favorite images from the server and cache them in the local Room database.
//    // If the server call is unsuccessful, it returns the cached favorites from Room.
//    suspend fun clearFavoritesForUser(userId: Long) {
//        favoriteImageDao.clearFavoritesForUser(userId)
//    }
//
//}
//

/**
 * Repository managing favorite images with local caching and server synchronization
 * Implements offline-first architecture pattern
 */
class FavoritesRepository(
    private val apiService: ApiService,
    private val favoriteImageDao: FavoriteImageDao,
    private val userRepository: UserRepository
) {
    // Safe retrieval of current user ID with null safety
    private suspend fun getCurrentUserId(): Long? {
        return userRepository.getUserId()
    }

    // Synchronizes favorites between server and local cache
    suspend fun refreshFavoritesForUser(userId: Long) {
        if (userId <= 0) {
            Log.e("FavoritesRepository", "Invalid userId: $userId. Skipping refresh.")
            return
        }
        withContext(Dispatchers.IO) {
            try {
                val favoritesFromServer = getFavoritesFromServer(userId)
                syncFavoritesWithLocalDb(userId, favoritesFromServer)
            } catch (e: Exception) {
                Log.e("FavoritesRepository", "Error refreshing favorites: ${e.message}")
            }
        }
    }

    // Retrieves favorites from local database with domain model mapping
    suspend fun getCachedFavorites(userId: Long): List<AllImagesItem> {
        return withContext(Dispatchers.IO) {
            try {
                favoriteImageDao.getFavoritesForUser(userId).map { favoriteImage ->
                    AllImagesItem(
                        allImagesId = favoriteImage.allImagesId.toInt(),
                        allImageUrl = favoriteImage.allImageUrl ?: "",
                        allImageDescriptions = favoriteImage.allImageDescriptions,
                        category = favoriteImage.category,
                        favorites = emptyList()
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesRepository", "Error fetching cached favorites: ${e.message}")
                emptyList()
            }
        }
    }

    // Initialization block for data consistency on repository creation
    init {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = getCurrentUserId()
            if (currentUserId == null || currentUserId <= 0) {
                favoriteImageDao.clearFavorites()
            }
        }
    }

    // Checks if image is favorited in local cache for current user
    suspend fun isFavoriteCached(imageId: Long): Boolean {
        val currentUserId = getCurrentUserId() ?: return false
        val favorite = favoriteImageDao.getFavoriteById(imageId, currentUserId)
        return favorite != null
    }

    // Adds favorite with server sync and local cache update
    suspend fun addFavorite(imageId: Long, userId: Long, imageUrl: String, description: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.addFavorite(FavoriteRequest(imageId, userId))
                if (response.isSuccessful) {
                    favoriteImageDao.insertFavorite(
                        FavoriteImage(
                            allImagesId = imageId,
                            allImageUrl = imageUrl,
                            allImageDescriptions = description,
                            category = "default",
                            userId = userId
                        )
                    )
                    refreshFavoritesForUser(userId)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Failed to add favorite on server"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Removes favorite with server sync and local cache cleanup
    suspend fun removeFavorite(userId: Long, imageId: Long): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteFavorite(userId, imageId)
                if (response.isSuccessful) {
                    favoriteImageDao.deleteFavoriteById(imageId, userId)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Failed to remove favorite on server"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Syncs local database with server data ensuring user-scoped operations
    private suspend fun syncFavoritesWithLocalDb(userId: Long, favorites: List<AllImagesItem>) {
        withContext(Dispatchers.IO) {
            favoriteImageDao.clearFavoritesForUser(userId)
            val favoriteImages = favorites.map {
                FavoriteImage(
                    allImagesId = it.allImagesId.toLong(),
                    allImageUrl = it.allImageUrl,
                    allImageDescriptions = it.allImageDescriptions,
                    category = it.category,
                    userId = userId
                )
            }
            favoriteImageDao.insertFavorites(favoriteImages)
        }
    }

    // Fetches favorites from server with error handling
    private suspend fun getFavoritesFromServer(userId: Long): List<AllImagesItem> {
        val response = apiService.getUserFavorites(userId)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    // Adds favorite to local cache only for offline support
    suspend fun addFavoriteLocal(imageId: Long, userId: Long, imageUrl: String, description: String) {
        val favoriteImage = FavoriteImage(
            allImagesId = imageId,
            allImageUrl = imageUrl,
            allImageDescriptions = description,
            category = "local",
            userId = userId
        )
        favoriteImageDao.insertFavorite(favoriteImage)
    }

    // Cleans up user's favorites from local cache
    suspend fun clearFavoritesForUser(userId: Long) {
        favoriteImageDao.clearFavoritesForUser(userId)
    }
}