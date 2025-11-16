package es.masopego.cactus.meetups.infrastructure.persistence.entity

import org.jetbrains.exposed.sql.Table

object Meetups : Table("meetups") {
    val id = long("id").autoIncrement()
    val title = varchar("title", 255)
    val description = varchar("description", 255)

    //  val startDate = varchar("start_date", 50)
    override val primaryKey = PrimaryKey(id)
}