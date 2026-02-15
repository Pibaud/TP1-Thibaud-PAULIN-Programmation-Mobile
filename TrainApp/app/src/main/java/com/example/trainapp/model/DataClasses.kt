package com.example.trainapp.model

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
    val journeys: List<Journey>,
    val links: List<Link>? = null
)

data class Link(
    val href: String,
    val rel: String, // les liens next ou prev
    val type: String?
)

data class Journey(
    val duration: Int, // Durée en secondes
    @SerializedName("departure_date_time")
    val departureDateTime: String, // ex: 20260207T145400
    @SerializedName("arrival_date_time")
    val arrivalDateTime: String,
    val sections: List<Section>,
    @SerializedName("nb_transfers")
    val nbTransfers: Int = 0
)

data class Section(
    val type: String,
    val duration: Int,
    @SerializedName("departure_date_time")
    val departureDateTime: String,
    @SerializedName("arrival_date_time")
    val arrivalDateTime: String,
    val from: PlaceSummary?,
    val to: PlaceSummary?,
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
    val headsign: String, // Numéro du train (ex: 9864)
    @SerializedName("physical_mode")
    val physicalMode: String?
)