package com.example.practice_app.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.db.RetrofitClient

class AllImagesViewModel : ViewModel() {

    suspend fun  fetchAllImagesData(): List<AllImagesItem>?{
        return try{
            val response = RetrofitClient.createApiService.getAllImages()
            if(response.isSuccessful){
                response.body()
            }
            else{
                Log.d("TAG", "ERROR fetchData: ${response.code()} ${response.message()}")
                null
            }
        } catch(e : Exception)
        {
            Log.d("TAG", "ERROR fetchData: ${e.message}")
            null
        }
    }

    // New function to fetch anime images
    suspend fun fetchAnimeImagesData(): List<AllImagesItem>? {
        return try {
            val response = RetrofitClient.createApiService.getImagesByCategory("anime")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.d("TAG", "ERROR fetchAnimeData: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.d("TAG", "ERROR fetchAnimeData: ${e.message}")
            null
        }
    }

    // New function to fetch anime images
    suspend fun fetchArtWorkImagesData(): List<AllImagesItem>? {
        return try {
            val response = RetrofitClient.createApiService.getImagesByCategory("jjk")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.d("TAG", "ERROR fetchArtworkData: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.d("TAG", "ERROR fetchArtWorkData: ${e.message}")
            null
        }
    }

}