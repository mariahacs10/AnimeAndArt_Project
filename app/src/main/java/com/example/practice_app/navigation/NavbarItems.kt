package com.example.practice_app.navigation


import com.example.practice_app.R


object NavBarItems {

    val BarItems = listOf(
        BarItem(
            title = "Anime",
            image = R.drawable.person,
            route = "anime"
        ),
        BarItem(
            title = "Artwork",
            image = R.drawable.art,
            route = "artwork"
        ),
        BarItem(
            title = "Comming soon",
            image = R.drawable.local,
            route = "comming soon"
        )
    )
}