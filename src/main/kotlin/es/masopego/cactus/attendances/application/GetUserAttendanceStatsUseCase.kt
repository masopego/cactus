package es.masopego.cactus.attendances.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class GetUserAttendanceStatsUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val meetupRepository: MeetupRepository
) {

    fun execute(userId: UUID): UserAttendanceStats {
        val attendances = attendanceRepository.getAttendancesForUser(userId)

        val confirmedAttendances = attendances.filter { it.confirmed != null }

        val attendedEvents = confirmedAttendances.mapNotNull { attendance ->
            val meetup = meetupRepository.findById(attendance.meetupId)
            meetup?.let {
                AttendedEventInfo(
                    title = it.title,
                    date = it.startDate
                )
            }
        }
        
        val totalMeetups = meetupRepository.getMeetups().size
        val totalConfirmed = confirmedAttendances.size

        val attendancePercentage = if (totalMeetups > 0) {
            (totalConfirmed.toDouble() / totalMeetups.toDouble() * 100).toInt()
        } else {
            0
        }

        return UserAttendanceStats(
            attendedEvents = attendedEvents,
            attendancePercentage = attendancePercentage,
            totalMeetups = totalMeetups,
            totalConfirmed = totalConfirmed
        )
    }
}

data class UserAttendanceStats(
    val attendedEvents: List<AttendedEventInfo>,
    val attendancePercentage: Int,
    val totalMeetups: Int,
    val totalConfirmed: Int
)

data class AttendedEventInfo(
    val title: String,
    val date: LocalDateTime?
)

