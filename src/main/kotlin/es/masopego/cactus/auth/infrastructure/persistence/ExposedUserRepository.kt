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

/**
 * Implementation of [UserRepository] using Exposed ORM.
 *
 * This repository provides data persistence for users using the Exposed SQL library.
 * All database operations are wrapped in transactions for consistency.
 *
 */
@Repository
class ExposedUserRepository : UserRepository {

    /**
     * Finds a user by their email address.
     *
     * @param email
     * @return Domain User object if found, null otherwise
     */
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

    /**
     * Finds a user by their unique identifier.
     *
     * @param id
     * @return Domain User object if found, null otherwise
     */
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

    /**
     * Saves or updates a user in the database.
     *
     * Implementation logic:
     * - If user.id exists and is found in database: performs UPDATE operation
     * - If user.id is null or not found: performs INSERT operation with new or provided UUID
     * - For new users, createDate is set to current timestamp
     *
     * @param user
     * @return User object with assigned/confirmed ID
     */
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

    /**
     * Checks if a user with the given ID exists in the database.
     *
     * @param id
     * @return true if user exists, false otherwise
     */
    private fun existsById(id: UUID): Boolean = transaction {
        Users.select { Users.id eq id }.count() > 0
    }
}

