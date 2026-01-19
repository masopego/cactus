package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.AttendanceAlreadyConfirmed
import es.masopego.cactus.attendances.application.AttendanceNotFound
import es.masopego.cactus.attendances.application.AttendanceNotFoundForDeletion
import es.masopego.cactus.attendances.application.CannotDeleteConfirmedAttendance
import es.masopego.cactus.attendances.domain.errors.MeetupAlreadyStarted
import es.masopego.cactus.attendances.domain.errors.MeetupNotFound
import es.masopego.cactus.attendances.domain.errors.UserAlreadyRegistered
import es.masopego.cactus.auth.domain.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class AttendanceControllerHelper(
    private val userRepository: UserRepository
) {

    fun getUserFromAuthentication(authentication: Authentication): Result<UUID> {
        val user = userRepository.findByEmail(authentication.name)
            ?: return Result.failure(UserNotAuthenticatedException())

        return Result.success(user.id!!)
    }

    fun handleError(error: Throwable): ResponseEntity<ErrorResponse> {
        return when (error) {
            is UserNotAuthenticatedException -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse(
                    code = 1000,
                    message = "User not authenticated"
                )
            )

            is MeetupNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(
                    code = 2001,
                    message = "Meetup not found"
                )
            )

            is MeetupAlreadyStarted -> ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse(
                    code = 2002,
                    message = "The meetup has already started"
                )
            )

            is UserAlreadyRegistered -> ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse(
                    code = 2003,
                    message = "User already registered for this meetup"
                )
            )

            is AttendanceNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(
                    code = 3001,
                    message = "Attendance not found"
                )
            )

            is AttendanceAlreadyConfirmed -> ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse(
                    code = 3003,
                    message = "Attendance is already confirmed"
                )
            )

            is AttendanceNotFoundForDeletion -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(
                    code = 4001,
                    message = "Attendance not found"
                )
            )

            is CannotDeleteConfirmedAttendance -> ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse(
                    code = 4003,
                    message = "Cannot delete confirmed attendance"
                )
            )

            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = 9999,
                    message = "An unexpected error occurred: ${error.message}"
                )
            )
        }
    }
}

data class ErrorResponse(
    val code: Int,
    val message: String
)

class UserNotAuthenticatedException : RuntimeException("User not authenticated")

