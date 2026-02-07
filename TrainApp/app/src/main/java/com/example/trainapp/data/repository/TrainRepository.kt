package com.example.trainapp.data.repository

import com.example.trainapp.data.api.Journey
import com.example.trainapp.data.api.SncfApiService
import com.example.trainapp.data.api.Place

class TrainRepository(private val apiService: SncfApiService) {

    suspend fun searchPlaces(query: String): List<Place> {
        return try {
            val response = apiService.searchPlaces(query)
            if (response.isSuccessful) {
                val places = response.body()?.places ?: emptyList()

                // Tri par qualité décroissante (100 en premier, puis 90, etc.)
                places.sortedByDescending { it.quality }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getJourneys(fromId: String, toId: String, dateTime: String?): List<Journey> {
        return try {
            android.util.Log.d("TRAIN_APP_DEBUG", "--- REQUÊTE TRAJET ---")
            android.util.Log.d("TRAIN_APP_DEBUG", "From ID: $fromId")
            android.util.Log.d("TRAIN_APP_DEBUG", "To ID: $toId")
            android.util.Log.d("TRAIN_APP_DEBUG", "DateTime: ${dateTime ?: "NON SPÉCIFIÉ (Maintenant)"}")

            val response = apiService.getJourneys(fromId, toId, dateTime)

            if (response.isSuccessful) {
                val journeys = response.body()?.journeys ?: emptyList()
                android.util.Log.d("TRAIN_APP_DEBUG", "Succès : ${journeys.size} trajets trouvés")
                journeys
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody?.contains("date_out_of_bounds") == true) {
                    android.util.Log.e("TRAIN_APP_DEBUG", "Date trop lointaine")
                }
                android.util.Log.e("TRAIN_APP_DEBUG", "Erreur API : ${response.code()} - $errorBody")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("TRAIN_APP_DEBUG", "Exception lors de la recherche", e)
            emptyList()
        }
    }
}