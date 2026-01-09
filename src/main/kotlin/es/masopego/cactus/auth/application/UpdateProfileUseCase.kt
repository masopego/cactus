package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {

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

class UserNotFoundException(userId: UUID) :
    RuntimeException("User not found: $userId")

