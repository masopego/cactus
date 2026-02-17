package es.masopego.cactus.feedback.application

import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service for managing feedback queries.
 *
 * This service provides simplified access to feedback data submitted by users.
 *
 * @property feedbackRepository
 */
@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {

    /**
     * Retrieves all feedback submitted by a specific user.
     *
     * @param userId
     * @return List of all feedback from the user (empty list if none)
     */
    fun getAllFeedbacksByUser(userId: UUID): List<Feedback> {
        return feedbackRepository.getFeedbackForUser(userId)
    }
}

