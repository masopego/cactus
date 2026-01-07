package es.masopego.cactus.feedback.domain

import java.util.*

data class Feedback(
    val id: UUID,
    val attendanceId: UUID,
    val rating: Short,
    val comment: String,
)