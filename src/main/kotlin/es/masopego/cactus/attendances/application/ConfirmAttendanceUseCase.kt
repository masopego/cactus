package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.attendances.domain.errors.UserAlreadyRegistered
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
        val existPreviousAttendance = existingAttendance != null
        if (existPreviousAttendance) {
            return Result.failure(UserAlreadyRegistered(request.userId, request.meetupId))
        }

        repository.confirmAttendance(request.userId, request.meetupId)

        return Result.success(Unit)
    }
}

data class ConfirmAttendanceRequest(
    val userId: UUID,
    val meetupId: UUID
)

