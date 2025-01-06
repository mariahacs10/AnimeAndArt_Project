package com.example.practice_app.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.practice_app.models.ForgotPasswordViewModel
import kotlinx.coroutines.delay

// Use experimental Material 3 API
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackClick: () -> Unit
) {
    // Get the current context
    val context = LocalContext.current
    // State to control when to show a toast
    var showToast by remember { mutableStateOf(false) }

    // Side effect to trigger toast when success message changes
    LaunchedEffect(viewModel.successMessage) {
        if (viewModel.successMessage != null) {
            showToast = true
        }
    }

    // Show toast if showToast is true
    if (showToast) {
        Toast.makeText(context, viewModel.successMessage, Toast.LENGTH_LONG).show()
        showToast = false
    }

    // Main column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email input field
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Spacer for vertical spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = { viewModel.sendForgotPasswordRequest() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            Text("Submit")
        }

        // Show loading indicator if request is in progress
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }

        // Show error message if any
        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // Show success message if any
        viewModel.successMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        // Spacer for vertical spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Back to login button
        TextButton(onClick = onBackClick) {
            Text("Back to Login")
        }
    }
}