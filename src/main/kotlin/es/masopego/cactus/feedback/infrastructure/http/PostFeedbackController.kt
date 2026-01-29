package es.masopego.cactus.feedback.infrastructure.http

import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.feedback.application.*
import es.masopego.cactus.feedback.infrastructure.http.dto.CreateFeedbackRequest
import es.masopego.cactus.feedback.infrastructure.http.dto.FeedbackErrorResponse
import es.masopego.cactus.feedback.infrastructure.http.dto.FeedbackResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
class PostFeedbackController(
    private val createFeedbackUseCase: CreateFeedbackUseCase,
    private val userRepository: UserRepository
) {

    @PostMapping("/meetups/{meetupId}/feedback")
    fun postFeedback(
        @PathVariable meetupId: UUID,
        @RequestBody request: CreateFeedbackRequest,
        authentication: Authentication
    ): ResponseEntity<Any> {

        val user = userRepository.findByEmail(authentication.name)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                FeedbackErrorResponse(
                    code = 3000,
                    message = "User not authenticated"
                )
            )

        val useCaseRequest = CreateFeedbackRequest(
            userId = user.id!!,
            meetupId = meetupId,
            rating = request.rating,
            comment = request.comment
        )

        val result = createFeedbackUseCase.execute(useCaseRequest)

        return result.fold(
            onSuccess = { feedback ->
                ResponseEntity.status(HttpStatus.CREATED).body(
                    FeedbackResponse(
                        id = feedback.id,
                        attendanceId = feedback.attendanceId,
                        rating = feedback.rating,
                        comment = feedback.comment
                    )
                )
            },
            onFailure = { error ->
                when (error) {
                    is AttendanceNotFoundException -> {
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            FeedbackErrorResponse(
                                code = 3001,
                                message = "Attendance not found for this user and meetup"
                            )
                        )
                    }

                    is AttendanceNotConfirmedException -> {
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            FeedbackErrorResponse(
                                code = 3002,
                                message = "Attendance must be confirmed before leaving feedback"
                            )
                        )
                    }

                    is FeedbackAlreadyExistsException -> {
                        ResponseEntity.status(HttpStatus.CONFLICT).body(
                            FeedbackErrorResponse(
                                code = 3003,
                                message = "Feedback already exists for this attendance"
                            )
                        )
                    }

                    is InvalidRatingException -> {
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            FeedbackErrorResponse(
                                code = 3004,
                                message = "Rating must be between 1 and 5"
                            )
                        )
                    }

                    else -> {
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            FeedbackErrorResponse(
                                code = 3999,
                                message = "An error occurred while processing the feedback"
                            )
                        )
                    }
                }
            }
        )
    }
}

