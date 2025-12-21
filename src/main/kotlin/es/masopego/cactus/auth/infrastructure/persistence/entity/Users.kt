package es.masopego.cactus.auth.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object Users : UUIDTable("user") {
    val nickname = varchar("nickname", 255)
    val email = varchar("email", 255).uniqueIndex()
    val avatar = varchar("avatar", 255)
    val createDate = datetime("create_date")

}