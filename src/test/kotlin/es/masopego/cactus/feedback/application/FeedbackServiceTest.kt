package es.masopego.cactus.feedback.application

import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.fixtures.FeedbackFixtures.createFeedback
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class FeedbackServiceTest {

    private lateinit var feedbackRepository: FeedbackRepository
    private lateinit var service: FeedbackService

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        feedbackRepository = mockk()
        service = FeedbackService(feedbackRepository)
    }

    @Test
    fun `should return all feedbacks for a user`() {
        val attendanceId1 = UUID.randomUUID()
        val attendanceId2 = UUID.randomUUID()
        val attendanceId3 = UUID.randomUUID()

        val feedback1 = createFeedback(attendanceId = attendanceId1, rating = 5, comment = "Excellent!")
        val feedback2 = createFeedback(attendanceId = attendanceId2, rating = 4, comment = "Very good")
        val feedback3 = createFeedback(attendanceId = attendanceId3, rating = 3, comment = "Average")

        val expectedFeedbacks = listOf(feedback1, feedback2, feedback3)

        every { feedbackRepository.getFeedbackForUser(userId) } returns expectedFeedbacks

        val result = service.getAllFeedbacksByUser(userId)

        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(expectedFeedbacks, result)
        assertTrue(result.any { it.rating.toInt() == 5 })
        assertTrue(result.any { it.rating.toInt() == 4 })
        assertTrue(result.any { it.rating.toInt() == 3 })
        verify { feedbackRepository.getFeedbackForUser(userId) }
    }

    @Test
    fun `should return empty list when user has no feedbacks`() {
        every { feedbackRepository.getFeedbackForUser(userId) } returns emptyList()

        val result = service.getAllFeedbacksByUser(userId)

        assertNotNull(result)
        assertTrue(result.isEmpty())
        verify { feedbackRepository.getFeedbackForUser(userId) }
    }
}
