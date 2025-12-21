package es.masopego.cactus.auth.infrastructure.http.dto

data class AuthenticationRequest(
    val nickname: String,
    val password: String
)

