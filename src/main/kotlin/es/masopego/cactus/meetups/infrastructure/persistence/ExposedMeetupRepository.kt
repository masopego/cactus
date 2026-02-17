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
import java.util.*

/**
 * Implementation of [MeetupRepository] using Exposed ORM with complex JOIN operations.
 *
 * This repository handles the persistence of meetups, which involve multiple related entities:
 * - **Meetups**: The main event entity
 * - **Venues**: Location information (one-to-one relationship)
 * - **Speakers**: Multiple speakers per meetup (many-to-many relationship via junction table)
 *``
 *
 */
@Repository
class ExposedMeetupRepository : MeetupRepository {

    /**
     * Retrieves all meetups with their complete information (venue and speakers).
     *
     * @return List of all meetups with complete venue and speaker information.
     *         Returns empty list if no meetups exist.
     */
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
                        startDate = row[Meetups.startDate],
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

    /**
     * Finds a specific meetup by its unique identifier with all related data.
     *
     * @param id
     * @return Complete [Meetup] with venue and speakers if found, null if the meetup
     *         doesn't exist or has no associated venue
     */
    override fun findById(id: UUID): Meetup? =
        transaction {
            (Meetups innerJoin Venues)
                .select { Meetups.id eq id }
                .singleOrNull()
                ?.let { row ->
                    val meetupId = row[Meetups.id]

                    val speakerIds = MeetupSpeakers.select { MeetupSpeakers.meetup eq meetupId }
                        .map { it[MeetupSpeakers.speaker] }

                    val speakers = Speakers.select { Speakers.id inList speakerIds }
                        .map { speakerRow ->
                            Speaker(
                                id = speakerRow[Speakers.id],
                                firstName = speakerRow[Speakers.firstName],
                                lastName = speakerRow[Speakers.lastName],
                                biography = speakerRow[Speakers.biography],
                                company = speakerRow[Speakers.company]
                            )
                        }
                    
                    Meetup(
                        id = row[Meetups.id],
                        title = row[Meetups.title],
                        description = row[Meetups.description],
                        startDate = row[Meetups.startDate],
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
