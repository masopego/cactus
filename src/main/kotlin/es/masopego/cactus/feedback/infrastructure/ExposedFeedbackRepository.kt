package es.masopego.cactus.feedback.infrastructure

import es.masopego.cactus.attendances.infrastructure.persistence.entity.Attendances
import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.feedback.infrastructure.persistence.entity.Feedbacks
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Implementation of [FeedbackRepository] using Exposed ORM.
 */
@Repository
class ExposedFeedbackRepository : FeedbackRepository {
    /**
     * Retrieves all feedback submitted by a specific user.
     *
     * @param userId
     * @return List of all feedback from the user
     */
    override fun getFeedbackForUser(userId: UUID): List<Feedback> = transaction {
        Feedbacks
            .innerJoin(Attendances, { attendanceId }, { id })
            .select { Attendances.user eq userId }
            .map { row ->
                Feedback(
                    id = row[Feedbacks.id],
                    attendanceId = row[Feedbacks.attendanceId],
                    rating = row[Feedbacks.rating],
                    comment = row[Feedbacks.comment]
                )
            }
    }

    /**
     * Saves new feedback to the database.
     *
     * @param feedback
     * @return The same feedback object after successful insertion
     */
    override fun save(feedback: Feedback): Feedback = transaction {
        val feedbackId = feedback.id

        Feedbacks.insert {
            it[id] = feedbackId
            it[attendanceId] = feedback.attendanceId
            it[rating] = feedback.rating
            it[comment] = feedback.comment
        }

        feedback
    }

    /**
     * Finds feedback associated with a specific attendance.
     *
     * @param attendanceId
     * @return Feedback if found, null otherwise
     */
    override fun findByAttendanceId(attendanceId: UUID): Feedback? = transaction {
        Feedbacks
            .select { Feedbacks.attendanceId eq attendanceId }
            .singleOrNull()
            ?.let { row ->
                Feedback(
                    id = row[Feedbacks.id],
                    attendanceId = row[Feedbacks.attendanceId],
                    rating = row[Feedbacks.rating],
                    comment = row[Feedbacks.comment]
                )
            }
    }
}