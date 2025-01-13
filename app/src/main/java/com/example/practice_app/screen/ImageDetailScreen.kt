package com.example.practice_app.screen

import com.example.practice_app.models.FavoritesViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun ImageDetailScreen(
    imageUrl: String,
    description: String,
    imageId: Long,
    userId: Long,
    viewModel: FavoritesViewModel,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main zoomable image component
        ZoomableImage(
            imageUrl = imageUrl,
            contentDescription = "Full size image",
            imageId = imageId,
            userId = userId,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )

        // Back button with improved visibility
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 50.dp, start = 20.dp)  // Increased top padding
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    shape = CircleShape
                )
                .size(40.dp)  // Fixed size for the button
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        // Description of the image
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ZoomableImage(
    imageUrl: String,
    contentDescription: String,
    imageId: Long,
    userId: Long,
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier
) {
    var scale = remember { mutableStateOf(1f) }
    var isFavorite = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Set the initial favorite state on composable launch.
    LaunchedEffect(imageId) {
        isFavorite.value = viewModel.isFavorite(imageId)
    }

    val state = rememberTransformableState { zoomChange, _, _ ->
        scale.value = maxOf(1f, minOf(8f, scale.value * zoomChange))
    }

    Box(
        modifier = modifier
            .transformable(state = state)
    ) {
        // Display the image with Coil and apply zoom transformations.
        CoilImageForAllImages(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
                .clip(RoundedCornerShape(12.dp))
        )

        // Heart (favorite) icon button with matching style to back button
        IconButton(
            onClick = {
                isFavorite.value = !isFavorite.value
                coroutineScope.launch {
                    if (isFavorite.value) {
                        viewModel.addFavorite(imageId, imageUrl, contentDescription)
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.removeFavorite(imageId)
                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    shape = CircleShape
                )
                .size(40.dp)  // Fixed size to match back button
        ) {
            Icon(
                imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite.value) "Unfavorite" else "Favorite",
                tint = if (isFavorite.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)  // Fixed size to match back button icon
            )
        }
    }
}

//// A zoomable image without a favorite icon for the detail view of already favorited images.
@Composable
fun ZoomableImageWithoutHeart(
    imageUrl: String,            // URL of the image.
    contentDescription: String,  // Description for accessibility.
    modifier: Modifier = Modifier // Modifier for customizing appearance.
) {
    var scale = remember { mutableStateOf(1f) }  // State for zoom scale.
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale.value = maxOf(1f, minOf(8f, scale.value * zoomChange))  // Restrict zoom between 1x and 8x.
    }

    Box(
        modifier = modifier
            .transformable(state = state)  // Enable pinch-to-zoom.
    ) {
        // Display the image using Coil with zoom transformations.
        CoilImageForAllImages(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale.value,  // Apply zoom scale on X-axis.
                    scaleY = scale.value   // Apply zoom scale on Y-axis.
                )
                .clip(RoundedCornerShape(12.dp))  // Rounded corners.
        )
    }
}

//// Detail screen for already favorited images without favorite management options.
@Composable
fun FavoriteImageDetailScreen(
    imageUrl: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ZoomableImageWithoutHeart(
            imageUrl = imageUrl,
            contentDescription = "Full-size image",
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}