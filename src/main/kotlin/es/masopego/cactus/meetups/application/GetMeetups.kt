package es.masopego.cactus.meetups.application

import es.masopego.cactus.meetups.domain.Meetup
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GetMeetups {

    fun getMeetups(): List<Meetup> {
        return listOf(
            Meetup(
                title = "Cómo crear kubernetes",
                description = "Va a se la leche",
                startDate = LocalDate.now()
            )
        )
    }
}