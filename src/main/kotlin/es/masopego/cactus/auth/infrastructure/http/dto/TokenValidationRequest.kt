package es.masopego.cactus.auth.infrastructure.http.dto

data class TokenValidationRequest(
    val supabaseToken: String
)

