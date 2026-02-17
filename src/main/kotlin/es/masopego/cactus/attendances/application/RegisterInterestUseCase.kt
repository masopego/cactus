package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.attendances.domain.errors.UserAlreadyRegistered
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Use case for registering a user's interest in attending a meetup.
 *
 * - Verifies that the meetup exists
 * - Checks that the meetup hasn't started yet
 * - Validates that the user is not already registered
 *
 * @property meetupRepository
 * @property attendanceRepository
 */
@Service
class RegisterInterestUseCase(
    private val meetupRepository: MeetupRepository,
    private val attendanceRepository: AttendanceRepository
) {

    /**
     * @param request Object containing the user ID and meetup ID
     * @return [Result] with the created attendance if successful, or a specific error if it fails
     *
     */
    fun execute(request: RegisterInterestRequest): Result<Attendance> {
        val meetup = meetupRepository.findById(request.meetupId)
            ?: return Result.failure(MeetupNotFound(request.meetupId))

        if (!meetup.canConfirmAttendance()) {
            return Result.failure(MeetupAlreadyStarted(request.meetupId))
        }

        val existingAttendance = attendanceRepository.getAttendance(request.userId, request.meetupId)
        if (existingAttendance != null) {
            return Result.failure(UserAlreadyRegistered(request.userId, request.meetupId))
        }

        val attendance = attendanceRepository.registerInterest(request.userId, request.meetupId)
        return Result.success(attendance)
    }
}

data class RegisterInterestRequest(
    val userId: UUID,
    val meetupId: UUID
)


