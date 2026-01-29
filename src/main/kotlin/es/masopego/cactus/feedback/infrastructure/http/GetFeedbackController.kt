package es.masopego.cactus.feedback.infrastructure.http

import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.feedback.application.CheckFeedbackStatusUseCase
import es.masopego.cactus.feedback.application.GetUserFeedbacksWithMeetupUseCase
import es.masopego.cactus.feedback.infrastructure.http.dto.FeedbackErrorResponse
import es.masopego.cactus.feedback.infrastructure.http.dto.FeedbackStatusResponse
import es.masopego.cactus.feedback.infrastructure.http.dto.FeedbackWithMeetupResponse
import es.masopego.cactus.feedback.infrastructure.http.dto.MeetupInfo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/api/feedback")
class GetFeedbackController(
    private val getUserFeedbacksWithMeetupUseCase: GetUserFeedbacksWithMeetupUseCase,
    private val checkFeedbackStatusUseCase: CheckFeedbackStatusUseCase,
    private val userRepository: UserRepository
) {

    @GetMapping
    fun getAllUserFeedbacks(
        authentication: Authentication
    ): ResponseEntity<List<FeedbackWithMeetupResponse>> {
        val user = userRepository.findByEmail(authentication.name)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val feedbacksWithMeetup = getUserFeedbacksWithMeetupUseCase.execute(user.id!!)

        val response = feedbacksWithMeetup.map { feedbackWithMeetup ->
            FeedbackWithMeetupResponse(
                id = feedbackWithMeetup.feedback.id,
                rating = feedbackWithMeetup.feedback.rating,
                comment = feedbackWithMeetup.feedback.comment,
                meetup = MeetupInfo(
                    id = feedbackWithMeetup.meetup.id,
                    title = feedbackWithMeetup.meetup.title,
                    description = feedbackWithMeetup.meetup.description,
                    startDate = feedbackWithMeetup.meetup.startDate
                )
            )
        }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/meetups/{meetupId}")
    fun getFeedbackStatus(
        @PathVariable meetupId: UUID,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val user = userRepository.findByEmail(authentication.name)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                FeedbackErrorResponse(
                    code = 3000,
                    message = "User not authenticated"
                )
            )

        val exists = checkFeedbackStatusUseCase.execute(user.id!!, meetupId)
        return ResponseEntity.ok(FeedbackStatusResponse(exists = exists))
    }
}
