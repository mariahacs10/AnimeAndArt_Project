package com.example.practice_app.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.practice_app.dataForAllImages.AllImagesItem
import com.example.practice_app.db.RetrofitClient

// ViewModel class to handle data fetching and business logic for AllImages.
// This class is responsible for fetching image data from the API and preparing it for the UI.
// The use of ViewModel ensures that the data survives configuration changes (like screen rotations).
class AllImagesViewModel : ViewModel() {

    // Function to fetch all images data from the API.
    // This function is marked as suspend, meaning it runs in a coroutine for asynchronous handling.
    suspend fun  fetchAllImagesData(): List<AllImagesItem>?{
        return try{
            // Call the API service to get all images.
            val response = RetrofitClient.createApiService.getAllImages()
            if(response.isSuccessful){
                // If the response is successful, return the body of the response (the list of images).
                response.body()
            }
            else{
                // Log the error if the response was not successful.
                Log.d("TAG", "ERROR fetchData: ${response.code()} ${response.message()}")
                null
            }
        } catch(e : Exception)
        {
            // Catch any exceptions (e.g., network issues) and log the error message.
            Log.d("TAG", "ERROR fetchData: ${e.message}")
            null
        }
    }

    // Function to fetch images from the "anime" category.
    // Similar structure to the fetchAllImagesData function but filters by category.
    suspend fun fetchAnimeImagesData(): List<AllImagesItem>? {
        return try {
            // Call the API to fetch images by the "anime" category.
            val response = RetrofitClient.createApiService.getImagesByCategory("anime")
            if (response.isSuccessful) {
                // Return the list of images if successful.
                response.body()
            } else {
                // Log the error if the response failed.
                Log.d("TAG", "ERROR fetchAnimeData: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            // Catch any exceptions and log the error message.
            Log.d("TAG", "ERROR fetchAnimeData: ${e.message}")
            null
        }
    }

    // Function to fetch images from the "jjk" (Jujutsu Kaisen) category.
    // This follows the same pattern as the previous fetch functions.
    suspend fun fetchArtWorkImagesData(): List<AllImagesItem>? {
        return try {
            // Call the API to fetch images by the "jjk" category.
            val response = RetrofitClient.createApiService.getImagesByCategory("jjk")
            if (response.isSuccessful) {
                // Return the list of images if successful.
                response.body()
            } else {
                // Log the error if the response failed.
                Log.d("TAG", "ERROR fetchArtworkData: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            // Catch any exceptions and log the error message.
            Log.d("TAG", "ERROR fetchArtWorkData: ${e.message}")
            null
        }
    }
}