package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.DeleteAttendanceRequest
import es.masopego.cactus.attendances.application.DeleteAttendanceUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
class DeleteAttendanceController(
    private val deleteAttendanceUseCase: DeleteAttendanceUseCase,
    private val helper: AttendanceControllerHelper
) {

    @DeleteMapping("/meetups/{meetupId}/attendance")
    fun deleteAttendance(
        @PathVariable meetupId: UUID,
        authentication: Authentication
    ): ResponseEntity<*> {

        return helper.getUserFromAuthentication(authentication).fold(
            onSuccess = { userId ->
                deleteAttendanceUseCase.execute(
                    DeleteAttendanceRequest(userId = userId, meetupId = meetupId)
                ).fold(
                    onSuccess = { ResponseEntity.noContent().build() },
                    onFailure = { error -> helper.handleError(error) }
                )
            },
            onFailure = { error -> helper.handleError(error) }
        )
    }
}

