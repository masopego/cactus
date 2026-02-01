package es.masopego.cactus.fixtures

import es.masopego.cactus.auth.infrastructure.supabase.SupabaseUserResponse
import es.masopego.cactus.auth.infrastructure.supabase.UserMetadata

object SupabaseFixtures {

    fun createSupabaseUserResponse(
        id: String = java.util.UUID.randomUUID().toString(),
        email: String = "test@example.com",
        userMetadata: UserMetadata? = null
    ) = SupabaseUserResponse(
        id = id,
        email = email,
        userMetadata = userMetadata
    )

    fun createUserMetadata(
        nickname: String? = "testuser",
        avatar: String? = "https://example.com/avatar.jpg",
        avatarUrl: String? = null
    ) = UserMetadata(
        nickname = nickname,
        avatar = avatar,
        avatarUrl = avatarUrl
    )

    fun createSupabaseUserWithMetadata(
        id: String = java.util.UUID.randomUUID().toString(),
        email: String = "test@example.com",
        nickname: String = "testuser",
        avatar: String = "https://example.com/avatar.jpg"
    ) = createSupabaseUserResponse(
        id = id,
        email = email,
        userMetadata = createUserMetadata(nickname = nickname, avatar = avatar)
    )
}

