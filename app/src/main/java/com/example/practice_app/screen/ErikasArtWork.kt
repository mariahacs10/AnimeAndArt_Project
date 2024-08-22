package com.example.practice_app.screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.models.AllImagesViewModel

@Composable
fun ErikasArtWorkComposable() {
    val artWork = remember { mutableStateOf<List<AllImagesItem>>(emptyList()) }
    val viewModel: AllImagesViewModel = viewModel()

    LaunchedEffect(Unit) {
        val art = viewModel.fetchArtWorkImagesData() ?: emptyList()
        artWork.value = art
    }

    if (artWork.value.isNotEmpty()) {
        LazyVerticalGrid(

            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFA2A2A2)),
            contentPadding = PaddingValues(top = 63.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(artWork.value) { images ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    CoilImageForAllImages(
                        imageUrl = images.allImageUrl,
                        contentDescription = "ArtWork picture",
                        modifier = Modifier
                            .size(150.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(47.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    } else {
        Text("No artWork found", modifier = Modifier.padding(16.dp))
    }
}