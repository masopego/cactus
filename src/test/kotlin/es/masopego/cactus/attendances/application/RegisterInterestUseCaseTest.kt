package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.attendances.domain.errors.UserAlreadyRegistered
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

class RegisterInterestUseCaseTest {

    private lateinit var meetupRepository: MeetupRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var useCase: RegisterInterestUseCase

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        meetupRepository = mockk()
        attendanceRepository = mockk()
        useCase = RegisterInterestUseCase(meetupRepository, attendanceRepository)
    }

    @Test
    fun `should register interest successfully when meetup exists and user is not registered`() {
        val futureDate = LocalDateTime.now().plusDays(7)
        val meetup = createMeetup(startDate = futureDate)
        val expectedAttendance = Attendance(
            id = UUID.randomUUID(),
            userId = userId,
            meetupId = meetupId,
            confirmed = null
        )
        val request = RegisterInterestRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns null
        every { attendanceRepository.registerInterest(userId, meetupId) } returns expectedAttendance

        val result = useCase.execute(request)

        assertTrue(result.isSuccess)
        val attendance = result.getOrNull()
        assertNotNull(attendance)
        assertEquals(expectedAttendance.id, attendance!!.id)
        assertEquals(userId, attendance.userId)
        assertEquals(meetupId, attendance.meetupId)
        assertNull(attendance.confirmed)
        verify { meetupRepository.findById(meetupId) }
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { attendanceRepository.registerInterest(userId, meetupId) }
    }

    @Test
    fun `should fail when meetup does not exist`() {
        val request = RegisterInterestRequest(userId, meetupId)
        every { meetupRepository.findById(meetupId) } returns null

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is MeetupNotFound)
        assertEquals("Meetup with id $meetupId not found", exception!!.message)
    }

    @Test
    fun `should fail when meetup has already started`() {
        val pastDate = LocalDateTime.now().minusDays(1)
        val meetup = createMeetup(startDate = pastDate)
        val request = RegisterInterestRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is MeetupAlreadyStarted)
    }

    @Test
    fun `should fail when user is already registered for the meetup`() {
        val futureDate = LocalDateTime.now().plusDays(7)
        val meetup = createMeetup(startDate = futureDate)
        val existingAttendance = Attendance(
            id = UUID.randomUUID(),
            userId = userId,
            meetupId = meetupId,
            confirmed = null
        )
        val request = RegisterInterestRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns existingAttendance

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is UserAlreadyRegistered)
    }
}

