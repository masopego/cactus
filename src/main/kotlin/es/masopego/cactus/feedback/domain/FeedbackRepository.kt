package es.masopego.cactus.feedback.domain

import java.util.*

interface FeedbackRepository {
    fun getFeedbackForUser(userId: UUID): List<Feedback>
    fun save(feedback: Feedback): Feedback
    fun findByAttendanceId(attendanceId: UUID): Feedback?
}