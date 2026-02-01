package es.masopego.cactus.meetups.application

import es.masopego.cactus.fixtures.MeetupFixtures.createMeetup
import es.masopego.cactus.fixtures.SpeakerFixtures.createSpeaker
import es.masopego.cactus.meetups.domain.MeetupRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GetMeetupsTest {

    private lateinit var meetupRepository: MeetupRepository
    private lateinit var useCase: GetMeetups

    @BeforeEach
    fun setUp() {
        meetupRepository = mockk()
        useCase = GetMeetups(meetupRepository)
    }

    @Test
    fun `should return all meetups when meetups exist`() {
        val speaker1 = createSpeaker(firstName = "John", lastName = "Doe")
        val speaker2 = createSpeaker(firstName = "Jane", lastName = "Smith")

        val meetup1 = createMeetup(
            title = "Kotlin Advanced",
            description = "Learn advanced Kotlin features",
            startDate = LocalDateTime.now().plusDays(5),
            speakers = listOf(speaker1)
        )
        val meetup2 = createMeetup(
            title = "Spring Boot Workshop",
            description = "Build REST APIs with Spring Boot",
            startDate = LocalDateTime.now().plusDays(10),
            speakers = listOf(speaker2)
        )
        val meetup3 = createMeetup(
            title = "Clean Architecture",
            description = "Learn clean architecture principles",
            startDate = LocalDateTime.now().plusDays(15),
            speakers = listOf(speaker1, speaker2)
        )

        val expectedMeetups = listOf(meetup1, meetup2, meetup3)

        every { meetupRepository.getMeetups() } returns expectedMeetups

        val result = useCase.getMeetups()

        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(expectedMeetups, result)
        assertTrue(result.any { it.title == "Kotlin Advanced" })
        assertTrue(result.any { it.title == "Spring Boot Workshop" })
        assertTrue(result.any { it.title == "Clean Architecture" })
        verify { meetupRepository.getMeetups() }
    }

    @Test
    fun `should return empty list when no meetups exist`() {
        every { meetupRepository.getMeetups() } returns emptyList()

        val result = useCase.getMeetups()

        assertNotNull(result)
        assertTrue(result.isEmpty())
        verify { meetupRepository.getMeetups() }
    }

    @Test
    fun `should return meetups with no speakers`() {
        val meetup = createMeetup(
            title = "Open Discussion",
            description = "Community open discussion",
            startDate = LocalDateTime.now().plusDays(3),
            speakers = emptyList()
        )

        every { meetupRepository.getMeetups() } returns listOf(meetup)

        val result = useCase.getMeetups()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertTrue(result[0].speakers.isEmpty())
        verify { meetupRepository.getMeetups() }
    }
    
}
