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



/**
 * ViewModel managing favorites data and UI state for favorite images
 * Implements MVVM pattern with LiveData and StateFlow
 */
class FavoritesViewModel(private val repository: FavoritesRepository, private val userRepository: UserRepository) : ViewModel() {

    // UI state holders for delete operations
    private val _itemToDelete = MutableLiveData<AllImagesItem?>()
    val itemToDelete: LiveData<AllImagesItem?> = _itemToDelete

    // State management for favorites list
    private val _favorites = MutableLiveData<List<AllImagesItem>>()
    val favorites: LiveData<List<AllImagesItem>> = _favorites

    // Favorite status state using Kotlin Flow
    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Dialog visibility state
    private val _isDeleteDialogVisible = MutableLiveData<Boolean>()
    val isDeleteDialogVisible: LiveData<Boolean> = _isDeleteDialogVisible

    // Multi-select state management
    private val _selectedImages = MutableLiveData<List<AllImagesItem>>(emptyList())
    val selectedImages: LiveData<List<AllImagesItem>> = _selectedImages

    // Initialize favorites on ViewModel creation
    init {
        initializeUserFavorites()
    }

    // User ID validation helper
    private fun isValidUserId(userId: Long?): Boolean {
        return userId != null && userId > 0
    }

    // Safe initialization of user favorites
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

    // Cleanup handler for logout
    suspend fun clearFavoritesOnLogout() {
        _favorites.postValue(emptyList())
        _selectedImages.postValue(emptyList())
    }

    // Fetches favorites with null safety
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

    // Adds favorite with user validation
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

    // Removes favorite with error handling
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
        }
    }

    // Synchronous favorite check
    fun isFavorite(imageId: Long): Boolean {
        return runBlocking {
            repository.isFavoriteCached(imageId)
        }
    }

    // Multi-select image handling
    fun toggleSelection(image: AllImagesItem) {
        val currentSelection = _selectedImages.value ?: emptyList()
        _selectedImages.value = if (currentSelection.contains(image)) {
            currentSelection - image
        } else {
            currentSelection + image
        }
    }

    // Dialog visibility controls
    fun showBulkDeleteDialog() {
        _isDeleteDialogVisible.value = true
    }

    fun hideDeleteDialog() {
        _isDeleteDialogVisible.value = false
    }

    // Bulk delete operation with null safety
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
}

//// The FavoritesViewModel class is responsible for managing the favorites data and actions for the UI.
//// It interacts with the FavoritesRepository to fetch, add, and remove favorite images.
//// This class extends ViewModel, allowing it to survive configuration changes.
//class FavoritesViewModel(private val repository: FavoritesRepository,     private val userRepository: UserRepository) : ViewModel() {
//
//    private val _itemToDelete = MutableLiveData<AllImagesItem?>()
//    val itemToDelete: LiveData<AllImagesItem?> = _itemToDelete
//
//    private val _favorites = MutableLiveData<List<AllImagesItem>>()
//    val favorites: LiveData<List<AllImagesItem>> = _favorites
//
//    private val _isFavorite = MutableStateFlow<Boolean>(false)
//    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
//
//    private val _isDeleteDialogVisible = MutableLiveData<Boolean>()
//    val isDeleteDialogVisible: LiveData<Boolean> = _isDeleteDialogVisible
//
//    private val _selectedImages = MutableLiveData<List<AllImagesItem>>(emptyList())
//    val selectedImages: LiveData<List<AllImagesItem>> = _selectedImages
//    init {
//        initializeUserFavorites()
//    }
//    private fun isValidUserId(userId: Long?): Boolean {
//        return userId != null && userId > 0
//    }
//
//
//    // Fix for initializeUserFavorites()
//    private fun initializeUserFavorites() {
//        viewModelScope.launch {
//            try {
//                val userId = userRepository.getUserId()
//                if (isValidUserId(userId)) {
//                    fetchFavorites(userId)
//                } else {
//                    _favorites.postValue(emptyList())
//                }
//            } catch (e: Exception) {
//                Log.e("FavoritesViewModel", "Error initializing favorites", e)
//                _favorites.postValue(emptyList())
//            }
//        }
//    }
//
//    // Fix: Modified version that doesn't use invalid userId
//    suspend fun clearFavoritesOnLogout() {
//        _favorites.postValue(emptyList())
//        _selectedImages.postValue(emptyList())
//        // No need to call repository.clearFavoritesForUser with invalid ID
//    }
//
//    // Update the fetchFavorites signature to accept nullable Long
//    fun fetchFavorites(userId: Long?) {
//        if (userId == null || !isValidUserId(userId)) {
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
//    fun addFavorite(imageId: Long, imageUrl: String, description: String) {
//        val userId = userRepository.getUserId()
//        if (isValidUserId(userId)) {
//            viewModelScope.launch {
//                try {
//                    val result = repository.addFavorite(imageId, userId!!, imageUrl, description)
//                    if (result.isSuccess) fetchFavorites(userId)
//                } catch (e: Exception) {
//                    Log.e("FavoritesViewModel", "Error adding favorite: ${e.message}")
//                }
//            }
//        } else {
//            Log.e("FavoritesViewModel", "Cannot add favorite. Invalid userId.")
//        }
//    }
//
//    fun removeFavorite(imageId: Long) {
//        val userId = userRepository.getUserId()
//        if (isValidUserId(userId)) {
//            viewModelScope.launch {
//                try {
//                    val result = repository.removeFavorite(userId!!, imageId)
//                    if (result.isSuccess) fetchFavorites(userId)
//                } catch (e: Exception) {
//                    Log.e("FavoritesViewModel", "Error removing favorite: ${e.message}")
//                }
//            }
//        } else {
//            Log.e("FavoritesViewModel", "Cannot remove favorite. Invalid userId.")
//        }
//    }
//
//    fun isFavorite(imageId: Long): Boolean {
//        return runBlocking {
//            repository.isFavoriteCached(imageId)
//        }
//    }
//
//    fun toggleSelection(image: AllImagesItem) {
//        val currentSelection = _selectedImages.value ?: emptyList()
//        _selectedImages.value = if (currentSelection.contains(image)) {
//            currentSelection - image
//        } else {
//            currentSelection + image
//        }
//    }
//
//    fun showBulkDeleteDialog() {
//        _isDeleteDialogVisible.value = true
//    }
//
//    fun hideDeleteDialog() {
//        _isDeleteDialogVisible.value = false
//    }
//
//    // Fix for bulkDeleteSelectedFavorites()
//    fun bulkDeleteSelectedFavorites() {
//        val userId = userRepository.getUserId()
//        if (userId == null || !isValidUserId(userId)) {
//            Log.e("FavoritesViewModel", "Cannot perform bulk delete. Invalid userId.")
//            return
//        }
//
//        val imagesToDelete = _selectedImages.value ?: return
//        viewModelScope.launch {
//            try {
//                imagesToDelete.forEach { image ->
//                    repository.removeFavorite(userId, image.allImagesId.toLong())
//                }
//                fetchFavorites(userId)
//                _selectedImages.value = emptyList()
//            } catch (e: Exception) {
//                Log.e("FavoritesViewModel", "Error during bulk deletion: ${e.message}")
//            }
//        }
//    }
//
//
//
////    suspend fun clearFavoritesOnLogout() {
////        _favorites.value = emptyList() // Reset LiveData
////        _selectedImages.value = emptyList() // Reset selections
////        repository.clearFavoritesForUser(-1) // Ensure no residual local favorites exist
////        userRepository.logout() // Reset user session
////    }
//
//
////
////    fun fetchFavorites(userId: Long) {
////        if (userId <= 0) {
////            Log.e("FavoritesViewModel", "Invalid userId: $userId. Skipping fetch.")
////            return
////        }
////        viewModelScope.launch {
////            try {
////                repository.refreshFavoritesForUser(userId)
////                val cachedFavorites = repository.getCachedFavorites(userId)
////                _favorites.postValue(cachedFavorites)
////            } catch (e: Exception) {
////                Log.e("FavoritesViewModel", "Error fetching favorites: ${e.message}")
////            }
////        }
////    }
////
////
////
////    fun addFavorite(imageId: Long, imageUrl: String, description: String) {
////        val userId = userRepository.getUserId()
////        if (userId > 0) {
////            viewModelScope.launch {
////                try {
////                    val result = repository.addFavorite(imageId, userId, imageUrl, description)
////                    if (result.isSuccess) fetchFavorites(userId)
////                } catch (e: Exception) {
////                    Log.e("FavoritesViewModel", "Error adding favorite: ${e.message}")
////                }
////            }
////        }
////    }
////
////    fun removeFavorite(imageId: Long) {
////        val userId = userRepository.getUserId()
////        if (userId > 0) {
////            viewModelScope.launch {
////                try {
////                    val result = repository.removeFavorite(userId, imageId)
////                    if (result.isSuccess) fetchFavorites(userId)
////                } catch (e: Exception) {
////                    Log.e("FavoritesViewModel", "Error removing favorite: ${e.message}")
////                }
////            }
////        }
////    }
////
////    // Function to check if an image is already in the user's favorites
////    fun isFavorite(imageId: Long): Boolean {
////        return runBlocking {
////            repository.isFavoriteCached(imageId)
////        }
////    }
////
////    // Function to toggle the selection status of an image for bulk deletion
////    fun toggleSelection(image: AllImagesItem) {
////        val currentSelection = _selectedImages.value ?: emptyList()
////        _selectedImages.value = if (currentSelection.contains(image)) {
////            currentSelection - image
////        } else {
////            currentSelection + image
////        }
////    }
////
////    // Function to show the bulk delete confirmation dialog
////    fun showBulkDeleteDialog() {
////        _isDeleteDialogVisible.value = true
////    }
////
////    // Function to hide the delete confirmation dialog
////    fun hideDeleteDialog() {
////        _isDeleteDialogVisible.value = false
////    }
////
////    // Function to perform bulk deletion of selected favorites
////    fun bulkDeleteSelectedFavorites() {
////        val userId = userRepository.getUserId()
////        if (userId > 0) {
////            val imagesToDelete = _selectedImages.value ?: return
////            viewModelScope.launch {
////                try {
////                    imagesToDelete.forEach { image ->
////                        repository.removeFavorite(userId, image.allImagesId.toLong())
////                    }
////                    fetchFavorites(userId)
////                    _selectedImages.value = emptyList() // Clear selections
////                } catch (e: Exception) {
////                    Log.e("FavoritesViewModel", "Error during bulk deletion: ${e.message}")
////                }
////            }
////        }
////    }
//}
