package es.masopego.cactus.attendances.infrastructure.http.dto

import es.masopego.cactus.attendances.domain.Attendance
import java.time.LocalDateTime
import java.util.*

enum class AttendanceStatus {
    NOT_REGISTERED,
    REGISTERED,
    CONFIRMED
}

data class AttendanceStatusResponse(
    val status: AttendanceStatus,
    val attendance: AttendanceDetailResponse?
) {
    companion object {
        fun from(attendance: Attendance): AttendanceDetailResponse {
            val status = when {
                attendance.confirmed == null -> AttendanceStatus.REGISTERED
                else -> AttendanceStatus.CONFIRMED
            }
            return AttendanceDetailResponse(
                id = attendance.id,
                userId = attendance.userId,
                meetupId = attendance.meetupId,
                confirmed = attendance.confirmed,
                status = status
            )
        }
    }
}

data class AttendanceDetailResponse(
    val id: UUID,
    val userId: UUID,
    val meetupId: UUID,
    val confirmed: LocalDateTime?,
    val status: AttendanceStatus
) {
    companion object {
        fun from(attendance: Attendance): AttendanceDetailResponse {
            val status = when {
                attendance.confirmed == null -> AttendanceStatus.REGISTERED
                else -> AttendanceStatus.CONFIRMED
            }
            return AttendanceDetailResponse(
                id = attendance.id,
                userId = attendance.userId,
                meetupId = attendance.meetupId,
                confirmed = attendance.confirmed,
                status = status
            )
        }
    }
}

