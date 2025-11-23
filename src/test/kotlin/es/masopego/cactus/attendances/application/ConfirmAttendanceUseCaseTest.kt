package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.attendances.domain.errors.UserAlreadyRegistered
import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import es.masopego.cactus.venues.domain.Venue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ConfirmAttendanceUseCaseTest {

    private lateinit var meetupRepository: MeetupRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var useCase: ConfirmAttendanceUseCase

    private val userId = UUID.randomUUID()
    private val meetupId = UUID.randomUUID()
    private val venueId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        meetupRepository = mockk()
        attendanceRepository = mockk()
        useCase = ConfirmAttendanceUseCase(meetupRepository, attendanceRepository)
    }

    @Test
    fun `debe confirmar asistencia exitosamente cuando el meetup existe y el usuario no esta registrado`() {
        // Given
        val futureDate = LocalDate.now().plusDays(7)
        val meetup = createMeetup(startDate = futureDate)
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns null
        every { attendanceRepository.confirmAttendance(userId, meetupId) } returns Unit

        // When
        val result = useCase.execute(request)

        // Then
        assertTrue(result.isSuccess)
        verify { meetupRepository.findById(meetupId) }
        verify { attendanceRepository.getAttendance(userId, meetupId) }
        verify { attendanceRepository.confirmAttendance(userId, meetupId) }
    }

    @Test
    fun `debe confirmar asistencia exitosamente cuando el meetup no tiene fecha de inicio`() {
        // Given
        val meetup = createMeetup(startDate = null)
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns null
        every { attendanceRepository.confirmAttendance(userId, meetupId) } returns Unit

        // When
        val result = useCase.execute(request)

        // Then
        assertTrue(result.isSuccess)
        verify { attendanceRepository.confirmAttendance(userId, meetupId) }
    }

    @Test
    fun `debe fallar cuando el meetup no existe`() {
        // Given
        val request = ConfirmAttendanceRequest(userId, meetupId)
        every { meetupRepository.findById(meetupId) } returns null

        // When
        val result = useCase.execute(request)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is MeetupNotFound)
        assertEquals("Meetup with id $meetupId not found", exception!!.message)
    }

    @Test
    fun `debe fallar cuando el meetup ya ha comenzado`() {
        // Given
        val pastDate = LocalDate.now().minusDays(1)
        val meetup = createMeetup(startDate = pastDate)
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup

        // When
        val result = useCase.execute(request)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is MeetupAlreadyStarted)
    }

    @Test
    fun `debe fallar cuando el usuario ya esta registrado en el meetup`() {
        // Given
        val futureDate = LocalDate.now().plusDays(7)
        val meetup = createMeetup(startDate = futureDate)
        val existingAttendance = Attendance(
            id = UUID.randomUUID(),
            userId = userId,
            meetupId = meetupId,
            confirmed = LocalDateTime.now()
        )
        val request = ConfirmAttendanceRequest(userId, meetupId)

        every { meetupRepository.findById(meetupId) } returns meetup
        every { attendanceRepository.getAttendance(userId, meetupId) } returns existingAttendance

        // When
        val result = useCase.execute(request)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is UserAlreadyRegistered)
    }
    

    private fun createMeetup(startDate: LocalDate?): Meetup {
        val venue = Venue(
            id = venueId,
            place = "Test Venue",
            latitude = 36.84,
            longitude = -2.46,
            address = "Test Address",
            seats = 100
        )

        return Meetup(
            id = meetupId,
            title = "Test Meetup",
            description = "Test Description",
            startDate = startDate,
            venue = venue,
            speakers = emptyList()
        )
    }
}


