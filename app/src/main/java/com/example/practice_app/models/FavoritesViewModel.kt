package com.example.practice_app.models

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.dataForAllImages.AllImagesItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**   Summary of functionality:

Fetching Favorites: The fetchFavorites function attempts to fetch favorites from the server. If the server call fails, it falls back to
cached favorites from the local Room database.

Adding Favorites: The addFavorite function adds a favorite image to the server and local database. After adding,
it refreshes the list of favorites.

Removing Favorites: The removeFavorite function removes a favorite image from the server and local database.
After removal, it refreshes the list of favorites.

Checking if Favorite Exists: The isFavorite function checks if an image is already favorited in the local database.

Caching Favorites Locally: The cacheFavoritesLocally function stores fetched favorites into the local Room database.
 */

// The FavoritesViewModel class is responsible for managing the favorites data and actions for the UI.
// It interacts with the FavoritesRepository to fetch, add, and remove favorite images.
// This class extends ViewModel, allowing it to survive configuration changes.
class FavoritesViewModel(private val repository: FavoritesRepository) : ViewModel() {

    private val _itemToDelete = MutableLiveData<AllImagesItem?>()
    val itemToDelete: LiveData<AllImagesItem?> = _itemToDelete

    private val _favorites = MutableLiveData<List<AllImagesItem>>()
    val favorites: LiveData<List<AllImagesItem>> = _favorites

    private val _isDeleteDialogVisible = MutableLiveData<Boolean>()
    val isDeleteDialogVisible: LiveData<Boolean> = _isDeleteDialogVisible

    private val _selectedImages = MutableLiveData<List<AllImagesItem>>(emptyList())
    val selectedImages: LiveData<List<AllImagesItem>> = _selectedImages

    // Function to fetch the favorites for a user
    fun fetchFavorites(userId: Long) {
        viewModelScope.launch {
            try {
                // Fetch favorites from server and handle local caching
                val favoritesFromServer = repository.getFavoritesFromServer(userId)
                _favorites.postValue(favoritesFromServer)
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error fetching favorites: ${e.message}")
                // If fetching from server fails, fallback to cached favorites
                val cachedFavorites = repository.getCachedFavorites(userId)
                _favorites.postValue(cachedFavorites)
            }
        }
    }

//    // Function to fetch favorites for a Google user using the stored user ID
//    fun fetchFavoritesForGoogleUser(context: Context) {
//        val userRepository = UserRepository(context)
//        val userId = userRepository.getSavedGoogleUserId()
//
//        if (userId != null) {
//            Log.d("FavoritesViewModel", "Fetching favorites for local Google user ID: $userId")
//            viewModelScope.launch {
//                try {
//                    val favorites = repository.getCachedFavorites(userId)
//                    _favorites.postValue(favorites)
//                    Log.d("FavoritesViewModel", "Fetched local favorites: $favorites")
//                } catch (e: Exception) {
//                    Log.e("FavoritesViewModel", "Error fetching local favorites: ${e.message}")
//                }
//            }
//        } else {
//            Log.e("FavoritesViewModel", "Saved Google user ID is null or invalid")
//        }
//    }

    // Function to add an image to the user's favorites
    fun addFavorite(imageId: Long, imageUrl: String, description: String, userId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.addFavorite(imageId, userId, imageUrl, description)
                if (result.isSuccess) {
                    fetchFavorites(userId)  // Refresh the favorites list after adding
                } else {
                    Log.e("FavoritesViewModel", "Failed to add favorite: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error adding favorite: ${e.message}")
            }
        }
    }

    // Function to remove a specific favorite
    fun removeFavorite(userId: Long, imageId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.removeFavorite(userId, imageId)
                if (result.isSuccess) {
                    delay(200)  // Delay to ensure DB update is complete
                    fetchFavorites(userId)  // Refresh the favorites list after deletion
                } else {
                    Log.e("FavoritesViewModel", "Failed to remove favorite: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error removing favorite: ${e.message}")
            }
        }
    }

    // Function to check if an image is already in the user's favorites
    fun isFavorite(imageId: Long): Boolean {
        return runBlocking {
            repository.isFavoriteCached(imageId)
        }
    }

    // Function to toggle the selection status of an image for bulk deletion
    fun toggleSelection(image: AllImagesItem) {
        val currentSelection = _selectedImages.value ?: emptyList()
        _selectedImages.value = if (currentSelection.contains(image)) {
            currentSelection - image
        } else {
            currentSelection + image
        }
    }

    // Function to show the bulk delete confirmation dialog
    fun showBulkDeleteDialog() {
        _isDeleteDialogVisible.value = true
    }

    // Function to hide the delete confirmation dialog
    fun hideDeleteDialog() {
        _isDeleteDialogVisible.value = false
    }

    // Function to perform bulk deletion of selected favorites
    fun bulkDeleteSelectedFavorites(userId: Long) {
        val imagesToDelete = _selectedImages.value ?: return
        viewModelScope.launch {
            try {
                imagesToDelete.forEach { image ->
                    repository.removeFavorite(userId, image.allImagesId.toLong())
                }
                fetchFavorites(userId)  // Refresh after bulk delete
                _selectedImages.value = emptyList()  // Clear selection
                hideDeleteDialog()  // Hide the delete dialog
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error deleting favorites: ${e.message}")
            }
        }
    }
}
