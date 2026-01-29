package es.masopego.cactus.feedback.application

import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.feedback.domain.FeedbackRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CheckFeedbackStatusUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val feedbackRepository: FeedbackRepository
) {

    fun execute(userId: UUID, meetupId: UUID): Boolean {
        val attendance = attendanceRepository.getAttendance(userId, meetupId)
            ?: return false

        val feedback = feedbackRepository.findByAttendanceId(attendance.id)
        return feedback != null
    }
}

