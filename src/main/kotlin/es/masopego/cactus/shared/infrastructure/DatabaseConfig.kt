package es.masopego.cactus.shared.infrastructure

import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * Spring configuration class for database initialization using Exposed ORM.
 *
 * This configuration class is responsible for:
 * 1. Establishing database connection with the configured DataSource
 *
 * @property dataSource Spring-managed DataSource configured in application properties
 */
@Configuration
class DatabaseConfig(
    private val dataSource: DataSource,
) {

    /**
     * Initializes the database connection
     *
     * @throws Exception if database connection fails
     */
    @PostConstruct
    fun init() {
        Database.connect(dataSource)
    }
}