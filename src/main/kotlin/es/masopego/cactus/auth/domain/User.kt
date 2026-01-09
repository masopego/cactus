package es.masopego.cactus.auth.domain

import java.time.LocalDateTime
import java.util.*

data class User(
    val id: UUID? = null,
    val nickname: String,
    val email: String,
    val avatar: String = AuthConstants.DEFAULT_AVATAR_URL,
    val createDate: LocalDateTime = LocalDateTime.now()
)

