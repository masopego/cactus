package es.masopego.cactus.speakers.persistence.fixture

import es.masopego.cactus.meetups.infrastructure.persistence.fixture.MeetupFixtures
import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import es.masopego.cactus.speakers.persistence.entity.Speakers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(3)
class SpeakerFixtures : Fixture {

    companion object {
        private val logger = LoggerFactory.getLogger(MeetupFixtures::class.java)

        val createdSpeakerIds = mutableMapOf<String, UUID>()
    }

    override fun load() {
        SpeakerFixtures.Companion.logger.info("Check if fixtures should be executed")

        if (Speakers.selectAll().empty()) {
            val id1 = Speakers.insert {
                it[firstName] = "Laura"
                it[lastName] = "Martínez"
                it[biography] = "Experta en IA aplicada al sector salud"
                it[company] = "MedTech"
            } get Speakers.id
            createdSpeakerIds["Laura Martínez"] = id1

            val id2 = Speakers.insert {
                it[firstName] = "Carlos"
                it[lastName] = "Ruíz"
                it[biography] = "Desarrollador Blockchain y formador"
                it[company] = "CryptoWorks"
            } get Speakers.id
            createdSpeakerIds["Carlos Ruíz"] = id2

            val id3 = Speakers.insert {
                it[firstName] = "Ana"
                it[lastName] = "Torres"
                it[biography] = "Consultora en ciberseguridad"
                it[company] = "SecureNow"
            } get Speakers.id
            createdSpeakerIds["Ana Torres"] = id3
        }
    }
}