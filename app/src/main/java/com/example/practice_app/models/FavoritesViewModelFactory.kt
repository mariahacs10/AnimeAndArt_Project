package com.example.practice_app.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practice_app.db.ApiService
import com.example.practice_app.db.FavoriteImageDao

// FavoritesViewModelFactory is a ViewModelFactory responsible for creating instances of FavoritesViewModel.
// It allows for the injection of dependencies (ApiService and FavoriteImageDao) into the ViewModel.
// ViewModelProvider.Factory is used to create ViewModel instances in a lifecycle-aware manner.
class FavoritesViewModelFactory(
    private val apiService: ApiService,
    private val favoriteImageDao: FavoriteImageDao, // Inject DAO
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    // Override the create method to provide the ViewModel instance.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the modelClass is assignable to FavoritesViewModel.
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            // If so, create an instance of FavoritesRepository using the provided apiService and favoriteImageDao.
            val repository = FavoritesRepository(apiService, favoriteImageDao, userRepository)
            // Return the FavoritesViewModel, passing the repository into its constructor.
            @Suppress("UNCHECKED_CAST")
            // Cast to the expected ViewModel type.
            return FavoritesViewModel(repository, userRepository) as T
        }
        // If the modelClass is not assignable to FavoritesViewModel, throw an exception.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

