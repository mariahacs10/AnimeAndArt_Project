package com.example.practice_app.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.practice_app.models.UserViewModel
import kotlinx.coroutines.launch

// Declare the HomeScreen composable function
@Composable
fun HomeScreen(navController: NavController, viewModel: UserViewModel) {
    // Declare a mutable state variable for the username
    var username by remember { mutableStateOf("") }

    // Get the current coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Use LaunchedEffect to perform side effects when the composable is launched
    LaunchedEffect(Unit) {
        // Launch a coroutine within the current scope
        coroutineScope.launch {
            // Get the logged-in user from the view model
            val user = viewModel.getLoggedInUser()
            // Check if the user is not null
            if (user != null) {
                // Set the username to the user's username
                username = user.username
            }
        }
    }

    // Create a Box with a modifier to fill the maximum available size and center its content
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Create a Column with horizontally centered content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Display a welcome message with the username
            Text(text = "Welcome, $username", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Add a vertical space of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Create a Button for the logout action
            Button(onClick = {
                // Launch a coroutine within the current scope
                coroutineScope.launch {
                    // Call the logoutUser function from the view model with the username
                    viewModel.logoutUser(username)
                    // Navigate to the login screen and pop up to the home screen (inclusive)
                    navController.navigate("login_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                }
            }) {
                // Display the text "Logout" on the button
                Text("Logout")
            }
        }
    }
}