package com.example.practice_app.navigation

// NavRoutes is a sealed class that defines the navigation routes in the application.
// Each route is represented as an object that inherits from NavRoutes, allowing for
// type-safe navigation and centralized route management.
sealed class NavRoutes(val route: String) {

    // Route for navigating to the "All Images" screen, which shows all available images.
    object AllImages : NavRoutes("allImages")

    // Route for navigating to the "Anime Convention" screen, which likely shows anime-related content.
    object AnimeConvention : NavRoutes("anime")

    // Route for navigating to the "Erika's ArtWork" screen, which likely shows Erika's artwork.
    object ErikasArtWork : NavRoutes("erikasArtWork")

    // Route for navigating to the "Coming Soon" screen, which likely shows upcoming content or features.
    object CommingSoon : NavRoutes("comming soon")

    object SettingsDark : NavRoutes("settings_screen")


    // Route for navigating to the "Image Detail" screen, which displays details for a specific image.
    // This object contains a function to create a route with dynamic parameters.
    object ImageDetail : NavRoutes("imageDetail/{imageUrl}/{description}/{imageId}") {

        // Function to create a route for a specific image detail by injecting the parameters:
        // - imageUrl: the URL of the image to display
        // - description: a description of the image
        // - imageId: the unique ID of the image
        //
        // This function returns a formatted route string with the specified parameters.
        fun createRoute(imageUrl: String, description: String, imageId: Long) =
            "imageDetail/$imageUrl/$description/$imageId"
    }

    // Route for navigating to the "Favorite Image Detail" screen, which displays details for a specific favorite image.
    // This object contains a function to create a route with dynamic parameters.
    object FavoriteImageDetail : NavRoutes("favoriteImageDetail/{imageUrl}/{imageId}") {

        // Function to create a route for a specific favorite image detail by injecting the parameters:
        // - imageUrl: the URL of the favorite image to display
        // - imageId: the unique ID of the favorite image
        //
        // This function returns a formatted route string with the specified parameters.
        fun createRoute(imageUrl: String, imageId: Long) = "favoriteImageDetail/$imageUrl/$imageId"
    }
}
