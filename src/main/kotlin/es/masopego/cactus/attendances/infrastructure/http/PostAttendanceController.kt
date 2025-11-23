package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.ConfirmAttendanceRequest
import es.masopego.cactus.attendances.application.ConfirmAttendanceUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
class PostAttendanceController(
    val useCase: ConfirmAttendanceUseCase
) {

    @PostMapping("/meetups/{meetupId}/attendance")
    fun confirmAttendance(
        @PathVariable meetupId: UUID
    ): ResponseEntity<Any> {

        // TODO: Meter autenticación y sacar el userId de ahí
        val userId = UUID.fromString("1b7c54db-8455-4cac-bbb3-86df5cd7e672")

        useCase.execute(
            ConfirmAttendanceRequest(
                userId = userId,
                meetupId = meetupId
            )
        ).onSuccess {
            return ResponseEntity.ok().build()
        }.onFailure {

            when (it) {
                is es.masopego.cactus.attendances.domain.errors.MeetupNotFound -> {
                    return ResponseEntity.notFound().build()
                }

                is es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted -> {
                    return ResponseEntity.status(409).body(
                        AttanceErrorResponse(
                            code = 1001,
                            message = "The meetup has already started"
                        )
                    )
                }

                is es.masopego.cactus.attendances.domain.errors.UserAlreadyRegistered -> {
                    return ResponseEntity.status(409).body(
                        AttanceErrorResponse(
                            code = 1002,
                            message = "The user has already started"
                        )
                    )
                }
            }
        }
        return ResponseEntity.ok().build()
    }
}

data class AttanceErrorResponse(
    val code: Int,
    val message: String
)
