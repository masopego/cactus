package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * This use case allows users to cancel their attendance to a meetup before it's confirmed.
 * It enforces the business rule that only unconfirmed attendances can be deleted,
 * preventing users from backing out after confirming their attendance.
 *
 * @property attendanceRepository
 */
@Service
class DeleteAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository
) {

    /**
     * Executes the deletion of an unconfirmed attendance.
     *
     * @param request Contains the user ID and meetup ID identifying the attendance to delete
     * @return [Result] with Unit if successful, or a specific error if it fails:
     *         - [AttendanceNotFoundForDeletion] if no attendance exists for user/meetup combination
     *         - [CannotDeleteConfirmedAttendance] if the attendance has been confirmed
     *         - [AttendanceDeletionFailed] if the database deletion operation fails
     *
     */
    fun execute(request: DeleteAttendanceRequest): Result<Unit> {
        val attendance = attendanceRepository.getAttendance(request.userId, request.meetupId)
            ?: return Result.failure(AttendanceNotFoundForDeletion(request.userId, request.meetupId))

        if (attendance.confirmed != null) {
            return Result.failure(CannotDeleteConfirmedAttendance(attendance.id))
        }

        val deleted = attendanceRepository.deleteAttendance(attendance.id)

        return if (deleted) {
            Result.success(Unit)
        } else {
            Result.failure(AttendanceDeletionFailed(attendance.id))
        }
    }
}

data class DeleteAttendanceRequest(
    val userId: UUID,
    val meetupId: UUID
)

/**
 * Exception thrown when attempting to delete a non-existent attendance.
 *
 * This indicates that the user has no attendance record for the specified meetup,
 * either because they never registered or the record was already deleted.
 *
 * @param userId
 * @param meetupId
 */
class AttendanceNotFoundForDeletion(userId: UUID, meetupId: UUID) :
    RuntimeException("Attendance not found for user $userId and meetup $meetupId")

/**
 * Exception thrown when attempting to delete a confirmed attendance.
 *
 * @param attendanceId
 */
class CannotDeleteConfirmedAttendance(attendanceId: UUID) :
    RuntimeException("Cannot delete confirmed attendance: $attendanceId")

/**
 * Exception thrown when the database deletion operation fails.
 *
 * @param attendanceId
 */
class AttendanceDeletionFailed(attendanceId: UUID) :
    RuntimeException("Failed to delete attendance: $attendanceId")

