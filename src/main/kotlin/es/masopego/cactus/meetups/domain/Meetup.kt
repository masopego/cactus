package es.masopego.cactus.meetups.domain

import es.masopego.cactus.speakers.domain.Speaker
import es.masopego.cactus.venues.domain.Venue
import java.time.LocalDate
import java.util.*

data class Meetup(
    val id: UUID,
    val title: String,
    val description: String,
    val startDate: LocalDate?,
    val venue: Venue,
    val speakers: List<Speaker> = emptyList()
)