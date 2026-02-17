package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Use case for updating a user's profile information.
 *
 * @property userRepository
 */
@Service
class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {

    /**
     * Updates a user's profile with new nickname and avatar.
     *
     * @param userId
     * @param nickname New display name for the user
     * @param avatar New avatar URL for the user's profile picture
     * @return [Result] containing the updated [User] if successful, or [UserNotFoundException] if user doesn't exist
     *
     */
    fun execute(userId: UUID, nickname: String, avatar: String): Result<User> {
        val existingUser = userRepository.findById(userId)
            ?: return Result.failure(UserNotFoundException(userId))

        val updatedUser = existingUser.copy(
            nickname = nickname,
            avatar = avatar
        )

        val savedUser = userRepository.save(updatedUser)

        return Result.success(savedUser)
    }
}

/**
 * Exception thrown when attempting to update a non-existent user.
 * @param userId
 */
class UserNotFoundException(userId: UUID) :
    RuntimeException("User not found: $userId")

