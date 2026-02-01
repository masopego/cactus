package es.masopego.cactus.fixtures

import es.masopego.cactus.attendances.domain.Attendance
import java.time.LocalDateTime
import java.util.*

object AttendanceFixtures {

    fun createAttendance(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        meetupId: UUID = UUID.randomUUID(),
        confirmed: LocalDateTime? = null
    ) = Attendance(
        id = id,
        userId = userId,
        meetupId = meetupId,
        confirmed = confirmed
    )

    fun createConfirmedAttendance(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        meetupId: UUID = UUID.randomUUID()
    ) = createAttendance(
        id = id,
        userId = userId,
        meetupId = meetupId,
        confirmed = LocalDateTime.now()
    )

    fun createUnconfirmedAttendance(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        meetupId: UUID = UUID.randomUUID()
    ) = createAttendance(
        id = id,
        userId = userId,
        meetupId = meetupId,
        confirmed = null
    )
}

