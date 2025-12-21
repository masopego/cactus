package es.masopego.cactus.auth.infrastructure.http.dto

data class AuthenticationResponse(
    val token: String,
    val nickname: String,
    val email: String
)

