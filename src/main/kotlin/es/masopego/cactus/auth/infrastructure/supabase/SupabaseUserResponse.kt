package es.masopego.cactus.auth.infrastructure.supabase

import com.fasterxml.jackson.annotation.JsonProperty

data class SupabaseUserResponse(
    val id: String,
    val email: String,
    @JsonProperty("user_metadata")
    val userMetadata: UserMetadata?
)

data class UserMetadata(
    val nickname: String?,
    val avatar: String?,
    @JsonProperty("avatar_url")
    val avatarUrl: String?
)

