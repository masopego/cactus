package es.masopego.cactus.feedback.infrastructure.http.dto

import es.masopego.cactus.feedback.application.UserFeedbackStats
import java.time.LocalDateTime
import java.util.*

data class FeedbackResponse(
    val id: UUID,
    val attendanceId: UUID,
    val rating: Short,
    val comment: String
)

data class UserFeedbackStatsResponse(
    val eventsWithFeedback: List<EventWithFeedbackResponse>,
    val feedbackPercentage: Int,
    val totalConfirmedAttendances: Int,
    val totalFeedbacks: Int
) {
    companion object {
        fun from(stats: UserFeedbackStats): UserFeedbackStatsResponse {
            return UserFeedbackStatsResponse(
                eventsWithFeedback = stats.eventsWithFeedback.map {
                    EventWithFeedbackResponse(
                        title = it.title,
                        date = it.date,
                        rating = it.rating,
                        comment = it.comment
                    )
                },
                feedbackPercentage = stats.feedbackPercentage,
                totalConfirmedAttendances = stats.totalConfirmedAttendances,
                totalFeedbacks = stats.totalFeedbacks
            )
        }
    }
}

data class EventWithFeedbackResponse(
    val title: String,
    val date: LocalDateTime?,
    val rating: Short,
    val comment: String
)
