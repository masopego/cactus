package es.masopego.cactus.attendances.domain

import java.util.*

interface AttendanceRepository {

    fun getAttendance(userId: UUID, meetupId: UUID): Attendance?
    fun findById(attendanceId: UUID): Attendance?
    fun registerInterest(userId: UUID, meetupId: UUID): Attendance
    fun confirmExistingAttendance(attendanceId: UUID): Attendance?
    fun deleteAttendance(attendanceId: UUID): Boolean
    fun getAttendancesForUser(userId: UUID): List<Attendance>
}