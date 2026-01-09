package es.masopego.cactus.auth.infrastructure.http.dto

data class UpdateProfileRequest(
    val nickname: String,
    val avatar: String,
)