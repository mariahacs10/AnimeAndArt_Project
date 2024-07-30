package com.example.practice_app.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice_app.db.User
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")

    suspend fun onSignUpClick() {
        val user = User(username = username.value, password = password.value, confirmPassword = confirmPassword.value)
        userRepository.insert(user)
        username.value = ""
        password.value = ""
        confirmPassword.value = ""
    }

    suspend fun signupUser(username: String, password: String, confirmPassword: String): Boolean {
        return userRepository.signupUser(username, password, confirmPassword)
    }

    fun getLoggedInUsername(): String {
        return userRepository.getLoggedInUsername()
    }

    fun loginWithGoogle() {
        userRepository.saveLoginState(true, isGoogleSignIn = true)
    }

    fun isGoogleSignIn(): Boolean {
        return userRepository.isGoogleSignIn()
    }

    fun logoutUserGoogle() {
        userRepository.saveLoginState(false, false)
    }

    fun logoutUser(username: String) {
        viewModelScope.launch {
            userRepository.updateLoginStatus(username, false)
            userRepository.saveLoginState(false)
        }
    }

    fun getLoggedInUser() {
        viewModelScope.launch {
            val user = userRepository.getLoggedInUser()
            user?.let {
                username.value = it.username ?: ""
            }
        }
    }

    suspend fun getUsername(inputUsername: String): String? {
        val user = userRepository.getUser(inputUsername)
        return user?.username
    }

    suspend fun loginWithCredentials(username: String, password: String): Boolean {
        return userRepository.loginUser(username, password)
    }

    suspend fun getPassword(inputUsername: String): String? {
        val user = userRepository.getUser(inputUsername)
        return user?.password
    }
    suspend fun loginWithGoogleToken(idToken: String): Boolean {
        return userRepository.loginWithGoogleToken(idToken)
    }
    suspend fun updatePassword(username: String, oldPassword: String, newPassword: String) {
        userRepository.getUserByPassword(username, oldPassword, newPassword)
    }
}
