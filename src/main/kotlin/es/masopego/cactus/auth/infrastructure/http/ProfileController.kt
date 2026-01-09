package es.masopego.cactus.auth.infrastructure.http

import es.masopego.cactus.auth.application.GetUserProfileUseCase
import es.masopego.cactus.auth.application.UpdateProfileUseCase
import es.masopego.cactus.auth.application.UserNotFoundException
import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.auth.infrastructure.http.dto.UpdateProfileRequest
import es.masopego.cactus.auth.infrastructure.http.dto.UserProfileResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profile")
class ProfileController(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val userRepository: UserRepository
) {

    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<UserProfileResponse> {
        return try {
            val result = getUserProfileUseCase.execute(authentication.name)

            result.fold(
                onSuccess = { user ->
                    val response = UserProfileResponse(
                        id = user.id!!,
                        nickname = user.nickname,
                        email = user.email,
                        avatar = user.avatar,
                        createDate = user.createDate
                    )
                    ResponseEntity.ok(response)
                },
                onFailure = {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                }
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/me")
    fun updateCurrentUser(
        @RequestBody request: UpdateProfileRequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val user = userRepository.findByEmail(authentication.name)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ProfileErrorResponse(
                    code = 4000,
                    message = "User not authenticated"
                )
            )
        
        val result = updateProfileUseCase.execute(
            userId = user.id!!,
            nickname = request.nickname,
            avatar = request.avatar
        )

        return result.fold(
            onSuccess = {
                ResponseEntity.ok().build()
            },
            onFailure = { error ->
                when (error) {
                    is UserNotFoundException -> {
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            ProfileErrorResponse(
                                code = 4001,
                                message = "User not found"
                            )
                        )
                    }

                    else -> {
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            ProfileErrorResponse(
                                code = 4999,
                                message = "An error occurred while updating the profile"
                            )
                        )
                    }
                }
            }
        )
    }
}

data class ProfileErrorResponse(
    val code: Int,
    val message: String
)

