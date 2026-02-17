package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * This use case manages the attendance confirmation process, differentiating it
 * from simple interest registration. It implements business validations to ensure
 * that confirmation only occurs under appropriate conditions.
 *
 * @property meetupRepository Repository for accessing meetup data
 * @property repository Repository for managing attendances
 */
@Service
class ConfirmAttendanceUseCase(
    val meetupRepository: MeetupRepository,
    val repository: AttendanceRepository
) {

    /**
     * Executes the confirmation of a user's attendance to a meetup.
     *
     * @param request Object containing the user ID and meetup ID
     * @return [Result] with Unit if successful, or a specific error if it fails:
     *         - [MeetupNotFound] if the meetup doesn't exist
     *         - [MeetupAlreadyStarted] if the meetup has already started
     *         - [AttendanceNotFound] if there's no prior interest registration
     *         - [AttendanceAlreadyConfirmed] if it was already confirmed previously
     *
     */
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

/**
 * Exception thrown when an attendance record is not found.
 *
 * @param userId
 * @param meetupId
 */
class AttendanceNotFound(userId: UUID, meetupId: UUID) :
    RuntimeException("Attendance not found for user $userId and meetup $meetupId")

/**
 * Exception thrown when attempting to confirm an already confirmed attendance.
 *
 * @param attendanceId
 */
class AttendanceAlreadyConfirmed(attendanceId: UUID) :
    RuntimeException("Attendance $attendanceId is already confirmed")

