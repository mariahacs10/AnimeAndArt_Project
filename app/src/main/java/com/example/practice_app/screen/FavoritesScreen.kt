package com.example.practice_app.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.practice_app.models.FavoritesViewModel
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.practice_app.R
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.db.AppDatabase
import com.example.practice_app.db.RetrofitClient
import com.example.practice_app.models.FavoritesRepository
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.models.UserViewModelFactory
import com.example.practice_app.navigation.NavRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel,
    userId: Long,
    drawerState: DrawerState
) {
    val favorites by viewModel.favorites.observeAsState(emptyList())
    val selectedImages by viewModel.selectedImages.observeAsState(emptyList())
    val isDeleteDialogVisible by viewModel.isDeleteDialogVisible.observeAsState(false)
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val favoriteImageDao = database.favoriteImageDao()
    val apiService = RetrofitClient.createApiService
    val userRepository = UserRepository(context, favoriteImageDao)  // Ensure UserRepository is properly initialized
    val favoritesRepository = FavoritesRepository(apiService, favoriteImageDao, userRepository)

    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository,favoritesRepository))
    val isDarkTheme by userViewModel.isDarkModeEnabled.collectAsState()



    // Fetch favorites on screen load
    LaunchedEffect(userId) {
        viewModel.fetchFavorites(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (drawerState.isOpen) drawerState.close()
                            else navController.navigate("home_screen")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (selectedImages.isNotEmpty()) {
                        IconButton(onClick = { viewModel.showBulkDeleteDialog() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Selected",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        // Box with background image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background image
            val backgroundImage = if (isDarkTheme) {
                painterResource(id = R.drawable.darkness)
            } else {
                painterResource(id = R.drawable.lightness)
            }

            Image(
                painter = backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Content overlay on top of the background
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (favorites.isEmpty()) {
                    // Show placeholder if no favorites
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No favorites yet",
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                } else {
                    // Display favorites in a grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favorites) { favorite ->
                            val isSelected = selectedImages.contains(favorite)

                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = { viewModel.toggleSelection(favorite) },
                                            onTap = {
                                                if (!isSelected) {
                                                    val encodedImageUrl = URLEncoder.encode(favorite.allImageUrl, "UTF-8")
                                                    navController.navigate(
                                                        NavRoutes.FavoriteImageDetail.createRoute(
                                                            imageUrl = encodedImageUrl,
                                                            imageId = favorite.allImagesId.toLong()
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    }
                                    .background(
                                        if (isSelected) Color.Gray.copy(alpha = 0.5f) else Color.Transparent
                                    )
                            ) {
                                AsyncImage(
                                    model = favorite.allImageUrl,
                                    contentDescription = favorite.allImageDescriptions,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text("Delete Selected Favorites") },
            text = { Text("Are you sure you want to delete the selected favorites?") },
            confirmButton = {
                TextButton(onClick = { viewModel.bulkDeleteSelectedFavorites() }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
