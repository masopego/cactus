package es.masopego.cactus.meetups.infrastructure.persistence

import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import es.masopego.cactus.venues.domain.Venue
import es.masopego.cactus.venues.infrastructure.persistence.entity.Venues
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ExposedMeetupRepository : MeetupRepository {
    override fun getMeetups(): List<Meetup> =
        transaction {
            (Meetups innerJoin Venues)
                .selectAll()
                .map { row ->
                    Meetup(
                        id = row[Meetups.id],
                        title = row[Meetups.title],
                        description = row[Meetups.description],
                        startDate = LocalDate.now(),
                        venue = Venue(
                            id = row[Venues.id],
                            place = row[Venues.place],
                            latitude = row[Venues.latitude],
                            longitude = row[Venues.longitude],
                            address = row[Venues.address],
                            seats = row[Venues.seats]
                        )
                    )
                }
        }
}
