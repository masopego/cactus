package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {

    fun execute(email: String): Result<User> {
        val user = userRepository.findByEmail(email)
            ?: return Result.failure(IllegalArgumentException("User not found"))

        return Result.success(user)
    }
}

