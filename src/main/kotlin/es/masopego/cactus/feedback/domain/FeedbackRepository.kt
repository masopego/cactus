package es.masopego.cactus.feedback.domain

import java.util.*

/**
 * Repository for managing user feedback on meetups.
 */
interface FeedbackRepository {
    /**
     * Gets all feedback submitted by a user.
     *
     * @param userId
     * @return List of all the user's feedback
     */
    fun getFeedbackForUser(userId: UUID): List<Feedback>

    /**
     * Saves new feedback to the database.
     *
     * @param feedback
     * @return The saved feedback with updated data
     */
    fun save(feedback: Feedback): Feedback

    /**
     * Finds feedback associated with a specific attendance.
     *
     * @param attendanceId
     * @return The found feedback or null if it doesn't exist
     */
    fun findByAttendanceId(attendanceId: UUID): Feedback?
}