package com.example.practice_app.navigation

// Data class representing an item in a navigation bar or menu.
// This class holds information such as the title, image, and route for the navigation item.
data class BarItem(
    val title: String,
    val image: Int,
    val filledIcon: Int,
    val route: String
)