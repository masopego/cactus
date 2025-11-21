package es.masopego.cactus.meetups.infrastructure.persistence

import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import es.masopego.cactus.meetups.infrastructure.persistence.entity.MeetupSpeakers
import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import es.masopego.cactus.speakers.domain.Speaker
import es.masopego.cactus.speakers.persistence.entity.Speakers
import es.masopego.cactus.venues.domain.Venue
import es.masopego.cactus.venues.infrastructure.persistence.entity.Venues
import org.jetbrains.exposed.sql.select
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

                    val meetupId = row[Meetups.id]

                    val speakerIds = MeetupSpeakers.select { MeetupSpeakers.meetup eq meetupId }
                        .map { it[MeetupSpeakers.speaker] }

                    val speakers = Speakers.select { Speakers.id inList speakerIds }
                        .map { row ->
                            Speaker(
                                id = row[Speakers.id],
                                firstName = row[Speakers.firstName],
                                lastName = row[Speakers.lastName],
                                biography = row[Speakers.biography],
                                company = row[Speakers.company]
                            )
                        }

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
                        ),
                        speakers = speakers
                    )
                }
        }
}
