package com.example.practice_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.screen.HomeScreen
import com.example.practice_app.screen.LoginScreen
import com.example.practice_app.screen.SignUpScreen


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
            // Check if the user is logged in using the repository
            val isUserLoggedIn = repository.isUserLoggedIn()
            // Determine the start destination based on the login state
            val startDestination = if (isUserLoggedIn) "home_screen" else "login_screen"
            // Call the HomeNavigation composable function with the viewModel and startDestination
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
    }
}