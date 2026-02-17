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

/**
 * Implementation of [AttendanceRepository] using Exposed ORM.
 *
 * This repository handles all database operations for attendances using
 * the Exposed SQL library. All operations are executed within transactions
 * to ensure data consistency.
 */
@Repository
class ExposedAttendanceRepository : AttendanceRepository {
    /**
     * Retrieves an attendance by user and meetup combination.
     *
     * @param userId
     * @param meetupId
     * @return Mapped Attendance domain object or null if not found
     */
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

    /**
     * Finds an attendance by its unique identifier.
     *
     * @param attendanceId
     * @return Mapped Attendance domain object or null if not found
     */
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

    /**
     * Registers a user's interest in a meetup.
     *
     * Creates a new attendance record with null confirmed date,
     * indicating interest but not yet confirmed attendance.
     *
     * @param userId
     * @param meetupId
     * @return Newly created Attendance with assigned UUID
     */
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

    /**
     * Confirms an existing attendance.
     *
     * Updates the attendance record setting the confirmed timestamp to now.
     * This marks the user's commitment to attend the meetup.
     *
     * @param attendanceId
     * @return Updated Attendance with confirmation timestamp, or null if not found
     */
    override fun confirmExistingAttendance(attendanceId: UUID): Attendance? = transaction {
        val attendance = findById(attendanceId) ?: return@transaction null

        Attendances.update({ Attendances.id eq attendanceId }) {
            it[confirmed] = LocalDateTime.now()
        }

        attendance.copy(confirmed = LocalDateTime.now())
    }

    /**
     * Deletes an unconfirmed attendance.
     *
     * Business rule enforcement: Only allows deletion of attendances that
     * haven't been confirmed. Confirmed attendances cannot be deleted.
     *
     * @param attendanceId
     * @return true if deleted successfully, false if not found or already confirmed
     */
    override fun deleteAttendance(attendanceId: UUID): Boolean = transaction {
        val attendance = findById(attendanceId) ?: return@transaction false

        if (attendance.confirmed != null) {
            return@transaction false
        }

        val deletedCount = Attendances.deleteWhere { Attendances.id eq attendanceId }

        deletedCount > 0
    }

    /**
     * Retrieves all attendances for a specific user.
     *
     * @param userId
     * @return List of all user's attendances (both confirmed and unconfirmed)
     */
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