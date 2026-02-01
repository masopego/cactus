package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.FeedbackRepository
import es.masopego.cactus.fixtures.AttendanceFixtures.createAttendance
import es.masopego.cactus.fixtures.AttendanceFixtures.createUnconfirmedAttendance
import es.masopego.cactus.fixtures.FeedbackFixtures.createFeedback
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CheckFeedbackStatusUseCaseTest {

    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var feedbackRepository: FeedbackRepository
    private lateinit var useCase: CheckFeedbackStatusUseCase

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()
    private val attendanceId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        attendanceRepository = mockk()
        feedbackRepository = mockk()
        useCase = CheckFeedbackStatusUseCase(attendanceRepository, feedbackRepository)
    }

    @Test
    fun `should return true when feedback exists for the attendance`() {
        val attendance = createAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val feedback = createFeedback(attendanceId = attendanceId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns attendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns feedback

        val result = useCase.execute(userId, meetupId)

        assertTrue(result)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { feedbackRepository.findByAttendanceId(attendanceId) }
    }

    @Test
    fun `should return false when attendance exists but no feedback`() {
        val attendance = createAttendance(id = attendanceId, userId = userId, meetupId = meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns attendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns null

        val result = useCase.execute(userId, meetupId)

        assertFalse(result)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { feedbackRepository.findByAttendanceId(attendanceId) }
    }

    @Test
    fun `should return false when attendance does not exist`() {
        every { attendanceRepository.getAttendance(userId, meetupId) } returns null

        val result = useCase.execute(userId, meetupId)

        assertFalse(result)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
    }

    @Test
    fun `should return false when attendance is unconfirmed and no feedback`() {
        val unconfirmedAttendance = createUnconfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns unconfirmedAttendance
        every { feedbackRepository.findByAttendanceId(attendanceId) } returns null

        val result = useCase.execute(userId, meetupId)
        
        assertFalse(result)
    }
}
