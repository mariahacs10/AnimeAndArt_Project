package com.example.practice_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practice_app.db.AppDatabase
import com.example.practice_app.db.RetrofitClient
import com.example.practice_app.models.FavoritesRepository
import com.example.practice_app.models.FavoritesViewModel
import com.example.practice_app.models.FavoritesViewModelFactory
import com.example.practice_app.models.ForgotPasswordViewModel
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.models.UserViewModelFactory
import com.example.practice_app.navigation.NavRoutes
import com.example.practice_app.screen.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.runBlocking

/**
 * Main entry point of the application, handling lifecycle and navigation
 * Implements Jetpack Compose UI with Material3 theming
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dependency initialization with manual DI
        val database = AppDatabase.getDatabase(applicationContext)
        val favoriteImageDao = database.favoriteImageDao()
        val apiService = RetrofitClient.createApiService
        val userRepository = UserRepository(applicationContext, favoriteImageDao)
        val favoritesRepository = FavoritesRepository(apiService, favoriteImageDao, userRepository)

        // ViewModel factory setup for dependency injection
        val userViewModelFactory = UserViewModelFactory(userRepository, favoritesRepository)
        val favoritesViewModelFactory = FavoritesViewModelFactory(apiService, favoriteImageDao, userRepository)
        val userViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)

        // Compose UI setup with state management
        setContent {
            // State collection using Compose state holders
            val isDarkModeEnabled by userViewModel.isDarkModeEnabled.collectAsState()
            val userState by userRepository.userState.collectAsState()
            val (userId, isUserLoggedIn) = userState

            // Session corruption handler
            LaunchedEffect(userState) {
                if (isUserLoggedIn && (userId == null || userId <= 0)) {
                    Log.e("MainActivity", "Corrupted session detected. Logging out.")
                    userRepository.logout()
                }
            }

            // Theme configuration with dark mode support
            MaterialTheme(
                colorScheme = if (isDarkModeEnabled) darkColorScheme() else lightColorScheme()
            ) {
                StatusBarColor(isDarkModeEnabled)

                // Navigation destination logic
                val (startDestination, validUserId) = when {
                    isUserLoggedIn && userId != null && userId > 0 -> "home_screen" to userId
                    else -> "login_screen" to null
                }

                // Main navigation setup
                HomeNavigation(
                    userViewModel = userViewModel,
                    startDestination = startDestination,
                    userId = validUserId,
                    favoritesViewModelFactory = favoritesViewModelFactory
                )
            }
        }
    }
}

// Status bar customization with system UI controller
@Composable
fun StatusBarColor(isDarkModeEnabled: Boolean) {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = if (isDarkModeEnabled) Color.Black else Color(0xFF6650a4)

    // Cleanup effect for system UI changes
    DisposableEffect(systemUiController, statusBarColor) {
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = !isDarkModeEnabled)
        onDispose {}
    }
}
// Main navigation component with route handling
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavigation(
    userViewModel: UserViewModel,
    startDestination: String,
    userId: Long?,
    favoritesViewModelFactory: FavoritesViewModelFactory
) {
    // Navigation controller setup
    val navController = rememberNavController()
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = favoritesViewModelFactory)

    // Navigation host with route definitions
    NavHost(navController = navController, startDestination = startDestination) {
        // Route configurations with session validation
        composable("login_screen") {
            LoginScreen(navController = navController, userViewModel, favoritesViewModel)
        }
        // Other route definitions...
        composable(
            route = NavRoutes.ImageDetail.route,
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("imageId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val imageId = backStackEntry.arguments?.getLong("imageId") ?: 0L

            // Only show ImageDetailScreen if we have a valid userId
            if (userId != null && userId > 0) {
                ImageDetailScreen(
                    imageUrl = imageUrl,
                    description = description,
                    imageId = imageId,
                    userId = userId,  // Pass the non-null userId
                    viewModel = favoritesViewModel
                ) {
                    navController.popBackStack()
                }
            } else {
                // Handle invalid user state - redirect to login
                LaunchedEffect(Unit) {
                    navController.navigate("login_screen") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }



        composable("favorites_screen") {
            if (userId != null && userId > 0) {
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    userId = userId,  // Pass the non-null userId
                    navController = navController,
                    drawerState = drawerState
                )
            } else {
                // Handle invalid user state - redirect to login
                LaunchedEffect(Unit) {
                    navController.navigate("login_screen") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }

        // Update favorite image detail route
        composable("favoriteImageDetail/{imageUrl}/{imageId}") { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

            FavoriteImageDetailScreen(
                imageUrl = imageUrl,
                onBack = { navController.popBackStack() }
            )
        }

        // Other routes remain the same...
        composable("signup_screen") {
            SignUpScreen(navController = navController, userViewModel)
        }
        composable("home_screen") {
            HomeScreen(navController, userViewModel, favoritesViewModel)
        }
        composable(NavRoutes.AnimeConvention.route) {
            AnimeConventionComposable(navController = navController)
        }
        composable(NavRoutes.ErikasArtWork.route) {
            ErikasArtWorkComposable(navController = navController)
        }
        composable(NavRoutes.CommingSoon.route) {
            CommingSoonComposable()
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("settings_screen") {
            SettingsScreen(navController, userViewModel, drawerState)
        }
    }
}