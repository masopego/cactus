package es.masopego.cactus.meetups.infrastructure.persistence.fixture

import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Component

@Component
class MeetupFixtures : Fixture {

    override fun load() {

        if (Meetups.selectAll().empty()) {
            Meetups.insert {
                it[title] = "Spring Boot Meetup"
                it[description] = "Madrid"
            }

            Meetups.insert {
                it[title] = "Kotlin Enthusiasts"
                it[description] = "Barcelona"
            }

            Meetups.insert {
                it[title] = "Exposed Workshop"
                it[description] = "Valencia"
            }
        }
    }
}