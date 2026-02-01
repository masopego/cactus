package es.masopego.cactus.fixtures

import es.masopego.cactus.auth.domain.AuthConstants
import es.masopego.cactus.auth.domain.User
import java.time.LocalDateTime
import java.util.*

object UserFixtures {

    fun createUser(
        id: UUID = UUID.randomUUID(),
        nickname: String = "testuser",
        email: String = "test@example.com",
        avatar: String = AuthConstants.DEFAULT_AVATAR_URL,
        createDate: LocalDateTime = LocalDateTime.now()
    ) = User(
        id = id,
        nickname = nickname,
        email = email,
        avatar = avatar,
        createDate = createDate
    )
    
}

