package es.masopego.cactus.auth.infrastructure.http

import es.masopego.cactus.auth.application.GetUserProfileUseCase
import es.masopego.cactus.auth.infrastructure.http.dto.UserProfileResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profile")
class ProfileController(
    private val getUserProfileUseCase: GetUserProfileUseCase
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
}

