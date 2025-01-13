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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class FavoritesViewModel(private val repository: FavoritesRepository,     private val userRepository: UserRepository) : ViewModel() {

    private val _itemToDelete = MutableLiveData<AllImagesItem?>()
    val itemToDelete: LiveData<AllImagesItem?> = _itemToDelete

    private val _favorites = MutableLiveData<List<AllImagesItem>>()
    val favorites: LiveData<List<AllImagesItem>> = _favorites

    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isDeleteDialogVisible = MutableLiveData<Boolean>()
    val isDeleteDialogVisible: LiveData<Boolean> = _isDeleteDialogVisible

    private val _selectedImages = MutableLiveData<List<AllImagesItem>>(emptyList())
    val selectedImages: LiveData<List<AllImagesItem>> = _selectedImages
    init {
        initializeUserFavorites()
    }
    private fun isValidUserId(userId: Long?): Boolean {
        return userId != null && userId > 0
    }


    // Fix for initializeUserFavorites()
    private fun initializeUserFavorites() {
        viewModelScope.launch {
            try {
                val userId = userRepository.getUserId()
                if (isValidUserId(userId)) {
                    fetchFavorites(userId)
                } else {
                    _favorites.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error initializing favorites", e)
                _favorites.postValue(emptyList())
            }
        }
    }

    // Fix: Modified version that doesn't use invalid userId
    suspend fun clearFavoritesOnLogout() {
        _favorites.postValue(emptyList())
        _selectedImages.postValue(emptyList())
        // No need to call repository.clearFavoritesForUser with invalid ID
    }

    // Update the fetchFavorites signature to accept nullable Long
    fun fetchFavorites(userId: Long?) {
        if (userId == null || !isValidUserId(userId)) {
            Log.e("FavoritesViewModel", "Invalid userId: $userId. Skipping fetch.")
            return
        }
        viewModelScope.launch {
            try {
                repository.refreshFavoritesForUser(userId)
                val cachedFavorites = repository.getCachedFavorites(userId)
                _favorites.postValue(cachedFavorites)
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error fetching favorites: ${e.message}")
            }
        }
    }

    fun addFavorite(imageId: Long, imageUrl: String, description: String) {
        val userId = userRepository.getUserId()
        if (isValidUserId(userId)) {
            viewModelScope.launch {
                try {
                    val result = repository.addFavorite(imageId, userId!!, imageUrl, description)
                    if (result.isSuccess) fetchFavorites(userId)
                } catch (e: Exception) {
                    Log.e("FavoritesViewModel", "Error adding favorite: ${e.message}")
                }
            }
        } else {
            Log.e("FavoritesViewModel", "Cannot add favorite. Invalid userId.")
        }
    }

    fun removeFavorite(imageId: Long) {
        val userId = userRepository.getUserId()
        if (isValidUserId(userId)) {
            viewModelScope.launch {
                try {
                    val result = repository.removeFavorite(userId!!, imageId)
                    if (result.isSuccess) fetchFavorites(userId)
                } catch (e: Exception) {
                    Log.e("FavoritesViewModel", "Error removing favorite: ${e.message}")
                }
            }
        } else {
            Log.e("FavoritesViewModel", "Cannot remove favorite. Invalid userId.")
        }
    }

    fun isFavorite(imageId: Long): Boolean {
        return runBlocking {
            repository.isFavoriteCached(imageId)
        }
    }

    fun toggleSelection(image: AllImagesItem) {
        val currentSelection = _selectedImages.value ?: emptyList()
        _selectedImages.value = if (currentSelection.contains(image)) {
            currentSelection - image
        } else {
            currentSelection + image
        }
    }

    fun showBulkDeleteDialog() {
        _isDeleteDialogVisible.value = true
    }

    fun hideDeleteDialog() {
        _isDeleteDialogVisible.value = false
    }

    // Fix for bulkDeleteSelectedFavorites()
    fun bulkDeleteSelectedFavorites() {
        val userId = userRepository.getUserId()
        if (userId == null || !isValidUserId(userId)) {
            Log.e("FavoritesViewModel", "Cannot perform bulk delete. Invalid userId.")
            return
        }

        val imagesToDelete = _selectedImages.value ?: return
        viewModelScope.launch {
            try {
                imagesToDelete.forEach { image ->
                    repository.removeFavorite(userId, image.allImagesId.toLong())
                }
                fetchFavorites(userId)
                _selectedImages.value = emptyList()
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error during bulk deletion: ${e.message}")
            }
        }
    }



//    suspend fun clearFavoritesOnLogout() {
//        _favorites.value = emptyList() // Reset LiveData
//        _selectedImages.value = emptyList() // Reset selections
//        repository.clearFavoritesForUser(-1) // Ensure no residual local favorites exist
//        userRepository.logout() // Reset user session
//    }


//
//    fun fetchFavorites(userId: Long) {
//        if (userId <= 0) {
//            Log.e("FavoritesViewModel", "Invalid userId: $userId. Skipping fetch.")
//            return
//        }
//        viewModelScope.launch {
//            try {
//                repository.refreshFavoritesForUser(userId)
//                val cachedFavorites = repository.getCachedFavorites(userId)
//                _favorites.postValue(cachedFavorites)
//            } catch (e: Exception) {
//                Log.e("FavoritesViewModel", "Error fetching favorites: ${e.message}")
//            }
//        }
//    }
//
//
//
//    fun addFavorite(imageId: Long, imageUrl: String, description: String) {
//        val userId = userRepository.getUserId()
//        if (userId > 0) {
//            viewModelScope.launch {
//                try {
//                    val result = repository.addFavorite(imageId, userId, imageUrl, description)
//                    if (result.isSuccess) fetchFavorites(userId)
//                } catch (e: Exception) {
//                    Log.e("FavoritesViewModel", "Error adding favorite: ${e.message}")
//                }
//            }
//        }
//    }
//
//    fun removeFavorite(imageId: Long) {
//        val userId = userRepository.getUserId()
//        if (userId > 0) {
//            viewModelScope.launch {
//                try {
//                    val result = repository.removeFavorite(userId, imageId)
//                    if (result.isSuccess) fetchFavorites(userId)
//                } catch (e: Exception) {
//                    Log.e("FavoritesViewModel", "Error removing favorite: ${e.message}")
//                }
//            }
//        }
//    }
//
//    // Function to check if an image is already in the user's favorites
//    fun isFavorite(imageId: Long): Boolean {
//        return runBlocking {
//            repository.isFavoriteCached(imageId)
//        }
//    }
//
//    // Function to toggle the selection status of an image for bulk deletion
//    fun toggleSelection(image: AllImagesItem) {
//        val currentSelection = _selectedImages.value ?: emptyList()
//        _selectedImages.value = if (currentSelection.contains(image)) {
//            currentSelection - image
//        } else {
//            currentSelection + image
//        }
//    }
//
//    // Function to show the bulk delete confirmation dialog
//    fun showBulkDeleteDialog() {
//        _isDeleteDialogVisible.value = true
//    }
//
//    // Function to hide the delete confirmation dialog
//    fun hideDeleteDialog() {
//        _isDeleteDialogVisible.value = false
//    }
//
//    // Function to perform bulk deletion of selected favorites
//    fun bulkDeleteSelectedFavorites() {
//        val userId = userRepository.getUserId()
//        if (userId > 0) {
//            val imagesToDelete = _selectedImages.value ?: return
//            viewModelScope.launch {
//                try {
//                    imagesToDelete.forEach { image ->
//                        repository.removeFavorite(userId, image.allImagesId.toLong())
//                    }
//                    fetchFavorites(userId)
//                    _selectedImages.value = emptyList() // Clear selections
//                } catch (e: Exception) {
//                    Log.e("FavoritesViewModel", "Error during bulk deletion: ${e.message}")
//                }
//            }
//        }
//    }
}
