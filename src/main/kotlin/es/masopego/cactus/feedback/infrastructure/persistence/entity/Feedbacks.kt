package es.masopego.cactus.feedback.infrastructure.persistence.entity

import es.masopego.cactus.attendances.infrastructure.persistence.entity.Attendances
import org.jetbrains.exposed.sql.Table
import java.util.*


object Feedbacks : Table("feedback") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val attendanceId = reference("attendance", Attendances.id)
    val rating = short("rating")
    val comment = varchar("comment", 255)

    override val primaryKey = PrimaryKey(id)
}