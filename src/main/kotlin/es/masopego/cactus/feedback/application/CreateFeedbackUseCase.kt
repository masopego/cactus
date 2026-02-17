package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Use case for creating feedback about a meetup.
 *
 * This use case allows users to submit feedback about meetups they have attended. It validates that:
 * - The user has an attendance record for the meetup
 * - The attendance has been confirmed
 * - No feedback already exists for this attendance
 * - The rating is within valid range (1-5)
 *
 * @property feedbackRepository
 * @property attendanceRepository
 */
@Service
class CreateFeedbackUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val attendanceRepository: AttendanceRepository
) {

    /**
     * @param request Object containing user ID, meetup ID, rating, and comment
     * @return [Result] with the created feedback if successful, or a specific error if it fails:
     *         - [AttendanceNotFoundException] if the user hasn't registered for the meetup
     *         - [AttendanceNotConfirmedException] if the attendance isn't confirmed
     *         - [FeedbackAlreadyExistsException] if feedback already exists
     *         - [InvalidRatingException] if the rating is not between 1 and 5
     *
     */
    fun execute(request: CreateFeedbackRequest): Result<Feedback> {
        val attendance = attendanceRepository.getAttendance(request.userId, request.meetupId)
            ?: return Result.failure(AttendanceNotFoundException(request.userId, request.meetupId))

        if (attendance.confirmed == null) {
            return Result.failure(AttendanceNotConfirmedException(attendance.id))
        }

        val existingFeedback = feedbackRepository.findByAttendanceId(attendance.id)
        if (existingFeedback != null) {
            return Result.failure(FeedbackAlreadyExistsException(attendance.id))
        }

        if (request.rating !in 1..5) {
            return Result.failure(InvalidRatingException(request.rating))
        }

        val feedback = Feedback(
            id = UUID.randomUUID(),
            attendanceId = attendance.id,
            rating = request.rating,
            comment = request.comment
        )

        val savedFeedback = feedbackRepository.save(feedback)
        return Result.success(savedFeedback)
    }
}

data class CreateFeedbackRequest(
    val userId: UUID,
    val meetupId: UUID,
    val rating: Short,
    val comment: String
)

/**
 * Exception thrown when the user's attendance for the meetup is not found.
 *
 * @param userId
 * @param meetupId
 */
class AttendanceNotFoundException(userId: UUID, meetupId: UUID) :
    RuntimeException("Attendance not found for user $userId and meetup $meetupId")

/**
 * Exception thrown when attempting to create feedback for an unconfirmed attendance.
 *
 * @param attendanceId
 */
class AttendanceNotConfirmedException(attendanceId: UUID) :
    RuntimeException("Attendance $attendanceId is not confirmed")

/**
 * Exception thrown when feedback already exists for an attendance.
 *
 * @param attendanceId
 */
class FeedbackAlreadyExistsException(attendanceId: UUID) :
    RuntimeException("Feedback already exists for attendance $attendanceId")

/**
 * Exception thrown when the rating value is invalid.
 *
 * @param rating The invalid rating value provided
 */
class InvalidRatingException(rating: Short) :
    RuntimeException("Invalid rating: $rating. Rating must be between 1 and 5")

