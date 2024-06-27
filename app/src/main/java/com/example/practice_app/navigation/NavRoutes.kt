package com.example.practice_app.navigation

sealed class NavRoutes(val route: String) {

    object Anime : NavRoutes("anime")
    object ArtWork : NavRoutes("artwork")
    object CommingSoon : NavRoutes("comming soon")
}