package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.fixtures.AttendanceFixtures.createConfirmedAttendance
import es.masopego.cactus.fixtures.AttendanceFixtures.createUnconfirmedAttendance
import es.masopego.cactus.fixtures.MeetupFixtures.createFutureMeetup
import es.masopego.cactus.fixtures.MeetupFixtures.createPastMeetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class ConfirmAttendanceUseCaseTest {

    private lateinit var meetupRepository: MeetupRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var useCase: ConfirmAttendanceUseCase

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        meetupRepository = mockk()
        attendanceRepository = mockk()
        useCase = ConfirmAttendanceUseCase(meetupRepository, attendanceRepository)
    }

    @Test
    fun `should confirm attendance successfully when meetup exists and user has unconfirmed attendance`() {
        val meetup = createFutureMeetup()
        val unconfirmedAttendance = createUnconfirmedAttendance(userId = userId, meetupId = meetupId)
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns unconfirmedAttendance
        every { attendanceRepository.confirmExistingAttendance(unconfirmedAttendance.id) } returns unconfirmedAttendance.copy(
            confirmed = LocalDateTime.now()
        )

        val result = useCase.execute(request)

        assertTrue(result.isSuccess)
        verify { meetupRepository.findById(meetupId) }
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { attendanceRepository.confirmExistingAttendance(unconfirmedAttendance.id) }
    }

    @Test
    fun `should fail when attendance does not exist for the user`() {
        val meetup = createFutureMeetup()
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns null

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is AttendanceNotFound)
    }

    @Test
    fun `should fail when meetup does not exist`() {
        val request = ConfirmAttendanceRequest(userId, meetupId)
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
        val meetup = createPastMeetup()
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup

        val result = useCase.execute(request)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is MeetupAlreadyStarted)
    }

    @Test
    fun `should fail when attendance is already confirmed`() {
        val meetup = createFutureMeetup()
        val confirmedAttendance = createConfirmedAttendance(userId = userId, meetupId = meetupId)
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns confirmedAttendance

        val result = useCase.execute(request)
        
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is AttendanceAlreadyConfirmed)
    }
}


