package com.example.practice_app.screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Menu
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
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import com.example.practice_app.R
import com.example.practice_app.db.User
import com.example.practice_app.models.UserViewModel
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
    // Declares a variable 'username' that triggers recomposition when changed
    var username by remember { mutableStateOf(viewModel.getLoggedInUsername()) }
    // Remembers a coroutine scope to launch coroutines
    val coroutineScope = rememberCoroutineScope()
    // Remembers the state of a drawer (open or closed)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    // Declares a variable 'isVisible' that triggers recomposition when changed
    val isVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val account = GoogleSignIn.getLastSignedInAccount(context)

    val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //This line adds a request to retrieve the user's email
            // address during the sign-in process.
            .requestEmail()
            //This line builds and returns the final GoogleSignInOptions
            // object based on the configured options.
            .build()
    }
    val googleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

    val user = remember {
        account?.let { googleAccount ->
            // Map Google account to User
            User(
                id = null,
                username = googleAccount.givenName!!,
                password = null,
                confirmPassword = null
            )
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            viewModel.getLoggedInUser()
        }
    }

    ModalNavigationDrawer(
        // Passes the drawer state to the modal navigation drawer
        drawerState = drawerState,
        gesturesEnabled = true,
        // Defines the content of the drawer
        drawerContent = {
            // AnimatedVisibility composable with enter and exit animations
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it / 2 },  // Start from half width off-screen
                    animationSpec = tween(durationMillis = 20000, easing = EaseInOutCubic)
                ) + fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 20000, easing = EaseInOutCubic)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it / 2 },  // Slide out to half width off-screen
                    animationSpec = tween(durationMillis = 20000, easing = EaseInOutCubic)
                ) + fadeOut(
                    targetAlpha = 0f,
                    animationSpec = tween(durationMillis = 20000, easing = EaseInOutCubic)
                ),
                modifier = Modifier.fillMaxHeight().width(218.dp)
            ){
                ModalDrawerSheet(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                            .height(150.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (account != null) {
                            ProfileImage(username)
                            Text("${user?.username}", style = TextStyle(fontSize = 20.sp, color = Color.White))
                        } else {
                            ProfileImage(username)
                            Text(text = viewModel.username.value, style = TextStyle(fontSize = 20.sp, color = Color.White))
                        }
                    }

                    // Divider composable
                    Divider()

                    // Custom composable item for navigation drawer
                    NavigationDrawerItem(
                        // Text label for logout
                        label = { Text(text = "Logout", color = Color.Black) },
                        // Indicates if the item is selected
                        selected = false,
                        // Icon for logout
                        icon = { Icon(Icons.Filled.ExitToApp, contentDescription = "Logout") },
                        // Click listener for logout action
                        onClick = {
                            coroutineScope.launch {
                                if (viewModel.isGoogleSignIn()) {
                                    googleSignInClient.signOut()
                                        .addOnCompleteListener {
                                            viewModel.logoutUserGoogle()
                                            navController.navigate("login_screen") {
                                                popUpTo("home_screen") { inclusive = true }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.e("SignOut", "Failed to sign out: ${it.message}")
                                        }
                                } else {
                                    viewModel.logoutUser(username)
                                    navController.navigate("login_screen") {
                                        popUpTo("home_screen") { inclusive = true }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    // Title text for the top app bar
                    title = { Text("Practice App") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                // Opens the drawer if it's closed
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                    Log.d("DrawerState", "Drawer Opened - isVisible: $isVisible")  // Add this line
                                } else {
                                    // Closes the drawer if it's open
                                    drawerState.close()
                                    Log.d("DrawerState", "Drawer Closed - isVisible: $isVisible")  // Add this line
                                }
                            }
                        }) {
                            // Icon for the menu button
                            Icon(Icons.Rounded.Menu, contentDescription = "MenuButton")
                        }
                    },
                    // Color for the app bar
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) {
            // Composable function for the bottom navmain screen content
            MainScreen()
        }
    }
}

@Composable
fun ProfileImage(username: String) {
    // State for holding the image URI
    val imageUri = rememberSaveable { mutableStateOf("") }
    // Accessing the current context
    val context = LocalContext.current
    // Coroutine scope for launching asynchronous tasks
    val coroutineScope = rememberCoroutineScope()

    // Remembering the asynchronous image painter
    val painter = rememberAsyncImagePainter(
        // Conditional model based on imageUri's value
        model = if (imageUri.value.isNotEmpty()) imageUri.value else R.drawable.baseline_person,
        // Placeholder image resource
        placeholder = painterResource(R.drawable.baseline_person)
    )

    // Remembering the launcher for activity result
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Setting the image URI value from the selected URI
            imageUri.value = it.toString()
            coroutineScope.launch {
                // Launching a coroutine to handle image loading and saving
                if (imageUri.value.isNotEmpty()) {
                    val imageRequest = ImageRequest.Builder(context)
                        .data(imageUri.value)
                        .build()
                    val bitmap = context.imageLoader.execute(imageRequest).drawable?.toBitmap()
                    bitmap?.let { bmp ->
                        // Saving the image to internal storage
                        saveImageToInternalStorage(context, bmp, getProfileImageFilename(username))
                    }
                }
            }
        }
    }

    // Launched effect to load saved image path on initial composition
    LaunchedEffect(username) {
        val savedImagePath = getSavedImagePath(context, username)
        imageUri.value = savedImagePath
    }

    // Column composable to hold the profile image and related UI
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card to contain the profile image
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(88.dp)
        ) {
            // Image composable to display the profile picture
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },  // Launches image picker on click
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Function to get the saved image path for a user
private fun getSavedImagePath(context: Context, username: String): String {
    val file = File(context.filesDir, getProfileImageFilename(username))
    return if (file.exists()) file.absolutePath else ""
}

// Function to save an image to internal storage
private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, filename: String) {
    try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
    } catch (e: IOException) {
        Log.e("ProfileImage", "Error saving image: ${e.message}")
    }
}

// Function to generate a profile image filename based on username
private fun getProfileImageFilename(username: String): String {
    return "profile_image_${username}.jpg"
}

// Main screen composable function
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val pagerState = rememberPagerState()  // Remembering state for pager
    val coroutineScope = rememberCoroutineScope()  // Remembering coroutine scope

    // Scaffold composable for main screen layout
    Scaffold(
        content = { padding ->
            // HorizontalPager composable to display different screens/pages
            HorizontalPager(
                count = NavBarItems.BarItems.size,
                state = pagerState,
                modifier = Modifier.padding(padding)
            ) { page ->
                // Switch statement to select content based on page index
                when (page) {
                    0 -> AnimeComposable()  // First page content
                    1 -> ArtWorkComposable()  // Second page content
                    2 -> CommingSoonComposable()  // Third page content
                }
            }
        },
        bottomBar = {
            // Bottom navigation bar composable
            BottomNavigationBar(pagerState = pagerState, scope = coroutineScope)
        }
    )
}

// Bottom navigation bar composable
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationBar(pagerState: PagerState, scope: CoroutineScope) {
    NavigationBar {
        NavBarItems.BarItems.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        // Use animateScrollToPage with a custom animation spec
                        tween<Float>(
                            durationMillis = 3000,
                            easing = EaseInOutCubic
                        )
                        pagerState.animateScrollToPage(
                            page = index
                        )
                    }
                },
                icon = {
                    // Add transition animation for icon
                    val transition = updateTransition(pagerState.currentPage == index, label = "iconTransition")
                    val iconSize by transition.animateFloat(
                        label = "iconSize",
                        transitionSpec = { tween(3000) }
                    ) { selected -> if (selected) 28f else 24f }

                    Image(
                        modifier = Modifier.size(iconSize.dp),
                        painter = painterResource(id = navItem.image),
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(
                        text = navItem.title,
                        // Add transition animation for text
                        modifier = Modifier.graphicsLayer {
                            alpha = if (pagerState.currentPage == index) 1f else 0.6f
                            scaleX = if (pagerState.currentPage == index) 1.1f else 1f
                            scaleY = if (pagerState.currentPage == index) 1.1f else 1f
                        }
                    )
                },
            )
        }
    }
}