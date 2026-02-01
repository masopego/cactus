package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.fixtures.AttendanceFixtures.createConfirmedAttendance
import es.masopego.cactus.fixtures.AttendanceFixtures.createUnconfirmedAttendance
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AttendanceServiceTest {

    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var service: AttendanceService

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        attendanceRepository = mockk()
        service = AttendanceService(attendanceRepository)
    }

    @Test
    fun `should return attendance when it exists for user and meetup`() {
        val expectedAttendance = createConfirmedAttendance(userId = userId, meetupId = meetupId)

        every { attendanceRepository.getAttendance(userId, meetupId) } returns expectedAttendance

        val result = service.getAttendance(userId, meetupId)

        assertNotNull(result)
        assertEquals(expectedAttendance.id, result!!.id)
        assertEquals(userId, result.userId)
        assertEquals(meetupId, result.meetupId)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
    }

    @Test
    fun `should return null when attendance does not exist for user and meetup`() {
        every { attendanceRepository.getAttendance(userId, meetupId) } returns null

        val result = service.getAttendance(userId, meetupId)

        assertNull(result)
        verify { attendanceRepository.getAttendance(userId, meetupId) }
    }

    @Test
    fun `should return all attendances for a user`() {
        val meetupId1 = UUID.randomUUID()
        val meetupId2 = UUID.randomUUID()
        val meetupId3 = UUID.randomUUID()

        val attendance1 = createConfirmedAttendance(userId = userId, meetupId = meetupId1)
        val attendance2 = createUnconfirmedAttendance(userId = userId, meetupId = meetupId2)
        val attendance3 = createConfirmedAttendance(userId = userId, meetupId = meetupId3)

        val expectedAttendances = listOf(attendance1, attendance2, attendance3)

        every { attendanceRepository.getAttendancesForUser(userId) } returns expectedAttendances

        val result = service.getAllAttendancesByUser(userId)

        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(expectedAttendances, result)
        verify { attendanceRepository.getAttendancesForUser(userId) }
    }

    @Test
    fun `should return empty list when user has no attendances`() {
        every { attendanceRepository.getAttendancesForUser(userId) } returns emptyList()

        val result = service.getAllAttendancesByUser(userId)
        
        assertNotNull(result)
        assertTrue(result.isEmpty())
        verify { attendanceRepository.getAttendancesForUser(userId) }
    }
}
