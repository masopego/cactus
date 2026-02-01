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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class GetUserFeedbackStatsUseCaseTest {

    private lateinit var feedbackRepository: FeedbackRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var meetupRepository: MeetupRepository
    private lateinit var useCase: GetUserFeedbackStatsUseCase

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        feedbackRepository = mockk()
        attendanceRepository = mockk()
        meetupRepository = mockk()
        useCase = GetUserFeedbackStatsUseCase(feedbackRepository, attendanceRepository, meetupRepository)
    }

    @Test
    fun `should return correct stats when user has feedbacks and confirmed attendances`() {
        val attendanceId1 = UUID.randomUUID()
        val attendanceId2 = UUID.randomUUID()
        val attendanceId3 = UUID.randomUUID()
        val meetupId1 = UUID.randomUUID()
        val meetupId2 = UUID.randomUUID()
        val meetupId3 = UUID.randomUUID()

        val feedback1 = createFeedback(
            attendanceId = attendanceId1,
            rating = 5,
            comment = "Excellent!"
        )
        val feedback2 = createFeedback(
            attendanceId = attendanceId2,
            rating = 4,
            comment = "Good meetup"
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
        val attendance3 = createConfirmedAttendance(
            id = attendanceId3,
            userId = userId,
            meetupId = meetupId3
        )

        val meetup1 = createMeetup(id = meetupId1, title = "Meetup 1", startDate = LocalDateTime.now().minusDays(10))
        val meetup2 = createMeetup(id = meetupId2, title = "Meetup 2", startDate = LocalDateTime.now().minusDays(5))

        every { feedbackRepository.getFeedbackForUser(userId) } returns listOf(feedback1, feedback2)
        every { attendanceRepository.findById(attendanceId1) } returns attendance1
        every { attendanceRepository.findById(attendanceId2) } returns attendance2
        every { meetupRepository.findById(meetupId1) } returns meetup1
        every { meetupRepository.findById(meetupId2) } returns meetup2
        every { attendanceRepository.getAttendancesForUser(userId) } returns listOf(
            attendance1,
            attendance2,
            attendance3
        )

        val stats = useCase.execute(userId)

        assertEquals(2, stats.totalFeedbacks)
        assertEquals(3, stats.totalConfirmedAttendances)
        assertEquals(66, stats.feedbackPercentage) // 2/3 * 100 = 66
        assertEquals(2, stats.eventsWithFeedback.size)
        assertTrue(stats.eventsWithFeedback.any { it.title == "Meetup 1" && it.rating.toInt() == 5 })
        assertTrue(stats.eventsWithFeedback.any { it.title == "Meetup 2" && it.rating.toInt() == 4 })
        verify { feedbackRepository.getFeedbackForUser(userId) }
        verify { attendanceRepository.getAttendancesForUser(userId) }
    }

    @Test
    fun `should return zero percentage when user has no feedbacks`() {
        val attendanceId = UUID.randomUUID()
        val meetupId = UUID.randomUUID()

        val attendance = createConfirmedAttendance(
            id = attendanceId,
            userId = userId,
            meetupId = meetupId
        )

        every { feedbackRepository.getFeedbackForUser(userId) } returns emptyList()
        every { attendanceRepository.getAttendancesForUser(userId) } returns listOf(attendance)

        val stats = useCase.execute(userId)

        assertEquals(0, stats.totalFeedbacks)
        assertEquals(1, stats.totalConfirmedAttendances)
        assertEquals(0, stats.feedbackPercentage)
        assertEquals(0, stats.eventsWithFeedback.size)
    }

    @Test
    fun `should return 100 percent when user has feedback for all confirmed attendances`() {
        val attendanceId1 = UUID.randomUUID()
        val attendanceId2 = UUID.randomUUID()
        val meetupId1 = UUID.randomUUID()
        val meetupId2 = UUID.randomUUID()

        val feedback1 = createFeedback(
            attendanceId = attendanceId1,
            rating = 5,
            comment = "Great!"
        )
        val feedback2 = createFeedback(
            attendanceId = attendanceId2,
            rating = 4,
            comment = "Good!"
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

        val meetup1 = createMeetup(id = meetupId1, title = "Meetup 1", startDate = LocalDateTime.now().minusDays(10))
        val meetup2 = createMeetup(id = meetupId2, title = "Meetup 2", startDate = LocalDateTime.now().minusDays(5))

        every { feedbackRepository.getFeedbackForUser(userId) } returns listOf(feedback1, feedback2)
        every { attendanceRepository.findById(attendanceId1) } returns attendance1
        every { attendanceRepository.findById(attendanceId2) } returns attendance2
        every { meetupRepository.findById(meetupId1) } returns meetup1
        every { meetupRepository.findById(meetupId2) } returns meetup2
        every { attendanceRepository.getAttendancesForUser(userId) } returns listOf(attendance1, attendance2)

        val stats = useCase.execute(userId)

        assertEquals(2, stats.totalFeedbacks)
        assertEquals(2, stats.totalConfirmedAttendances)
        assertEquals(100, stats.feedbackPercentage)
        assertEquals(2, stats.eventsWithFeedback.size)
    }
    
}
