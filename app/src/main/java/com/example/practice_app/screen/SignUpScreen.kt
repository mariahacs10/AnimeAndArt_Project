package com.example.practice_app.screen

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practice_app.R
import com.example.practice_app.models.UserViewModel
import kotlinx.coroutines.launch


// Define a composable function for the sign-up screen
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    // Navigation controller for screen transitions
    navController: NavController,
    // UserViewModel to manage user data
    viewModel: UserViewModel
) {
    // Coroutine scope for asynchronous operations
    val coroutineScope = rememberCoroutineScope()

    // Read username, password and email state from viewModel,
    // and create local state for confirmed password and visibility
    var username by viewModel.username
    var password by viewModel.password
    var email by viewModel.email
    var confirmedPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()  // Fill the entire screen
    ) {

        Column(
            // Fill the entire screen
            modifier = Modifier.fillMaxSize(),
            // Center content vertically
            verticalArrangement = Arrangement.Center,
            // Center content horizontally
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the "Create Account" text
            Text(
                text = "Create Account",
                // Apply headlineMedium text style
                style = MaterialTheme.typography.headlineMedium
            )

            // Add vertical spacing
            Spacer(modifier = Modifier.height(16.dp))

            // Username field
            OutlinedTextField(
                value = username,
                // Update username state on change
                onValueChange = { username = it },
                // Label for the field
                label = { Text("Username") }
            )

            // Email input field
            OutlinedTextField(
                value = email,
                // Update username state on change
                onValueChange = { email = it },
                // Label for the field
                label = { Text("Enter email") }
            )

            // Add vertical spacing
            Spacer(modifier = Modifier.height(8.dp))

            //This is the rules / cases for the password
            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{8,15}$")

            // Choose icon based on password visibility
            val icon = if (passwordVisible) painterResource(id = R.drawable.eyeballnothidden) else painterResource(id = R.drawable.hiddeneye)
            OutlinedTextField(
                value = password,
                // Update password state on change
                onValueChange = { password = it },
                /// Toggle password visibility
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {  // Toggle visibility on button click
                        Icon(painter = icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                label = { Text("Password") }
            )


            Spacer(modifier = Modifier.height(8.dp))  // Add vertical spacing

            // Confirm password field with visibility toggle (similar to password field)
            val icon2 = if (confirmVisible) painterResource(id = R.drawable.eyeballnothidden) else painterResource(id = R.drawable.hiddeneye)
            OutlinedTextField(
                value = confirmedPassword,
                onValueChange = { confirmedPassword = it },
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(painter = icon2, contentDescription = "Toggle Password Visibility")
                    }
                },
                label = { Text("Confirm Password") }
            )

            Spacer(modifier = Modifier.height(16.dp))  // Add vertical spacing


            // Sign Up button
            Button(onClick = {

                // Prioritize password requirements check first
                if (!password.matches(passwordRegex)) {
                    Toast.makeText(context,"Requirements are: Lower Case, Uppercase, Special character, And a Number, and a Password 8 or Longer", Toast.LENGTH_LONG).show()
                }
                //this is the password checking
                if (password.length < 8 || password.length > 15 || confirmedPassword.length < 8 || confirmedPassword.length > 15) {
                    Toast.makeText(context, "Password must be between 8 and 15 characters", Toast.LENGTH_SHORT).show()
                } else if (password != confirmedPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else if (username.length < 6 || username.length > 10) {
                    Toast.makeText(context, "Please make username between 6 and 10 characters", Toast.LENGTH_SHORT).show()
                } else {
                    val toast = Toast.makeText(context, "Sign up Successful!", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM, 0,80)
                    toast.show()
                    coroutineScope.launch {
                        // Call the ViewModel function to handle sign-up with email
                        viewModel.signupUser(username, password, confirmedPassword, email)
                    }
                    // Navigate back after sign-up
                    navController.popBackStack()
                }
            }) {
                Text("Sign Up")
            }
        }
    }
}
