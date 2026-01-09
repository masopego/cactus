package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.AuthConstants
import es.masopego.cactus.auth.domain.TokenGenerator
import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.auth.infrastructure.http.dto.AuthenticationResponse
import es.masopego.cactus.auth.infrastructure.supabase.SupabaseUserResponse
import es.masopego.cactus.auth.infrastructure.supabase.SupabaseValidationService
import org.springframework.security.core.userdetails.User.builder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AuthenticateWithSupabaseUseCase(
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator,
    private val supabaseValidationService: SupabaseValidationService
) {

    fun authenticateWithSupabase(supabaseToken: String): AuthenticationResponse {
        val supabaseUser = supabaseValidationService.validateTokenAndGetUser(supabaseToken)
            ?: throw IllegalArgumentException("Invalid Supabase token")

        var user = userRepository.findByEmail(supabaseUser.email)

        if (user == null) {
            user = createUser(supabaseUser)
        }

        val userDetails = builder()
            .username(user.email)
            .password("")
            .authorities("ROLE_CUSTOMER")
            .build()

        val jwtToken = tokenGenerator.generateToken(userDetails)

        return AuthenticationResponse(
            token = jwtToken,
            nickname = user.nickname,
            email = user.email
        )
    }

    private fun createUser(
        supabaseUser: SupabaseUserResponse,
    ): User {
        val nickname = supabaseUser.userMetadata?.nickname
            ?: supabaseUser.email.substringBefore("@")
        val avatar = supabaseUser.userMetadata?.avatar
            ?: supabaseUser.userMetadata?.avatarUrl
            ?: AuthConstants.DEFAULT_AVATAR_URL

        val user = User(
            id = UUID.fromString(supabaseUser.id),
            nickname = nickname,
            email = supabaseUser.email,
            avatar = avatar,
            createDate = LocalDateTime.now()
        )

        userRepository.save(user)

        return user
    }
}

