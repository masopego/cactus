package es.masopego.cactus.meetups.infrastructure.persistence

import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class InMemoryMeetupRepository : MeetupRepository {
    override fun getMeetups(): List<Meetup> {
        return listOf(
            Meetup(
                title = "Cómo crear kubernetes",
                description = "Va a se la leche",
                startDate = LocalDate.now()
            )
        )
    }
}