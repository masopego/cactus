package es.masopego.cactus.attendances.infrastructure.http

import es.masopego.cactus.attendances.application.AttendanceService
import es.masopego.cactus.attendances.domain.Attendance
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/attendance")
class GetAttendanceController(val service: AttendanceService) {

    @GetMapping()
    fun getAllUserAttendances(
        @PathVariable userId: UUID
    ): ResponseEntity<List<Attendance>> {
        val attendances = service.getAllAttendancesByUser(userId)
        return ResponseEntity.ok(attendances)
    }

    @GetMapping("/user/{userId}")
    fun getUserAttendanceByMeetup(
        @RequestParam userId: UUID,
        @RequestParam meetupId: UUID
    ): ResponseEntity<Attendance?> {
        val attendance = service.getAttendance(userId, meetupId)
        return ResponseEntity.ok(attendance)
    }


}