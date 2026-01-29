package es.masopego.cactus.feedback.infrastructure.http

import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.feedback.application.GetUserFeedbackStatsUseCase
import es.masopego.cactus.feedback.infrastructure.http.dto.UserFeedbackStatsResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feedback")
class GetUserFeedbackStatsController(
    private val getUserFeedbackStatsUseCase: GetUserFeedbackStatsUseCase,
    private val userRepository: UserRepository
) {

    @GetMapping("/stats")
    fun getUserFeedbackStats(
        authentication: Authentication
    ): ResponseEntity<UserFeedbackStatsResponse> {
        val user = userRepository.findByEmail(authentication.name)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val stats = getUserFeedbackStatsUseCase.execute(user.id!!)
        val response = UserFeedbackStatsResponse.from(stats)

        return ResponseEntity.ok(response)
    }
}

