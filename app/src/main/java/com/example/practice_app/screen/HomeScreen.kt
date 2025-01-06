package com.example.practice_app.screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Scale
import com.example.practice_app.R
import com.example.practice_app.db.User
import com.example.practice_app.models.UserRepository
import com.example.practice_app.models.UserViewModel
import com.example.practice_app.models.UserViewModelFactory
import com.example.practice_app.navigation.NavBarItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, viewModel: UserViewModel) {
    val username = viewModel.username.value // Access the value directly
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkModeEnabled.collectAsState()

    /*  val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            viewModel.updateGoogleUser(account) // Save user and token in Room
            account.givenName?.let { googleName ->
                if (viewModel.username.value != googleName) {
                    viewModel.updateUsername(googleName)
                }
            }
        } else {*/

    // Check Google account and update username
    LaunchedEffect(Unit) {
        viewModel.getLoggedInUser() // Regular login logic
    }


    val backgroundImage = if (isDarkTheme) {
        painterResource(id = R.drawable.darkness)
    } else {
        painterResource(id = R.drawable.lightness)
    }
//
//    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestEmail()
//        .build()
//
//    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(218.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileImage(username) // Your profile image logic
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (username.isNotEmpty()) username else "Guest",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                        )
                    }

                    Divider()
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Favorites",
                                color = if (isDarkTheme) Color.LightGray else Color.Black
                            )
                        },
                        selected = false,
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
                        onClick = { navController.navigate("favorites_screen") }
                    )

                    Divider()
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Settings",
                                color = if (isDarkTheme) Color.LightGray else Color.Black
                            )
                        },
                        selected = false,
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        onClick = { navController.navigate("settings_screen") }
                    )

                    Spacer(modifier = Modifier.padding(top = 200.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                "Logout",
                                color = if (isDarkTheme) Color.LightGray else Color.Black
                            )
                        },
                        selected = false,
                        icon = { Icon(Icons.Filled.ExitToApp, contentDescription = "Logout") },
                        onClick = {
                            coroutineScope.launch {
                                viewModel.logoutUser(username)
                                navController.navigate("login_screen") {
                                    popUpTo("home_screen") { inclusive = true }
                                }
                            }

                        }
                    )
                }
            }

        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("ArtWork And Anime") },
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }) {
                                Icon(Icons.Rounded.Menu, contentDescription = "MenuButton")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                },
                content = { MainScreen(navController) }
            )
        }
    }
}


@Composable
fun ProfileImage(username: String) {
    // State for holding the image URI
    val imageUri = rememberSaveable { mutableStateOf("") }
    val showImageSourceDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(if (imageUri.value.isNotEmpty()) imageUri.value else R.drawable.outline_person_24)
            .scale(Scale.FILL)
            .build(),
        placeholder = painterResource(R.drawable.outline_person_24)
    )

    // Launcher for picking an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                saveProfileImage(context, it.toString(), username)?.let { savedUri ->
                    imageUri.value = "$savedUri?timestamp=${System.currentTimeMillis()}"
                }
            }
        }
    }

    // Launcher for taking a new picture with the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            coroutineScope.launch {
                val savedUri = saveImageToInternalStorage(context, it, getProfileImageFilename(username))
                if (savedUri.isNotEmpty()) {
                    imageUri.value = "$savedUri?timestamp=${System.currentTimeMillis()}"
                }
            }
        }
    }

    // Load the saved image path on initial composition
    LaunchedEffect(username) {
        val savedPath = getSavedImagePath(context, username)
        if (savedPath.isNotEmpty()) {
            imageUri.value = "$savedPath?timestamp=${System.currentTimeMillis()}"
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(88.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable { showImageSourceDialog.value = true },
                contentScale = ContentScale.Crop
            )
        }
    }

    // Show dialog for image source selection
    if (showImageSourceDialog.value) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog.value = false },
            title = { Text("Select Image Source") },
            text = {
                Column {
                    Text(
                        "Choose from Library",
                        modifier = Modifier
                            .clickable {
                                showImageSourceDialog.value = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        "Take a Picture",
                        modifier = Modifier
                            .clickable {
                                showImageSourceDialog.value = false
                                cameraLauncher.launch(null)
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

// Function to get the saved image path for a user
private fun getSavedImagePath(context: Context, username: String): String {
    val file = File(context.filesDir, getProfileImageFilename(username))
    return if (file.exists()) "file://${file.absolutePath}" else ""
}

// Function to save an image to internal storage
private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, filename: String): String {
    try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        val file = File(context.filesDir, filename)
        return "file://${file.absolutePath}"
    } catch (e: IOException) {
        Log.e("ProfileImage", "Error saving image: ${e.message}")
        return ""
    }
}

// Function to generate a profile image filename based on username
private fun getProfileImageFilename(username: String): String {
    return "profile_image_${username}.jpg"
}

// Helper function to save profile image
private suspend fun saveProfileImage(context: Context, imageUri: String, username: String): String? {
    try {
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUri)
            .build()
        val bitmap = context.imageLoader.execute(imageRequest).drawable?.toBitmap()
        return bitmap?.let { saveImageToInternalStorage(context, it, getProfileImageFilename(username)) }
    } catch (e: Exception) {
        Log.e("ProfileImage", "Error saving profile image: ${e.message}")
        return null
    }
}


// Main screen composable function
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val pagerState = rememberPagerState()  // Remembering state for pager
    val coroutineScope = rememberCoroutineScope()  // Remembering coroutine scope
    val context = LocalContext.current
    // Create UserRepository instance (or inject it if you’re using a dependency injection framework)
    val userRepository = UserRepository(context)  // Ensure UserRepository is properly initialized
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )
    val isDarkTheme by userViewModel.isDarkModeEnabled.collectAsState()
    // Create an instance of UserViewModel using the custom factory


    Scaffold(
        content = { padding ->
            HorizontalPager(
                count = NavBarItems.BarItems.size,
                state = pagerState,
                modifier = Modifier.padding(padding)
            ) { page ->
                when (page) {
                    0 -> AllImagesComposable(navController, userViewModel = userViewModel)
                    1 -> AnimeConventionComposable(navController)
                    2 -> ErikasArtWorkComposable(navController)
                    3 -> CommingSoonComposable()
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                pagerState = pagerState,
                scope = coroutineScope,
                isDarkTheme = isDarkTheme
            )
        }
    )

}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationBar(pagerState: PagerState, scope: CoroutineScope, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.primary
    val contentColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onPrimary

    NavigationBar(
        modifier = Modifier.height(70.dp),
        containerColor = backgroundColor,
        tonalElevation = 0.dp
    ) {
        NavBarItems.BarItems.forEachIndexed { index, navItem ->
            val selected = pagerState.currentPage == index
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(
                            bounded = false,
                            color = contentColor.copy(alpha = 0.6f),
                        )
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = index,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedIcon(
                        outlinedIcon = navItem.image,
                        filledIcon = navItem.filledIcon,
                        selected = selected,
                        contentColor = contentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = navItem.title,
                        fontSize = 11.sp,
                        color = contentColor,
                        modifier = Modifier.alpha(if (selected) 1f else 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedIcon(
    outlinedIcon: Int,
    filledIcon: Int,
    selected: Boolean,
    contentColor: Color
) {
    val transition = updateTransition(selected, label = "iconTransition")
    val alpha by transition.animateFloat(
        label = "iconAlpha",
        transitionSpec = { tween(durationMillis = 500) }
    ) { if (it) 1f else 0f }

    Box {
        Icon(
            painter = painterResource(id = outlinedIcon),
            contentDescription = null,
            tint = contentColor.copy(alpha = 1f - alpha)
        )
        Icon(
            painter = painterResource(id = filledIcon),
            contentDescription = null,
            tint = contentColor.copy(alpha = alpha)
        )
    }
}
