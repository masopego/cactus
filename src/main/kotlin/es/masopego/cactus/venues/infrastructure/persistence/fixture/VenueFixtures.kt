package es.masopego.cactus.venues.infrastructure.persistence.fixture

import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import es.masopego.cactus.venues.infrastructure.persistence.entity.Venues
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(1)
class VenueFixtures : Fixture {

    companion object {
        private val logger = LoggerFactory.getLogger(VenueFixtures::class.java)
    }

    val createdVenueIds = mutableMapOf<String, UUID>()


    override fun load() {

        if (Venues.selectAll().empty()) {
            logger.info("Inserting venues")
            val id1 = UUID.randomUUID()

            Venues.insert {
                it[id] = id1
                it[place] = "Clasijazz"
                it[latitude] = 36.8342656
                it[longitude] = -2.4641421
                it[address] = "Calle Maestro Serrano 9, Almería"
                it[seats] = 100
            }
            createdVenueIds["Clasijazz"] = id1

            val id2 = UUID.randomUUID()
            Venues.insert {
                it[id] = id2
                it[place] = "Coworking Workspace"
                it[latitude] = 36.8393027
                it[longitude] = -2.4737653
                it[address] = "Calle Arráez 11, Almería"
                it[seats] = 58
            }

            createdVenueIds["Coworking Workspace"] = id2

            val id3 = UUID.randomUUID()
            Venues.insert {
                it[id] = id3
                it[place] = "Teatro Apolo"
                it[latitude] = 36.8406024
                it[longitude] = -2.4667382
                it[address] = "Calle Rambla Obispo Orberá 25, Almería"
                it[seats] = 250
            }
            createdVenueIds["Teatro Apolo"] = id3

        }
    }
}