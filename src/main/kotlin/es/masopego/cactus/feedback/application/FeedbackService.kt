package es.masopego.cactus.feedback.application

import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {

    fun getAllAttendancesByUser(userId: UUID): List<Feedback> {
        return feedbackRepository.getFeedbackForUser(userId)
    }
}

