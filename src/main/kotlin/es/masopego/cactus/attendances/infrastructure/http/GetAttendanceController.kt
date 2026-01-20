package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.AttendanceService
import es.masopego.cactus.attendances.infrastructure.http.dto.AttendanceDetailResponse
import es.masopego.cactus.attendances.infrastructure.http.dto.AttendanceStatus
import es.masopego.cactus.attendances.infrastructure.http.dto.AttendanceStatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
class GetAttendanceController(
    private val service: AttendanceService,
    private val helper: AttendanceControllerHelper
) {

    @GetMapping("/meetups/attendances")
    fun getAllUserAttendances(
        authentication: Authentication
    ): ResponseEntity<*> {
        return helper.getUserFromAuthentication(authentication).fold(
            onSuccess = { userId ->
                val attendances = service.getAllAttendancesByUser(userId)
                val response = attendances.map { AttendanceStatusResponse.from(it) }
                ResponseEntity.ok(response)
            },
            onFailure = { error -> helper.handleError(error) }
        )
    }

    @GetMapping("/meetups/{meetupId}/attendance/status")
    fun getAttendanceStatus(
        @PathVariable meetupId: UUID,
        authentication: Authentication
    ): ResponseEntity<*> {
        return helper.getUserFromAuthentication(authentication).fold(
            onSuccess = { userId ->
                val attendance = service.getAttendance(userId, meetupId)
                val status = when {
                    attendance == null -> AttendanceStatusResponse(
                        status = AttendanceStatus.NOT_REGISTERED,
                        attendance = null
                    )

                    attendance.confirmed == null -> AttendanceStatusResponse(
                        status = AttendanceStatus.REGISTERED,
                        attendance = AttendanceDetailResponse.from(attendance)
                    )

                    else -> AttendanceStatusResponse(
                        status = AttendanceStatus.CONFIRMED,
                        attendance = AttendanceDetailResponse.from(attendance)
                    )
                }
                ResponseEntity.ok(status)
            },
            onFailure = { error -> helper.handleError(error) }
        )
    }
}