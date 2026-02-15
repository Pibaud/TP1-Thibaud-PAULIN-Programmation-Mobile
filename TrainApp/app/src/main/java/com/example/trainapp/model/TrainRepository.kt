package com.example.trainapp.model

class TrainRepository(private val apiService: SncfApiService) {

    suspend fun searchPlaces(query: String): List<Place> {
        return try {
            val response = apiService.searchPlaces(query)
            if (response.isSuccessful) {
                val places = response.body()?.places ?: emptyList()
                places.sortedByDescending { it.quality }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getJourneys(fromId: String, toId: String, datetime: String?): JourneyResponse {
        val response = apiService.getJourneys(fromId, toId, datetime)
        if (response.isSuccessful) {
            return response.body() ?: JourneyResponse(emptyList(), emptyList())
        } else {
            throw Exception("Erreur API : ${response.code()}")
        }
    }
}