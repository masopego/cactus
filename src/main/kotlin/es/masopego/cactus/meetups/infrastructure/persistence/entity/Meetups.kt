package es.masopego.cactus.meetups.infrastructure.persistence.entity

import es.masopego.cactus.venues.infrastructure.persistence.entity.Venues
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Meetups : Table("meetups") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val title = varchar("title", 255)
    val description = varchar("description", 255)

    val startDate = datetime("start_date")

    // Foreign key → venue.id
    val venue = reference("venue", Venues.id)
    override val primaryKey = PrimaryKey(id)
}