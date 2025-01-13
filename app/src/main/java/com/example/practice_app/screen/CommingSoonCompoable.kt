package com.example.practice_app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practice_app.R
import com.example.practice_app.db.AppDatabase
import com.example.practice_app.db.RetrofitClient
import com.example.practice_app.models.FavoritesRepository
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.models.UserViewModelFactory

@Composable
fun CommingSoonComposable() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val favoriteImageDao = database.favoriteImageDao()
    val apiService = RetrofitClient.createApiService
    val userRepository = UserRepository(context, favoriteImageDao)  // Ensure UserRepository is properly initialized
    val favoritesRepository = FavoritesRepository(apiService, favoriteImageDao, userRepository)

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository, favoritesRepository)
    )
    val isDarkTheme by userViewModel.isDarkModeEnabled.collectAsState()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val backgroundImage = if (isDarkTheme) {
            painterResource(id = R.drawable.darkness) // Replace with dark mode resource ID
        } else {
            painterResource(id = R.drawable.lightness) // Replace with light mode resource ID
        }

        Image(
            painter = backgroundImage, // Replace with the actual resource ID
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Comming soon Composable",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue  // Or use your theme color: MaterialTheme.colorScheme.primary
        )
    }
}