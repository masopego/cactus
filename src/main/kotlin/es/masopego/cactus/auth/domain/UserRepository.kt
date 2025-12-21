package es.masopego.cactus.auth.domain

import java.util.*

interface UserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: UUID): User?
    fun save(user: User): User
}

