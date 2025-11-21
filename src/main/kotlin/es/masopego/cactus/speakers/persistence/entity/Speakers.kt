package es.masopego.cactus.speakers.persistence.entity


import org.jetbrains.exposed.sql.Table
import java.util.*

object Speakers : Table("speakers") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val biography = varchar("biography", 255)
    val company = varchar("company", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}