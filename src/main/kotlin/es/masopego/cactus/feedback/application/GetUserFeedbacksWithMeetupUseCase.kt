package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Use case for retrieving user feedbacks enriched with complete meetup information.
 *
 * @property feedbackRepository
 * @property attendanceRepository
 * @property meetupRepository
 */
@Service
class GetUserFeedbacksWithMeetupUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val attendanceRepository: AttendanceRepository,
    private val meetupRepository: MeetupRepository
) {

    /**
     * Retrieves all user feedbacks with their associated meetup information.
     *
     * @param userId
     * @return List of [FeedbackWithMeetup] containing complete feedback and meetup data.
     *         Returns empty list if user has no feedbacks or all feedbacks reference deleted data.
     *
     * @see FeedbackWithMeetup
     */
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

