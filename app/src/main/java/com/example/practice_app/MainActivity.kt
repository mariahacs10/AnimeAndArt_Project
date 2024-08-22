package com.example.practice_app


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.navigation.NavRoutes
import com.example.practice_app.screen.AllImagesComposable
import com.example.practice_app.screen.AnimeConventionComposable
import com.example.practice_app.screen.CommingSoonComposable
import com.example.practice_app.screen.ErikasArtWorkComposable
import com.example.practice_app.screen.HomeScreen
import com.example.practice_app.screen.ImageDetailScreen
import com.example.practice_app.screen.LoginScreen
import com.example.practice_app.screen.SignUpScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.net.URLDecoder


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create an instance of the UserRepository
        val repository = UserRepository(applicationContext)

        // Create a ViewModelFactory to provide the repository to the ViewModel
        val viewModelFactory = UserViewModelFactory(repository)

        // Obtain the UserViewModel instance using the ViewModelProvider
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(UserViewModel::class.java)

        // Set the content of the activity using a composable function
        setContent {
            //add a check for Google Sign-In state
            val isUserLoggedIn = repository.isUserLoggedIn()
            val googleAccount = GoogleSignIn.getLastSignedInAccount(this)

            val startDestination = when {
                isUserLoggedIn -> "home_screen"
                googleAccount != null -> "home_screen"
                else -> "login_screen"
            }

            HomeNavigation(viewModel, startDestination)
        }
    }
}


// UserViewModelFactory is a factory class for creating instances of UserViewModel
class UserViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    // Override the create function to create and return a UserViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Create a new instance of UserViewModel with the injected repository and cast it to the requested model class
        return UserViewModel(repository) as T
    }
}

// HomeNavigation is a composable function that sets up the navigation graph
@Composable
fun HomeNavigation(viewModel: UserViewModel, startDestination: String) {
    // Create a NavController using the rememberNavController function
    val navController = rememberNavController()

    // Create a NavHost with the specified startDestination
    NavHost(navController = navController, startDestination = startDestination) {
        // Define a composable for the login screen
        composable("login_screen") {
            // Call the LoginScreen composable function with the navController and viewModel
            LoginScreen(navController = navController, viewModel)
        }
        // Define a composable for the signup screen
        composable("signup_screen") {
            // Call the SignUpScreen composable function with the navController and viewModel
            SignUpScreen(navController = navController, viewModel)
        }
        // Define a composable for the home screen
        composable("home_screen") {
            // Call the HomeScreen composable function with the navController and viewModel
            HomeScreen(navController, viewModel)
        }
        composable(NavRoutes.AnimeConvention.route) {
            AnimeConventionComposable()
        }
        composable(NavRoutes.ErikasArtWork.route) {
            ErikasArtWorkComposable()
        }
        composable(NavRoutes.CommingSoon.route) {
            CommingSoonComposable()
        }
        composable(
            route = NavRoutes.ImageDetail.route,
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedImageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val encodedDescription = backStackEntry.arguments?.getString("description") ?: ""
            val imageUrl = URLDecoder.decode(encodedImageUrl, "UTF-8")
            val description = URLDecoder.decode(encodedDescription, "UTF-8")
            ImageDetailScreen(
                imageUrl = imageUrl,
                description = description,
                onBack = { navController.popBackStack() }
            )
        }
    }
}