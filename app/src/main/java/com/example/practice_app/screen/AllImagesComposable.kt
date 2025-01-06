package com.example.practice_app.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.practice_app.R
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.models.AllImagesViewModel
import com.example.practice_app.navigation.NavRoutes
import kotlinx.coroutines.delay
import java.net.URLEncoder
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.zIndex
import com.example.practice_app.models.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllImagesComposable(navController: NavController, userViewModel: UserViewModel) {
    var allImagesData by remember { mutableStateOf<List<AllImagesItem>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) } // Initial loading state for full-screen spinner
    val viewModel: AllImagesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isDarkTheme by userViewModel.isDarkModeEnabled.collectAsState()

    // Function to check for network connectivity
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Function to load data with retry mechanism
    val refreshData: suspend () -> Unit = suspend@{
        isRefreshing = true
        if (!isNetworkAvailable(context)) {
            allImagesData = emptyList()
            isRefreshing = false
            return@suspend
        }

        delay(1000) // Delay to simulate data loading
        allImagesData = viewModel.fetchAllImagesData() ?: emptyList()
        isRefreshing = false
        isLoading = false // Set isLoading to false after the initial load
    }

    // Initialize the Pull-to-Refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { scope.launch { refreshData() } }
    )

    // Initial data load effect
    LaunchedEffect(Unit) {
        refreshData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState) // Attach pullRefresh state
    ) {

        val backgroundImage = if (isDarkTheme) {
            painterResource(id = R.drawable.darkness) // Replace with dark mode resource ID
        } else {
            painterResource(id = R.drawable.lightness) // Replace with light mode resource ID
        }

        Image(
            painter = backgroundImage, // Replace with the actual resource ID
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Display a full-screen loading indicator for the initial load
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Display LazyVerticalGrid with images
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    top = 67.dp, start = 8.dp, end = 8.dp, bottom = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allImagesData) { image ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                            .clickable {
                                val encodedImageUrl = URLEncoder.encode(image.allImageUrl, "UTF-8")
                                val encodedDescription = URLEncoder.encode(image.allImageDescriptions, "UTF-8")
                                navController.navigate(
                                    NavRoutes.ImageDetail.createRoute(
                                        imageUrl = encodedImageUrl,
                                        description = encodedDescription,
                                        imageId = image.allImagesId.toLong()
                                    )
                                )
                            }
                    ) {
                        AsyncImage(
                            model = image.allImageUrl,
                            contentDescription = "AllImages picture",
                            contentScale = ContentScale.Crop, // Changed from Fit to Crop
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Pull-to-refresh indicator
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f), // Ensure indicator appears above content
            scale = true // Enable scaling for smooth pull effect
        )
    }
}

@Composable
// Function to load and display an image using Coil (or a placeholder if the image URL is empty).
fun CoilImageForAllImages(imageUrl: String, contentDescription: String, modifier: Modifier = Modifier) {
    // Decide what image model to use, either an image URL or a fallback placeholder image.
    val imageModel = if (imageUrl.isEmpty()) {
        painterResource(R.drawable.no_photography)  // Fallback drawable resource.
    } else {
        rememberImagePainter(imageUrl)  // Use Coil to load the image from the provided URL.
    }

    // Display the image using the Image composable, scaling it to fit the content area.
    Image(
        painter = imageModel,
        contentDescription = contentDescription,  // For accessibility purposes.
        modifier = modifier
            .fillMaxWidth(),  // Ensure the image fills the available width.
        contentScale = ContentScale.Fit  // Fit the image within its bounds while maintaining the aspect ratio.
    )
}
