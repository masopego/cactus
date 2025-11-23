package es.masopego.cactus.attendances.domain

import java.time.LocalDateTime
import java.util.*

data class Attendance(
    val id: UUID,
    val userId: UUID,
    val meetupId: UUID,
    val confirmed: LocalDateTime? = null
)