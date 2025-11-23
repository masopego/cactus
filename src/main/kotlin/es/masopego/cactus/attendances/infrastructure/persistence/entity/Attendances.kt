package es.masopego.cactus.attendances.infrastructure.persistence.entity

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Attendances : Table("attendance") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val user = uuid("user")
    val meetup = uuid("meetup")
    val confirmed = datetime("confirmed").nullable()

    override val primaryKey = PrimaryKey(id)
}