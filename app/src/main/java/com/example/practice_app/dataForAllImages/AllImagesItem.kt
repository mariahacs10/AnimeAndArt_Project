package com.example.practice_app.dataForAllImages

// Defining a data class for AllImagesItem.
//This AllImagesItem class holds the main properties related to an image,
// such as its description, URL, ID, category, and associated favorites.
data class AllImagesItem(
    val allImageDescriptions: String,
    val allImageUrl: String,
    val allImagesId: Int,
    val category: String,
    val favorites: List<Favorite>
)