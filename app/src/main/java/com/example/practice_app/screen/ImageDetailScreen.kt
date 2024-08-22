package com.example.practice_app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun ImageDetailScreen(imageUrl: String, description: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA2A2A2))
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                    tint = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        ZoomableImage(
            imageUrl = imageUrl,
            contentDescription = "Full size image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)  // Adjust this value as needed
        )
    }
}

@Composable
fun ZoomableImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var scale = remember { mutableStateOf(1f) }
    var isFavorite = remember { mutableStateOf(false) }  // Track heart state

    val state = rememberTransformableState { zoomChange, _, _ ->
        scale.value = maxOf(1f, minOf(8f, scale.value * zoomChange))
    }

    Box(
        modifier = modifier
            .transformable(state = state)
    ) {
        CoilImageForAllImages(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
                .clip(RoundedCornerShape(100.dp)) // Increase the corner radius to 32dp
        )

        IconButton(
            onClick = { isFavorite.value = !isFavorite.value },  // Toggle heart state on click
            modifier = Modifier
                .align(Alignment.BottomEnd) // Position it at the bottom right
                .padding(bottom = 16.dp, end = 16.dp) // Add padding to position correctly
        ) {
            Icon(
                imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite.value) "Unfavorite" else "Favorite",
                tint = if (isFavorite.value) Color.Blue else Color.White
            )
        }
    }
}