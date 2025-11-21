package es.masopego.cactus.speakers.domain

import java.util.*

data class Speaker(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val biography: String,
    val company: String?,
)