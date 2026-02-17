package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import org.springframework.stereotype.Service

/**
 * Use case for retrieving a user's profile information.
 *
 * @property userRepository
 */
@Service
class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {

    /**
     * Retrieves a user's complete profile by their email address.
     *
     * @param email
     * @return [Result] containing the [User] if found, or failure with IllegalArgumentException if not found
     */
    fun execute(email: String): Result<User> {
        val user = userRepository.findByEmail(email)
            ?: return Result.failure(IllegalArgumentException("User not found"))

        return Result.success(user)
    }
}

