package com.example.practice_app.models
import android.util.Log
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.db.ApiService
import com.example.practice_app.db.FavoriteImageDao
import com.example.practice_app.db.FavoriteRequest

// FavoritesRepository is a repository class responsible for handling favorite-related data.
// It acts as a mediator between the API (remote data source) and the local Room database (local data source).
// It abstracts away the details of managing the data from the rest of the application.

class FavoritesRepository(
    private val apiService: ApiService,
    private val favoriteImageDao: FavoriteImageDao
) {

//    // Function to add a favorite image to both the server and the local Room database.
    // Takes the imageId, userId, imageUrl, and description as parameters.
    suspend fun addFavorite(imageId: Long, userId: Long, imageUrl: String, description: String): Result<Boolean> {
        return try {
            // Create a FavoriteRequest object to send to the API.
            val favoriteRequest = FavoriteRequest(imageId = imageId, userId = userId)
            // Call the API to add the favorite.
            val response = apiService.addFavorite(favoriteRequest)

            if (response.isSuccessful) {
                // If successful, cache the favorite in Room database.
                favoriteImageDao.insertFavorite(
                    FavoriteImage(
                        allImagesId = imageId,
                        allImageUrl = imageUrl,
                        allImageDescriptions = description,
                        category = "category",  // Adjust as necessary
                        userId = userId
                    )
                )
                Result.success(true)
            } else {
                // Return failure if the API call was not successful.
                Result.failure(Exception("Failed to add favorite on server"))
            }
        } catch (e: Exception) {
            // Handle any exceptions, such as network issues.
            Result.failure(e)
        }
    }
//
//    suspend fun getFavoritesForGoogleUser(googleUserId: String): List<AllImagesItem> {
//        // Retrieve the consistent userId from SharedPreferences
//        val userId = googleUserId.hashCode().toLong()
//
//        // Fetch data from the Room database
//        return favoriteImageDao.getFavoritesForUser(userId).map { favoriteImage ->
//            AllImagesItem(
//                allImagesId = favoriteImage.allImagesId.toInt(),
//                allImageUrl = favoriteImage.allImageUrl,
//                allImageDescriptions = favoriteImage.allImageDescriptions,
//                category = favoriteImage.category,
//                favorites = emptyList() // Adjust if needed
//            )
//        }
//    }

    suspend fun addFavoriteLocal(imageId: Long, userId: Long, imageUrl: String, description: String) {
        val favoriteImage = FavoriteImage(
            allImagesId = imageId,
            allImageUrl = imageUrl,
            allImageDescriptions = description,
            category = "local", // Optionally mark as "local"
            userId = userId
        )
        favoriteImageDao.insertFavorite(favoriteImage) // Insert into Room database
    }







    // Function to get cached favorites from the local Room database for a specific user.
    // Maps the FavoriteImage objects from the database to AllImagesItem objects.
    suspend fun getCachedFavorites(userId: Long): List<AllImagesItem> {
        return favoriteImageDao.getFavoritesForUser(userId)
            .map { favoriteImage ->
                AllImagesItem(
                    allImagesId = favoriteImage.allImagesId.toInt(),
                    allImageUrl = favoriteImage.allImageUrl ?: "",
                    allImageDescriptions = favoriteImage.allImageDescriptions,
                    category = favoriteImage.category,
                    favorites = emptyList()
                )
            }
    }
    suspend fun removeFavorite(userId: Long, imageId: Long): Result<Boolean> {
        return try {
            // Call the API with the correct order: userId first, then imageId
            val response = apiService.deleteFavorite(userId, imageId)
            if (response.isSuccessful) {
                // If successful, remove the favorite from the local Room database.
                favoriteImageDao.deleteFavoriteById(imageId)
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to remove favorite on server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    // Function to check if a specific image is already favorited in the local Room database.
    // Returns true if the image is found, otherwise false.
    suspend fun isFavoriteCached(imageId: Long): Boolean {
        return favoriteImageDao.getFavoriteById(imageId) != null
    }

    // Function to fetch favorite images from the server and cache them in the local Room database.
    // If the server call is unsuccessful, it returns the cached favorites from Room.
    suspend fun getFavoritesFromServer(userId: Long): List<AllImagesItem> {
        val response = apiService.getUserFavorites(userId)
        return if (response.isSuccessful) {
            val allImages = response.body() ?: emptyList()

            // Cache the favorites from the server in Room.
            val favoriteImages = allImages.map { allImageItem ->
                FavoriteImage(
                    allImagesId = allImageItem.allImagesId.toLong(),
                    allImageUrl = allImageItem.allImageUrl,
                    allImageDescriptions = allImageItem.allImageDescriptions,
                    category = allImageItem.category,
                    userId = userId
                )
            }
            favoriteImageDao.insertFavorites(favoriteImages)
            // Return the list of favorite images from the server.
            allImages
        } else {
            // If server call fails, return the cached favorites from Room.
            favoriteImageDao.getFavoritesForUser(userId).map {
                AllImagesItem(
                    allImagesId = it.allImagesId.toInt(),
                    allImageUrl = it.allImageUrl ?: "",
                    allImageDescriptions = it.allImageDescriptions,
                    category = it.category,
                    favorites = emptyList()
                )
            }
        }
    }

    // Function to cache favorite images locally in Room.
    // Takes a list of AllImagesItem and maps them to FavoriteImage objects for storage in Room.
    suspend fun cacheFavoritesLocally(favorites: List<AllImagesItem>, userId: Long) {
        val favoriteImages = favorites.map { allImageItem ->
            FavoriteImage(
                allImagesId = allImageItem.allImagesId.toLong(),
                allImageUrl = allImageItem.allImageUrl,
                allImageDescriptions = allImageItem.allImageDescriptions,
                category = allImageItem.category,
                userId = userId
            )
        }
        // Insert the list of favorites into the Room database.
        favoriteImageDao.insertFavorites(favoriteImages)
    }
}

