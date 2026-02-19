package es.masopego.cactus.meetups.infrastructure.persistence.fixture

import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import es.masopego.cactus.venues.infrastructure.persistence.fixture.VenueFixtures
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
@Order(2)
class MeetupFixtures(
    private val venueFixture: VenueFixtures
) : Fixture {

    companion object {
        private val logger = LoggerFactory.getLogger(MeetupFixtures::class.java)

        val createdMeetupIds = mutableMapOf<String, UUID>()
    }

    override fun load() {
        logger.info("Check if fixtures should be executed")

        if (Meetups.selectAll().empty()) {
            logger.info("Inserting meetups")

            val id1: UUID = Meetups.insert {
                it[title] = "3D Printing Day: From bits to Atoms"
                it[description] = "Charla sobre inteligencia artificial aplicada"
                it[startDate] = LocalDateTime.parse("2026-11-10T18:00:00")
                it[venue] = venueFixture.createdVenueIds["Clasijazz"]!!
            } get Meetups.id
            createdMeetupIds["3D Printing Day: From bits to Atoms"] = id1

            val id2: UUID = Meetups.insert {
                it[title] = "Web3 Developers"
                it[description] = "Evento sobre desarrollo descentralizado"
                it[startDate] = LocalDateTime.parse("2025-12-02T17:00:00")
                it[venue] = venueFixture.createdVenueIds["Coworking Workspace"]!!
            } get Meetups.id
            createdMeetupIds["Web3 Developers"] = id2

            val id3: UUID = Meetups.insert {
                it[title] = "Cybersecurity 2025"
                it[description] = "Jornada sobre ciberseguridad y privacidad"
                it[startDate] = LocalDateTime.parse("2025-12-15T09:30:00")
                it[venue] = venueFixture.createdVenueIds["Teatro Apolo"]!!
            } get Meetups.id
            createdMeetupIds["Cybersecurity 2025"] = id3
        }
    }
}