package es.masopego.cactus.meetups.domain

import java.time.LocalDate

data class Meetup(
    val title: String,
    val description: String,
    val startDate: LocalDate,
)