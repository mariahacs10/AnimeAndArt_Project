package com.example.practice_app.dataForAllImages

data class AllImagesItem(
    val allImageDescriptions: String,
    val allImageUrl: String,
    val allImagesId: Int,
    val category: String,
    val favorites: List<Any>
)