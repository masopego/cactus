package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class GetUserFeedbackStatsUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val attendanceRepository: AttendanceRepository,
    private val meetupRepository: MeetupRepository
) {

    fun execute(userId: UUID): UserFeedbackStats {
        val feedbacks = feedbackRepository.getFeedbackForUser(userId)

        val eventsWithFeedback = feedbacks.mapNotNull { feedback ->
            val attendance = attendanceRepository.findById(feedback.attendanceId)
            val meetup = attendance?.let { meetupRepository.findById(it.meetupId) }

            meetup?.let {
                EventWithFeedbackInfo(
                    title = it.title,
                    date = it.startDate,
                    rating = feedback.rating,
                    comment = feedback.comment
                )
            }
        }
        
        val confirmedAttendances = attendanceRepository.getAttendancesForUser(userId)
            .filter { it.confirmed != null }

        val totalConfirmedAttendances = confirmedAttendances.size
        val totalFeedbacks = feedbacks.size

        val feedbackPercentage = if (totalConfirmedAttendances > 0) {
            (totalFeedbacks.toDouble() / totalConfirmedAttendances.toDouble() * 100).toInt()
        } else {
            0
        }

        return UserFeedbackStats(
            eventsWithFeedback = eventsWithFeedback,
            feedbackPercentage = feedbackPercentage,
            totalConfirmedAttendances = totalConfirmedAttendances,
            totalFeedbacks = totalFeedbacks
        )
    }
}

data class UserFeedbackStats(
    val eventsWithFeedback: List<EventWithFeedbackInfo>,
    val feedbackPercentage: Int,
    val totalConfirmedAttendances: Int,
    val totalFeedbacks: Int
)

data class EventWithFeedbackInfo(
    val title: String,
    val date: LocalDateTime?,
    val rating: Short,
    val comment: String
)

