package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.GetUserAttendanceStatsUseCase
import es.masopego.cactus.attendances.infrastructure.http.dto.UserAttendanceStatsResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class GetUserAttendanceStatsController(
    private val getUserAttendanceStatsUseCase: GetUserAttendanceStatsUseCase,
    private val helper: AttendanceControllerHelper
) {

    @GetMapping("/attendances/stats")
    fun getUserAttendanceStats(
        authentication: Authentication
    ): ResponseEntity<*> {
        return helper.getUserFromAuthentication(authentication).fold(
            onSuccess = { userId ->
                val stats = getUserAttendanceStatsUseCase.execute(userId)
                val response = UserAttendanceStatsResponse.from(stats)
                ResponseEntity.ok(response)
            },
            onFailure = { error -> helper.handleError(error) }
        )
    }
}

