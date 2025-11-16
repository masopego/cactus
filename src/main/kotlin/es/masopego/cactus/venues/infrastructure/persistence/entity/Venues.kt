package es.masopego.cactus.venues.infrastructure.persistence.entity

import org.jetbrains.exposed.sql.Table

object Venues : Table("venues") {
    val id = uuid("id")
    val place = varchar("place", 255)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val address = varchar("address", 255)
    val seats = integer("seats")
    override val primaryKey = PrimaryKey(id)
}