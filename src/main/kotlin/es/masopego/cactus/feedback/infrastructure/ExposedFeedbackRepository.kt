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

@Repository
class ExposedFeedbackRepository : FeedbackRepository {
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