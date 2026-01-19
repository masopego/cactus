package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.ConfirmAttendanceRequest
import es.masopego.cactus.attendances.application.ConfirmAttendanceUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
class ConfirmExistingAttendanceController(
    private val confirmAttendanceUseCase: ConfirmAttendanceUseCase,
    private val helper: AttendanceControllerHelper
) {

    @PutMapping("/meetups/{meetupId}/attendance")
    fun confirmExistingAttendance(
        @PathVariable meetupId: UUID,
        authentication: Authentication
    ): ResponseEntity<*> {

        return helper.getUserFromAuthentication(authentication).fold(
            onSuccess = { userId ->
                confirmAttendanceUseCase.execute(
                    ConfirmAttendanceRequest(userId = userId, meetupId = meetupId)
                ).fold(
                    onSuccess = { ResponseEntity.ok().build() },
                    onFailure = { error -> helper.handleError(error) }
                )
            },
            onFailure = { error -> helper.handleError(error) }
        )
    }
}

