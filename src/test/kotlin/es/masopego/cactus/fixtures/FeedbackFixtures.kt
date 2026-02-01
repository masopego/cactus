package es.masopego.cactus.fixtures

import es.masopego.cactus.feedback.domain.Feedback
import java.util.*

object FeedbackFixtures {

    fun createFeedback(
        id: UUID = UUID.randomUUID(),
        attendanceId: UUID = UUID.randomUUID(),
        rating: Short = 5,
        comment: String = "Great meetup!"
    ) = Feedback(
        id = id,
        attendanceId = attendanceId,
        rating = rating,
        comment = comment
    )
    
}

