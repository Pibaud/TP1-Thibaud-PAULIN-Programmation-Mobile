package com.example.trainapp.data.api

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("places")
    val places: List<Place>
)

data class Place(
    val id: String,
    val name: String,
    val quality: Int,
    @SerializedName("embedded_type")
    val type: String = ""
)

data class JourneyResponse(
    @SerializedName("journeys")
    val journeys: List<Journey>
)

data class Journey(
    val duration: Int, // Durée en secondes
    @SerializedName("departure_date_time")
    val departureDateTime: String, // ex: 20260207T145400
    @SerializedName("arrival_date_time")
    val arrivalDateTime: String,
    val sections: List<Section>
)

data class Section(
    val type: String,
    val from: PlaceSummary?, // Contient l'id et le name du point de départ
    val to: PlaceSummary?,   // Contient l'id et le name du point d'arrivée
    @SerializedName("display_informations")
    val displayInformations: DisplayInfo?
)

data class PlaceSummary(
    val name: String,
    val id: String
)

data class DisplayInfo(
    val network: String, // ex: "TGV INOUI"
    val direction: String, // Destination finale du train
    @SerializedName("commercial_mode")
    val commercialMode: String,
    val headsign: String // Numéro du train (ex: 9864)
)