package com.example.practice_app.navigation

sealed class NavRoutes(val route: String) {

    object AllImages : NavRoutes("allImages")
    object AnimeConvention : NavRoutes("anime")
    object ErikasArtWork : NavRoutes("erikasArtWork")
    object CommingSoon : NavRoutes("comming soon")

    object ImageDetail : NavRoutes("imageDetail/{imageUrl}/{description}")
}