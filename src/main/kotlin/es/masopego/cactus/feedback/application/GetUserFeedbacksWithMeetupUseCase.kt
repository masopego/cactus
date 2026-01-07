package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetUserFeedbacksWithMeetupUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val attendanceRepository: AttendanceRepository,
    private val meetupRepository: MeetupRepository
) {

    fun execute(userId: UUID): List<FeedbackWithMeetup> {
        val feedbacks = feedbackRepository.getFeedbackForUser(userId)

        return feedbacks.mapNotNull { feedback ->
            val attendance = attendanceRepository.findById(feedback.attendanceId)
            
            val meetup = attendance?.let { att ->
                meetupRepository.findById(att.meetupId)
            }

            meetup?.let {
                FeedbackWithMeetup(
                    feedback = feedback,
                    meetup = it
                )
            }
        }
    }
}

data class FeedbackWithMeetup(
    val feedback: Feedback,
    val meetup: Meetup
)

