package es.masopego.cactus.meetups.domain

import es.masopego.cactus.venues.domain.Venue
import java.time.LocalDate

data class Meetup(
    val id: Long,
    val title: String,
    val description: String,
    val startDate: LocalDate?,
    val venue: Venue
)