package com.example.trainapp.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SncfApiService {
    @GET("https://api.sncf.com/v1/coverage/sncf/places")
    suspend fun searchPlaces(
        @Query("q") query: String
    ): Response<PlaceResponse>

    @GET("https://api.sncf.com/v1/coverage/sncf/journeys")
    suspend fun getJourneys(
        @Query("from") fromId: String,
        @Query("to") toId: String,
        @Query("datetime") datetime: String? = null
    ): Response<JourneyResponse>
}