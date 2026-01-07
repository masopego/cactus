package es.masopego.cactus.feedback.infrastructure.http.dto

import java.time.LocalDateTime
import java.util.*

data class FeedbackWithMeetupResponse(
    val id: UUID,
    val rating: Short,
    val comment: String,
    val meetup: MeetupInfo
)

data class MeetupInfo(
    val id: UUID,
    val title: String,
    val description: String,
    val startDate: LocalDateTime?
)

