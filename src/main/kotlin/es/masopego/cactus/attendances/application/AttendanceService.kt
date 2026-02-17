package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * This service provides simplified access to attendance data.
 *
 * @property repository Attendance repository for data access
 */
@Service
class AttendanceService(
    val repository: AttendanceRepository
) {
    /**
     * Gets a specific attendance for a user and meetup combination.
     *
     * @param userId Identifier of the user
     * @param meetupId Identifier of the meetup
     * @return The attendance if it exists, null otherwise
     */
    fun getAttendance(userId: UUID, meetupId: UUID): Attendance? =
        repository.getAttendance(userId, meetupId)

    /**
     * Retrieves all attendances for a specific user.
     *
     * @param userId Identifier of the user
     * @return List of all attendances for the user (empty list if none)
     */
    fun getAllAttendancesByUser(userId: UUID): List<Attendance> =
        repository.getAttendancesForUser(userId)

}