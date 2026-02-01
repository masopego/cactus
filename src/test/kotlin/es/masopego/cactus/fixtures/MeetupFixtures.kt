package es.masopego.cactus.fixtures

import es.masopego.cactus.fixtures.VenueFixtures.createVenue
import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.speakers.domain.Speaker
import es.masopego.cactus.venues.domain.Venue
import java.time.LocalDateTime
import java.util.*

object MeetupFixtures {

    fun createMeetup(
        id: UUID = UUID.randomUUID(),
        title: String = "Test Meetup",
        description: String = "Test Description",
        startDate: LocalDateTime? = LocalDateTime.now().plusDays(7),
        venue: Venue = createVenue(),
        speakers: List<Speaker> = emptyList()
    ) = Meetup(
        id = id,
        title = title,
        description = description,
        startDate = startDate,
        venue = venue,
        speakers = speakers
    )

    fun createFutureMeetup() = createMeetup(startDate = LocalDateTime.now().plusDays(7))

    fun createPastMeetup() = createMeetup(startDate = LocalDateTime.now().minusDays(1))
    
}

