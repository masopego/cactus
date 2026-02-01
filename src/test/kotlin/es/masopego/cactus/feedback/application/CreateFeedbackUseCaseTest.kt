package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.Feedback
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.fixtures.AttendanceFixtures.createConfirmedAttendance
import es.masopego.cactus.fixtures.AttendanceFixtures.createUnconfirmedAttendance
import es.masopego.cactus.fixtures.FeedbackFixtures.createFeedback
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateFeedbackUseCaseTest {

    private lateinit var feedbackRepository: FeedbackRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var useCase: CreateFeedbackUseCase

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()
    private val attendanceId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        feedbackRepository = mockk()
        attendanceRepository = mockk()
        useCase = CreateFeedbackUseCase(feedbackRepository, attendanceRepository)
    }

    @Test
    fun `should create feedback successfully when attendance is confirmed and no feedback exists`() {
        val confirmedAttendance = createConfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val request = CreateFeedbackRequest(
            userId = userId,
            meetupId = meetupId,
            rating = 5,
            comment = "Great meetup!"
        )
        val feedbackSlot = slot<Feedback>()

        every { attendanceRepository.getAttendance(userId, meetupId) } returns confirmedAttendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns null
        every { feedbackRepository.save(capture(feedbackSlot)) } answers { feedbackSlot.captured }

        val result = useCase.execute(request)

        assertTrue(result.isSuccess)
        val feedback = result.getOrNull()
        assertNotNull(feedback)
        assertEquals(attendanceId, feedback!!.attendanceId)
        assertEquals(5, feedback.rating)
        assertEquals("Great meetup!", feedback.comment)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { feedbackRepository.findByAttendanceId(attendanceId) }
        verify { feedbackRepository.save(any()) }
    }

    @Test
    fun `should fail when attendance does not exist`() {
        val request = CreateFeedbackRequest(
            userId = userId,
            meetupId = meetupId,
            rating = 5,
            comment = "Great meetup!"
        )

        every { attendanceRepository.getAttendance(userId, meetupId) } returns null

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is AttendanceNotFoundException)
        assertEquals("Attendance not found for user $userId and meetup $meetupId", exception!!.message)
    }

    @Test
    fun `should fail when attendance is not confirmed`() {
        val unconfirmedAttendance = createUnconfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val request = CreateFeedbackRequest(
            userId = userId,
            meetupId = meetupId,
            rating = 5,
            comment = "Great meetup!"
        )

        every { attendanceRepository.getAttendance(userId, meetupId) } returns unconfirmedAttendance

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is AttendanceNotConfirmedException)
        assertEquals("Attendance $attendanceId is not confirmed", exception!!.message)
    }

    @Test
    fun `should fail when feedback already exists for the attendance`() {
        val confirmedAttendance = createConfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val existingFeedback = createFeedback(attendanceId = attendanceId, rating = 4, comment = "Already submitted")
        val request = CreateFeedbackRequest(
            userId = userId,
            meetupId = meetupId,
            rating = 5,
            comment = "Great meetup!"
        )

        every { attendanceRepository.getAttendance(userId, meetupId) } returns confirmedAttendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns existingFeedback

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is FeedbackAlreadyExistsException)
        assertEquals("Feedback already exists for attendance $attendanceId", exception!!.message)
    }

    @Test
    fun `should fail when rating is below minimum value`() {
        val confirmedAttendance = createConfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val request = CreateFeedbackRequest(
            userId = userId,
            meetupId = meetupId,
            rating = 0,
            comment = "Bad rating value"
        )

        every { attendanceRepository.getAttendance(userId, meetupId) } returns confirmedAttendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns null

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is InvalidRatingException)
        assertEquals("Invalid rating: 0. Rating must be between 1 and 5", exception!!.message)
    }

    @Test
    fun `should fail when rating is above maximum value`() {
        val confirmedAttendance = createConfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val request = CreateFeedbackRequest(
            userId = userId,
            meetupId = meetupId,
            rating = 6,
            comment = "Bad rating value"
        )

        every { attendanceRepository.getAttendance(userId, meetupId) } returns confirmedAttendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns null

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is InvalidRatingException)
        assertEquals("Invalid rating: 6. Rating must be between 1 and 5", exception!!.message)
    }
    
}
