package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository
) {

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

class AttendanceNotFoundForDeletion(userId: UUID, meetupId: UUID) :
    RuntimeException("Attendance not found for user $userId and meetup $meetupId")

class CannotDeleteConfirmedAttendance(attendanceId: UUID) :
    RuntimeException("Cannot delete confirmed attendance: $attendanceId")

class AttendanceDeletionFailed(attendanceId: UUID) :
    RuntimeException("Failed to delete attendance: $attendanceId")

