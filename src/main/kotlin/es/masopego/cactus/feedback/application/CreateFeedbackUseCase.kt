package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateFeedbackUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val attendanceRepository: AttendanceRepository
) {

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

class AttendanceNotFoundException(userId: UUID, meetupId: UUID) :
    RuntimeException("Attendance not found for user $userId and meetup $meetupId")

class AttendanceNotConfirmedException(attendanceId: UUID) :
    RuntimeException("Attendance $attendanceId is not confirmed")

class FeedbackAlreadyExistsException(attendanceId: UUID) :
    RuntimeException("Feedback already exists for attendance $attendanceId")

class InvalidRatingException(rating: Short) :
    RuntimeException("Invalid rating: $rating. Rating must be between 1 and 5")

