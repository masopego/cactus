package es.masopego.cactus.attendances.infrastructure

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.infrastructure.persistence.entity.Attendances
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class ExposedAttendanceRepository : AttendanceRepository {
    override fun getAttendance(userId: UUID, meetupId: UUID): Attendance? =
        transaction {
            Attendances.select { (Attendances.user eq userId) and (Attendances.meetup eq meetupId) }
                .map { row ->
                    Attendance(
                        id = row[Attendances.id],
                        userId = row[Attendances.user].value,
                        meetupId = row[Attendances.meetup],
                        confirmed = row[Attendances.confirmed]
                    )
                }.firstOrNull()
        }

    override fun findById(attendanceId: UUID): Attendance? =
        transaction {
            Attendances.select { Attendances.id eq attendanceId }
                .map { row ->
                    Attendance(
                        id = row[Attendances.id],
                        userId = row[Attendances.user].value,
                        meetupId = row[Attendances.meetup],
                        confirmed = row[Attendances.confirmed]
                    )
                }.firstOrNull()
        }

    override fun registerInterest(userId: UUID, meetupId: UUID): Attendance = transaction {
        val attendanceId = Attendances.insert {
            it[Attendances.user] = userId
            it[Attendances.meetup] = meetupId
            it[confirmed] = null
        }[Attendances.id]

        Attendance(
            id = attendanceId,
            userId = userId,
            meetupId = meetupId,
            confirmed = null
        )
    }

    override fun confirmExistingAttendance(attendanceId: UUID): Attendance? = transaction {
        val attendance = findById(attendanceId) ?: return@transaction null

        Attendances.update({ Attendances.id eq attendanceId }) {
            it[confirmed] = LocalDateTime.now()
        }

        attendance.copy(confirmed = LocalDateTime.now())
    }

    override fun deleteAttendance(attendanceId: UUID): Boolean = transaction {
        val attendance = findById(attendanceId) ?: return@transaction false

        if (attendance.confirmed != null) {
            return@transaction false
        }

        val deletedCount = Attendances.deleteWhere { Attendances.id eq attendanceId }

        deletedCount > 0
    }

    override fun getAttendancesForUser(userId: UUID): List<Attendance> = transaction {
        Attendances.select { Attendances.user eq userId }
            .map { row ->
                Attendance(
                    id = row[Attendances.id],
                    userId = row[Attendances.user].value,
                    meetupId = row[Attendances.meetup],
                    confirmed = row[Attendances.confirmed]
                )
            }
    }

}