package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AttendanceService(
    val repository: AttendanceRepository
) {
    fun getAttendance(userId: UUID, meetupId: UUID): Attendance? =
        repository.getAttendance(userId, meetupId)

    fun getAllAttendancesByUser(userId: UUID): List<Attendance> =
        repository.getAttendancesForUser(userId)

}