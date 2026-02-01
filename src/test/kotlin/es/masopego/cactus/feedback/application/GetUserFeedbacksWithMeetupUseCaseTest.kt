package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.fixtures.AttendanceFixtures.createConfirmedAttendance
import es.masopego.cactus.fixtures.FeedbackFixtures.createFeedback
import es.masopego.cactus.fixtures.MeetupFixtures.createMeetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class GetUserFeedbacksWithMeetupUseCaseTest {

    private lateinit var feedbackRepository: FeedbackRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var meetupRepository: MeetupRepository
    private lateinit var useCase: GetUserFeedbacksWithMeetupUseCase

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        feedbackRepository = mockk()
        attendanceRepository = mockk()
        meetupRepository = mockk()
        useCase = GetUserFeedbacksWithMeetupUseCase(feedbackRepository, attendanceRepository, meetupRepository)
    }

    @Test
    fun `should return feedbacks with meetup details when user has feedbacks`() {
        val attendanceId1 = UUID.randomUUID()
        val attendanceId2 = UUID.randomUUID()
        val meetupId1 = UUID.randomUUID()
        val meetupId2 = UUID.randomUUID()

        val feedback1 = createFeedback(
            attendanceId = attendanceId1,
            rating = 5,
            comment = "Excellent event!"
        )
        val feedback2 = createFeedback(
            attendanceId = attendanceId2,
            rating = 4,
            comment = "Very good!"
        )

        val attendance1 = createConfirmedAttendance(
            id = attendanceId1,
            userId = userId,
            meetupId = meetupId1
        )
        val attendance2 = createConfirmedAttendance(
            id = attendanceId2,
            userId = userId,
            meetupId = meetupId2
        )

        val meetup1 =
            createMeetup(id = meetupId1, title = "Kotlin Meetup", startDate = LocalDateTime.now().minusDays(10))
        val meetup2 = createMeetup(id = meetupId2, title = "Java Meetup", startDate = LocalDateTime.now().minusDays(5))

        every { feedbackRepository.getFeedbackForUser(userId) } returns listOf(feedback1, feedback2)
        every { attendanceRepository.findById(attendanceId1) } returns attendance1
        every { attendanceRepository.findById(attendanceId2) } returns attendance2
        every { meetupRepository.findById(meetupId1) } returns meetup1
        every { meetupRepository.findById(meetupId2) } returns meetup2

        val result = useCase.execute(userId)

        assertEquals(2, result.size)

        val firstResult = result.find { it.meetup.title == "Kotlin Meetup" }
        assertNotNull(firstResult)
        assertEquals(feedback1.id, firstResult!!.feedback.id)
        assertEquals(5, firstResult.feedback.rating)
        assertEquals("Excellent event!", firstResult.feedback.comment)
        assertEquals("Kotlin Meetup", firstResult.meetup.title)

        val secondResult = result.find { it.meetup.title == "Java Meetup" }
        assertNotNull(secondResult)
        assertEquals(feedback2.id, secondResult!!.feedback.id)
        assertEquals(4, secondResult.feedback.rating)

        verify { feedbackRepository.getFeedbackForUser(userId) }
        verify { attendanceRepository.findById(attendanceId1) }
        verify { attendanceRepository.findById(attendanceId2) }
        verify { meetupRepository.findById(meetupId1) }
        verify { meetupRepository.findById(meetupId2) }
    }

    @Test
    fun `should return empty list when user has no feedbacks`() {
        every { feedbackRepository.getFeedbackForUser(userId) } returns emptyList()

        val result = useCase.execute(userId)

        assertTrue(result.isEmpty())
        verify { feedbackRepository.getFeedbackForUser(userId) }
    }
    
}

