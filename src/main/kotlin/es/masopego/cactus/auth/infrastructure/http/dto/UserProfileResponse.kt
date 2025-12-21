package es.masopego.cactus.auth.infrastructure.http.dto

import java.time.LocalDateTime
import java.util.*

data class UserProfileResponse(
    val id: UUID,
    val nickname: String,
    val email: String,
    val avatar: String,
    val createDate: LocalDateTime
)
