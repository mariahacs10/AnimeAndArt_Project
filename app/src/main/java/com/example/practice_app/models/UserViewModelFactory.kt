package com.example.practice_app.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practice_app.db.FavoriteImageDao

class UserViewModelFactory(
    private val repository: UserRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository, favoritesRepository) as T
    }
}