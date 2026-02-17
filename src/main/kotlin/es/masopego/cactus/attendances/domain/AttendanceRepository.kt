package es.masopego.cactus.attendances.domain

import java.util.*

/**
 * Repository for managing user attendances to meetups.
 *
 * This interface defines CRUD and query operations for the [Attendance] entity.
 * It manages both interest registration and attendance confirmation for events.
 */
interface AttendanceRepository {

    /**
     * Gets a specific user's attendance to a meetup.
     *
     * @param userId
     * @param meetupId
     * @return The attendance if it exists, null otherwise
     */
    fun getAttendance(userId: UUID, meetupId: UUID): Attendance?

    /**
     * Finds an attendance by its unique identifier.
     *
     * @param attendanceId
     * @return The found attendance or null if it doesn't exist
     */
    fun findById(attendanceId: UUID): Attendance?

    /**
     * Registers a user's interest in attending a meetup. Creates a new unconfirmed attendance entry.
     *
     * @param userId
     * @param meetupId
     * @return The newly created attendance
     */
    fun registerInterest(userId: UUID, meetupId: UUID): Attendance

    /**
     * Confirms an existing attendance. Updates the attendance confirmation date.
     *
     * @param attendanceId
     * @return The confirmed attendance or null if it doesn't exist
     */
    fun confirmExistingAttendance(attendanceId: UUID): Attendance?

    /**
     * Deletes an unconfirmed attendance. Only unconfirmed attendances can be deleted.
     *
     * @param attendanceId
     * @return true if successfully deleted, false if it couldn't be deleted or doesn't exist
     */
    fun deleteAttendance(attendanceId: UUID): Boolean

    /**
     * Gets all attendances for a user.
     *
     * @param userId
     * @return List of all the user's attendances
     */
    fun getAttendancesForUser(userId: UUID): List<Attendance>
}