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

/**
 * Use case for authenticating users via Supabase and issuing JWT tokens.
 *
 * Validates the Supabase token, creates or retrieves the local user account
 *
 * @property userRepository
 * @property tokenGenerator
 * @property supabaseValidationService
 */
@Service
class AuthenticateWithSupabaseUseCase(
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator,
    private val supabaseValidationService: SupabaseValidationService
) {

    /**
     * Authenticates a user with their Supabase token and returns a JWT for API access.
     *
     * @param supabaseToken
     * @return [AuthenticationResponse] containing JWT token and user information
     * @throws IllegalArgumentException if the Supabase token is invalid or expired
     *
     */
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

    /**
     * Creates a new local user account from Supabase user data.
     *
     * @param supabaseUser
     * @return Newly created and persisted [User] entity
     */
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

