package es.masopego.cactus.auth.infrastructure.persistence

import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.auth.infrastructure.persistence.entity.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class ExposedUserRepository : UserRepository {

    override fun findByEmail(email: String): User? = transaction {
        Users.select { Users.email eq email }
            .singleOrNull()
            ?.let { row ->
                User(
                    id = row[Users.id].value,
                    nickname = row[Users.nickname],
                    email = row[Users.email],
                    avatar = row[Users.avatar],
                    createDate = row[Users.createDate]
                )
            }
    }

    override fun findById(id: UUID): User? = transaction {
        Users.select { Users.id eq id }
            .singleOrNull()
            ?.let { row ->
                User(
                    id = row[Users.id].value,
                    nickname = row[Users.nickname],
                    email = row[Users.email],
                    avatar = row[Users.avatar],
                    createDate = row[Users.createDate]
                )
            }
    }

    override fun save(user: User): User = transaction {
        val userId = if (user.id != null && existsById(user.id)) {
            Users.update({ Users.id eq user.id }) {
                it[nickname] = user.nickname
                it[email] = user.email
                it[avatar] = user.avatar
                it[createDate] = user.createDate
            }
            user.id
        } else {
            val newId = user.id ?: UUID.randomUUID()
            Users.insert {
                it[id] = newId
                it[nickname] = user.nickname
                it[email] = user.email
                it[avatar] = user.avatar
                it[createDate] = LocalDateTime.now()
            }[Users.id].value
        }

        user.copy(id = userId)
    }

    private fun existsById(id: UUID): Boolean = transaction {
        Users.select { Users.id eq id }.count() > 0
    }
}

