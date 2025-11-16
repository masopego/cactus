package es.masopego.cactus.meetups.infrastructure.persistence.fixture

import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import es.masopego.cactus.venues.infrastructure.persistence.fixture.VenueFixtures
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class MeetupFixtures(
    private val venueFixture: VenueFixtures
) : Fixture {

    companion object {
        private val logger = LoggerFactory.getLogger(MeetupFixtures::class.java)
    }

    override fun load() {
        logger.info("Check if fixtures should be executed")

        if (Meetups.selectAll().empty()) {
            logger.info("Inserting meetups")

            Meetups.insert {
                it[title] = "AI & Future"
                it[description] = "Charla sobre inteligencia artificial aplicada"
                it[venue] = venueFixture.createdVenueIds["Clasijazz"]!!
            }

            Meetups.insert {
                it[title] = "Web3 Developers"
                it[description] = "Evento sobre desarrollo descentralizado"
                it[venue] = venueFixture.createdVenueIds["Coworking Workspace"]!!
            }

            Meetups.insert {
                it[title] = "Cybersecurity 2025"
                it[description] = "Jornada sobre ciberseguridad y privacidad"
                it[venue] = venueFixture.createdVenueIds["Teatro Apolo"]!!
            }
        }
    }
}