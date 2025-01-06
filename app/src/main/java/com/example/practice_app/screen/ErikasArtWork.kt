package com.example.practice_app.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.practice_app.R
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.models.AllImagesViewModel
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.models.UserViewModelFactory
import com.example.practice_app.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder

// Composable function to display a grid of Erika's artwork images.
// It uses a lazy grid to display images in a structured format and includes loading and error handling.
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ErikasArtWorkComposable(navController: NavController) {
    val artWork = remember { mutableStateOf<List<AllImagesItem>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) } // Initial loading state for full-screen spinner
    val viewModel: AllImagesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val userRepository = UserRepository(context)  // Ensure UserRepository is properly initialized

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )
    val isDarkTheme by userViewModel.isDarkModeEnabled.collectAsState()



    // Function to check for network connectivity
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Function to load data with retry mechanism
    val refreshData: suspend () -> Unit = suspend@{
        isRefreshing = true
        if (!isNetworkAvailable(context)) {
            artWork.value = emptyList()
            isRefreshing = false
            return@suspend
        }

        delay(1000) // Delay to simulate data loading
        artWork.value = viewModel.fetchArtWorkImagesData() ?: emptyList()
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
                items(artWork.value) { images ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                            .clickable {
                                val encodedImageUrl = URLEncoder.encode(images.allImageUrl, "UTF-8")
                                val encodedDescription =
                                    URLEncoder.encode(images.allImageDescriptions, "UTF-8")
                                navController.navigate(
                                    NavRoutes.ImageDetail.createRoute(
                                        imageUrl = encodedImageUrl,
                                        description = encodedDescription,
                                        imageId = images.allImagesId.toLong()
                                    )
                                )
                            }
                    ) {
                        AsyncImage(
                            model = images.allImageUrl,
                            contentDescription = "ArtWork picture",
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