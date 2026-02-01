package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.fixtures.AttendanceFixtures.createConfirmedAttendance
import es.masopego.cactus.fixtures.AttendanceFixtures.createUnconfirmedAttendance
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

class GetUserAttendanceStatsUseCaseTest {

    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var meetupRepository: MeetupRepository
    private lateinit var useCase: GetUserAttendanceStatsUseCase

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        attendanceRepository = mockk()
        meetupRepository = mockk()
        useCase = GetUserAttendanceStatsUseCase(attendanceRepository, meetupRepository)
    }

    @Test
    fun `should return correct stats when user has confirmed attendances`() {
        val meetupId1 = UUID.randomUUID()
        val meetupId2 = UUID.randomUUID()
        val meetupId3 = UUID.randomUUID()

        val confirmedAttendance1 = createConfirmedAttendance(userId = userId, meetupId = meetupId1)
        val confirmedAttendance2 = createConfirmedAttendance(userId = userId, meetupId = meetupId2)
        val unconfirmedAttendance = createUnconfirmedAttendance(userId = userId, meetupId = meetupId3)

        val meetup1 = createMeetup(id = meetupId1, title = "Meetup 1", startDate = LocalDateTime.now().minusDays(10))
        val meetup2 = createMeetup(id = meetupId2, title = "Meetup 2", startDate = LocalDateTime.now().minusDays(5))
        val meetup3 = createMeetup(id = meetupId3, title = "Meetup 3", startDate = LocalDateTime.now().plusDays(5))

        val allAttendances = listOf(confirmedAttendance1, confirmedAttendance2, unconfirmedAttendance)
        val allMeetups = listOf(meetup1, meetup2, meetup3)

        every { attendanceRepository.getAttendancesForUser(userId) } returns allAttendances
        every { meetupRepository.getMeetups() } returns allMeetups
        every { meetupRepository.findById(meetupId1) } returns meetup1
        every { meetupRepository.findById(meetupId2) } returns meetup2

        val stats = useCase.execute(userId)

        assertEquals(2, stats.totalConfirmed)
        assertEquals(3, stats.totalMeetups)
        assertEquals(66, stats.attendancePercentage)
        assertEquals(2, stats.attendedEvents.size)
        assertTrue(stats.attendedEvents.any { it.title == "Meetup 1" })
        assertTrue(stats.attendedEvents.any { it.title == "Meetup 2" })
        verify { attendanceRepository.getAttendancesForUser(userId) }
        verify { meetupRepository.getMeetups() }
    }

    @Test
    fun `should return zero percentage when user has no confirmed attendances`() {
        val meetupId = UUID.randomUUID()
        val unconfirmedAttendance = createUnconfirmedAttendance(userId = userId, meetupId = meetupId)
        val meetup = createMeetup(id = meetupId, title = "Meetup 1", startDate = LocalDateTime.now().plusDays(5))
        val allMeetups = listOf(meetup)

        every { attendanceRepository.getAttendancesForUser(userId) } returns listOf(unconfirmedAttendance)
        every { meetupRepository.getMeetups() } returns allMeetups

        val stats = useCase.execute(userId)

        assertEquals(0, stats.totalConfirmed)
        assertEquals(1, stats.totalMeetups)
        assertEquals(0, stats.attendancePercentage)
        assertEquals(0, stats.attendedEvents.size)
    }

    @Test
    fun `should return empty stats when user has no attendances`() {
        val meetup = createMeetup(title = "Meetup 1", startDate = LocalDateTime.now().plusDays(5))

        every { attendanceRepository.getAttendancesForUser(userId) } returns emptyList()
        every { meetupRepository.getMeetups() } returns listOf(meetup)

        val stats = useCase.execute(userId)

        assertEquals(0, stats.totalConfirmed)
        assertEquals(1, stats.totalMeetups)
        assertEquals(0, stats.attendancePercentage)
        assertEquals(0, stats.attendedEvents.size)
    }

    @Test
    fun `should return zero percentage when there are no meetups registered`() {
        every { attendanceRepository.getAttendancesForUser(userId) } returns emptyList()
        every { meetupRepository.getMeetups() } returns emptyList()

        val stats = useCase.execute(userId)

        assertEquals(0, stats.totalConfirmed)
        assertEquals(0, stats.totalMeetups)
        assertEquals(0, stats.attendancePercentage)
        assertEquals(0, stats.attendedEvents.size)
    }


    @Test
    fun `should calculate 100 percent when user attended all meetups`() {
        val meetupId1 = UUID.randomUUID()
        val meetupId2 = UUID.randomUUID()

        val confirmedAttendance1 = createConfirmedAttendance(userId = userId, meetupId = meetupId1)
        val confirmedAttendance2 = createConfirmedAttendance(userId = userId, meetupId = meetupId2)

        val meetup1 = createMeetup(id = meetupId1, title = "Meetup 1", startDate = LocalDateTime.now().minusDays(10))
        val meetup2 = createMeetup(id = meetupId2, title = "Meetup 2", startDate = LocalDateTime.now().minusDays(5))

        every { attendanceRepository.getAttendancesForUser(userId) } returns listOf(
            confirmedAttendance1,
            confirmedAttendance2
        )
        every { meetupRepository.getMeetups() } returns listOf(meetup1, meetup2)
        every { meetupRepository.findById(meetupId1) } returns meetup1
        every { meetupRepository.findById(meetupId2) } returns meetup2

        val stats = useCase.execute(userId)
        
        assertEquals(2, stats.totalConfirmed)
        assertEquals(2, stats.totalMeetups)
        assertEquals(100, stats.attendancePercentage)
        assertEquals(2, stats.attendedEvents.size)
    }
}
