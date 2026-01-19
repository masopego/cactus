package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ConfirmAttendanceUseCase(
    val meetupRepository: MeetupRepository,
    val repository: AttendanceRepository
) {

    fun execute(
        request: ConfirmAttendanceRequest
    ): Result<Unit> {

        val meetup = meetupRepository.findById(request.meetupId)
            ?: return Result.failure(MeetupNotFound(request.meetupId))

        if (!meetup.canConfirmAttendance()) {
            return Result.failure(MeetupAlreadyStarted(request.meetupId))
        }

        val existingAttendance = repository.getAttendance(request.userId, request.meetupId)
            ?: return Result.failure(AttendanceNotFound(request.userId, request.meetupId))

        if (existingAttendance.confirmed != null) {
            return Result.failure(AttendanceAlreadyConfirmed(existingAttendance.id))
        }

        repository.confirmExistingAttendance(existingAttendance.id)

        return Result.success(Unit)
    }
}

data class ConfirmAttendanceRequest(
    val userId: UUID,
    val meetupId: UUID
)

class AttendanceNotFound(userId: UUID, meetupId: UUID) :
    RuntimeException("Attendance not found for user $userId and meetup $meetupId")

class AttendanceAlreadyConfirmed(attendanceId: UUID) :
    RuntimeException("Attendance $attendanceId is already confirmed")

