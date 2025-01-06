package com.example.practice_app.navigation


import com.example.practice_app.R


object NavBarItems {

    val BarItems = listOf(
        BarItem(
            title = "AllImages",
            image = R.drawable.outline_color_lens_24,
            filledIcon = R.drawable.art,
            route = "allImages"
        ),
        BarItem(
            title = "Anime",
            image = R.drawable.outline_person_24,
            filledIcon = R.drawable.person,
            route = "anime"
        ),
        BarItem(
            title = "ArtWork",
            image = R.drawable.outline_color_lens_24,
            filledIcon = R.drawable.art,
            route = "erikasArtWork"
        ),
        BarItem(
            title = "Comming soon",
            image = R.drawable.outline_lock_24,
            filledIcon = R.drawable.local,
            route = "comming soon"
        )
    )
}