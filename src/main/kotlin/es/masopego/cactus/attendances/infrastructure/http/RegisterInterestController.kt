package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.RegisterInterestRequest
import es.masopego.cactus.attendances.application.RegisterInterestUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api")
class RegisterInterestController(
    private val registerInterestUseCase: RegisterInterestUseCase,
    private val helper: AttendanceControllerHelper
) {

    @PostMapping("/meetups/{meetupId}/attendance")
    fun registerInterest(
        @PathVariable meetupId: UUID,
        authentication: Authentication
    ): ResponseEntity<*> {

        return helper.getUserFromAuthentication(authentication).fold(
            onSuccess = { userId ->
                registerInterestUseCase.execute(
                    RegisterInterestRequest(userId = userId, meetupId = meetupId)
                ).fold(
                    onSuccess = { attendance ->
                        ResponseEntity.status(HttpStatus.CREATED).body(
                            AttendanceResponse(
                                id = attendance.id,
                                userId = attendance.userId,
                                meetupId = attendance.meetupId,
                                confirmed = attendance.confirmed
                            )
                        )
                    },
                    onFailure = { error -> helper.handleError(error) }
                )
            },
            onFailure = { error -> helper.handleError(error) }
        )
    }
}

data class AttendanceResponse(
    val id: UUID,
    val userId: UUID,
    val meetupId: UUID,
    val confirmed: LocalDateTime?
)



