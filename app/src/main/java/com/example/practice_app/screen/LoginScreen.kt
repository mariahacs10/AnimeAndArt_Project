package com.example.practice_app.screen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practice_app.R
import com.example.practice_app.models.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

// Opt-in to the ExperimentalMaterial3Api annotation
@OptIn(ExperimentalMaterial3Api::class)
// Define a composable function named LoginScreen
@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {
    // Declare state variables for username, password, and password visibility
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Get a reference to the current coroutine scope
    val coroutineScope = rememberCoroutineScope()
    // Get a reference to the current context
    val context = LocalContext.current

    // Create a Box with a modifier to fill the maximum available size
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Create a Column with a modifier to fill the maximum available size, center vertically and horizontally
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display a text for the login title
            Text(text = "Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Add a vertical space of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Create an OutlinedTextField for the username input
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") }
            )

            // Add a vertical space of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Determine the icon based on password visibility
            val icon = if (passwordVisible)
                painterResource(id = R.drawable.eyeballnothidden)
            else
                painterResource(id = R.drawable.hiddeneye)

            // Create an OutlinedTextField for the password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    // Create an IconButton to toggle password visibility
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(painter = icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                label = { Text(text = "Password") },
            )

            // Add a vertical space of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Create a TextButton for the forgot password action
            TextButton(onClick = {
                // Handle forgot password action here if needed
                navController.navigate("forgotPassword")
            }) {
                Text("Forgot Password?")
            }

            // Add a vertical space of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                coroutineScope.launch {
                    if (username.isEmpty()) {
                        Toast.makeText(context, "Please fill out your username", Toast.LENGTH_SHORT).show()
                    } else if (password.isEmpty()) {
                        Toast.makeText(context, "Please fill out your password", Toast.LENGTH_SHORT).show()
                    } else {
                        val loginSuccess = viewModel.loginWithCredentials(username, password)
                        if (loginSuccess) {
                            viewModel.updateUsername(username) // Update the username in ViewModel
                            navController.navigate("home_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Text(text = "LOGIN")
            }

            // Add a vertical space of 16dp
            Spacer(modifier = Modifier.height(16.dp))

//            val gso = remember {
//                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestEmail()
//                    .build()
//            }
//
//            val signInLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    try {
//                        viewModel.loginWithGoogle(context)
//                        navController.navigate("home_screen") {
//                            popUpTo("login_screen") { inclusive = true }
//                        }
//                    } catch (e: ApiException) {
//                        Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            val googleSignInClient = remember {
//                GoogleSignIn.getClient(context, gso)
//            }
            // Google sign-in button
            Button(onClick = {
            }) {
                // Google icon
                Icon(
                    painter = painterResource(id = R.drawable.googleicon),
                    contentDescription = "Google",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Sign in with Google")
            }


            // Add a vertical space of 50dp
            Spacer(modifier = Modifier.padding(top = 50.dp))

            // Display a text for the sign up prompt
            Text(text = "Don't have an account?")

            // Create a TextButton for the sign up action
            TextButton(onClick = { navController.navigate("signup_screen") }) {
                Text(text = "Sign up", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}