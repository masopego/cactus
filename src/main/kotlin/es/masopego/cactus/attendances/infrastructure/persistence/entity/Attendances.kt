package es.masopego.cactus.attendances.infrastructure.persistence.entity

import es.masopego.cactus.auth.infrastructure.persistence.entity.Users
import es.masopego.cactus.meetups.infrastructure.persistence.entity.Meetups
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Attendances : Table("attendance") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }

    val user = reference("user", Users.id)
    val meetup = reference("meetup", Meetups.id)
    val confirmed = datetime("confirmed").nullable()

    override val primaryKey = PrimaryKey(id)
}