package es.masopego.cactus.attendances.domain

import java.util.*

interface AttendanceRepository {

    fun getAttendance(userId: UUID, meetupId: UUID): Attendance?
    fun findById(attendanceId: UUID): Attendance?
    fun confirmAttendance(userId: UUID, meetupId: UUID)
    fun getAttendancesForUser(userId: UUID): List<Attendance>
}