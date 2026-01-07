package es.masopego.cactus.feedback.infrastructure.http.dto

import java.util.*

data class FeedbackResponse(
    val id: UUID,
    val attendanceId: UUID,
    val rating: Short,
    val comment: String
)

