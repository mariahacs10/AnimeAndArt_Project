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
//@SuppressLint("SuspiciousIndentation")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SignUpScreen(
//    navController: NavController,
//    viewModel: UserViewModel
//) {
//    val coroutineScope = rememberCoroutineScope()
//
//    var username by viewModel.username
//    var password by viewModel.password
//    var email by viewModel.email
//    var confirmedPassword by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//    var confirmVisible by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Create Account",
//                style = MaterialTheme.typography.headlineMedium
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = username,
//                onValueChange = { username = it },
//                label = { Text("Username") }
//            )
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Enter email") }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{8,15}$")
//
//            val icon = if (passwordVisible) painterResource(id = R.drawable.eyeballnothidden) else painterResource(id = R.drawable.hiddeneye)
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                        Icon(painter = icon, contentDescription = "Toggle Password Visibility")
//                    }
//                },
//                label = { Text("Password") }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            val icon2 = if (confirmVisible) painterResource(id = R.drawable.eyeballnothidden) else painterResource(id = R.drawable.hiddeneye)
//            OutlinedTextField(
//                value = confirmedPassword,
//                onValueChange = { confirmedPassword = it },
//                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
//                        Icon(painter = icon2, contentDescription = "Toggle Password Visibility")
//                    }
//                },
//                label = { Text("Confirm Password") }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = {
//                if (!password.matches(passwordRegex)) {
//                    Toast.makeText(context, "Password must include a lowercase, uppercase, number, special character, and be 8-15 characters long.", Toast.LENGTH_LONG).show()
//                } else if (password != confirmedPassword) {
//                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
//                } else if (username.length !in 6..10) {
//                    Toast.makeText(context, "Username must be between 6 and 10 characters", Toast.LENGTH_SHORT).show()
//                } else {
//                    coroutineScope.launch {
//                        val signupSuccess = viewModel.signupUser(username, password, confirmedPassword, email)
//                        if (signupSuccess) {
//                            // Clear input fields after successful signup
//                            username = ""
//                            password = ""
//                            email = ""
//                            confirmedPassword = ""
//
//                            // Show success message
//                            Toast.makeText(context, "Signup Successful! Redirecting to Login...", Toast.LENGTH_SHORT).show()
//
//                            // Navigate back to the login screen
//                            navController.popBackStack()
//                        } else {
//                            Toast.makeText(context, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }) {
//                Text("Sign Up")
//            }
//        }
//    }
//}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var username by viewModel.username
    var password by viewModel.password
    var email by viewModel.email
    var confirmedPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter email") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{8,15}$")

            val icon = if (passwordVisible) painterResource(id = R.drawable.eyeballnothidden) else painterResource(id = R.drawable.hiddeneye)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                label = { Text("Password") }
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                when {
                    !password.matches(passwordRegex) -> {
                        Toast.makeText(
                            context,
                            "Password must include a lowercase, uppercase, number, special character, and be 8-15 characters long.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    password != confirmedPassword -> {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    username.length !in 6..10 -> {
                        Toast.makeText(context, "Username must be between 6 and 10 characters", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        coroutineScope.launch {
                            val signupResult = viewModel.signupUser(username, password, confirmedPassword, email)
                            signupResult.fold(
                                onSuccess = {
                                    // Clear input fields after successful signup
                                    username = ""
                                    password = ""
                                    email = ""
                                    confirmedPassword = ""

                                    // Show success message
                                    Toast.makeText(
                                        context,
                                        "Signup Successful! Redirecting to Login...",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Navigate back to the login screen
                                    navController.popBackStack()
                                },
                                onFailure = { error ->
                                    // Show error message from Result failure
                                    Toast.makeText(
                                        context,
                                        "Signup failed: ${error.message ?: "Unknown error"}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }) {
                Text("Sign Up")
            }
        }
    }
}
