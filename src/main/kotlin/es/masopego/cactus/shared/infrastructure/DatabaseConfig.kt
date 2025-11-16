package es.masopego.cactus.shared.infrastructure

import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import es.masopego.cactus.venues.infrastructure.persistence.entity.Venues
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfig(
    private val dataSource: DataSource,
    private val fixtures: List<Fixture>
) {

    @PostConstruct
    fun init() {
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Venues, Meetups)

            fixtures.forEach {
                it.load()
            }
        }
    }
}