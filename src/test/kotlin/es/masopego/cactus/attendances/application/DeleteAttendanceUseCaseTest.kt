package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.fixtures.AttendanceFixtures.createUnconfirmedAttendance
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class DeleteAttendanceUseCaseTest {

    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var useCase: DeleteAttendanceUseCase

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()
    private val attendanceId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        attendanceRepository = mockk()
        useCase = DeleteAttendanceUseCase(attendanceRepository)
    }

    @Test
    fun `should delete attendance successfully when it exists and is not confirmed`() {
        val unconfirmedAttendance = Attendance(
            id = attendanceId,
            userId = userId,
            meetupId = meetupId,
            confirmed = null
        )
        val request = DeleteAttendanceRequest(userId, meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns unconfirmedAttendance
        every { attendanceRepository.deleteAttendance(attendanceId) } returns true

        val result = useCase.execute(request)

        assertTrue(result.isSuccess)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { attendanceRepository.deleteAttendance(attendanceId) }
    }

    @Test
    fun `should fail when attendance does not exist`() {
        val request = DeleteAttendanceRequest(userId, meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns null

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is AttendanceNotFoundForDeletion)
        assertEquals("Attendance not found for user $userId and meetup $meetupId", exception!!.message)
    }

    @Test
    fun `should fail when trying to delete a confirmed attendance`() {
        val confirmedAttendance = Attendance(
            id = attendanceId,
            userId = userId,
            meetupId = meetupId,
            confirmed = LocalDateTime.now()
        )
        val request = DeleteAttendanceRequest(userId, meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns confirmedAttendance

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is CannotDeleteConfirmedAttendance)
        assertEquals("Cannot delete confirmed attendance: $attendanceId", exception!!.message)
    }

    @Test
    fun `should fail when deletion operation fails`() {
        val unconfirmedAttendance = createUnconfirmedAttendance(id = attendanceId, userId = userId, meetupId = meetupId)
        val request = DeleteAttendanceRequest(userId, meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns unconfirmedAttendance
        every { attendanceRepository.deleteAttendance(attendanceId) } returns false

        val result = useCase.execute(request)
        
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is AttendanceDeletionFailed)
        assertEquals("Failed to delete attendance: $attendanceId", exception!!.message)
    }
}

