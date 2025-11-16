package es.masopego.cactus.venues.domain

import java.util.*

data class Venue(
    val id: UUID,
    val place: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val seats: Int,
)