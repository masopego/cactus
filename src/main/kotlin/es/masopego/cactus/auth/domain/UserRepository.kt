package es.masopego.cactus.auth.domain

import java.util.*

/**
 * This interface defines data access operations for the [User] entity.
 */
interface UserRepository {
    /**
     * Finds a user by their email address.
     *
     * @param email
     * @return The found user or null if it doesn't exist
     */
    fun findByEmail(email: String): User?

    /**
     * Finds a user by their unique identifier.
     *
     * @param id
     * @return The found user or null if it doesn't exist
     */
    fun findById(id: UUID): User?

    /**
     * Saves or updates a user in the database.
     *
     * If the user already exists (has an existing ID), it will be updated.
     * If it's a new user, a new entry will be created.
     *
     * @param user
     * @return The saved user with updated data
     */
    fun save(user: User): User
}

