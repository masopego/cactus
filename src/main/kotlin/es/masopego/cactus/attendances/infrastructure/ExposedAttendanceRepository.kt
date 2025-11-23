package es.masopego.cactus.attendances.infrastructure

import es.masopego.cactus.attendances.domain.Attendance
import es.masopego.cactus.attendances.domain.AttendanceRepository
import es.masopego.cactus.attendances.infrastructure.persistence.entity.Attendances
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class ExposedAttendanceRepository : AttendanceRepository {
    override fun getAttendance(userId: UUID, meetupId: UUID): Attendance? =
        transaction {
            Attendances.select { (Attendances.user eq userId) and (Attendances.meetup eq meetupId) }
                .map { row ->
                    Attendance(
                        id = row[Attendances.id],
                        userId = row[Attendances.user],
                        meetupId = row[Attendances.meetup],
                        confirmed = row[Attendances.confirmed]
                    )
                }.firstOrNull()
        }

    override fun confirmAttendance(userId: UUID, meetupId: UUID) {
        transaction {
            Attendances.insert {
                it[Attendances.user] = userId
                it[Attendances.meetup] = meetupId
                it[confirmed] = LocalDateTime.now()
            }

        }
    }

    override fun getAttendancesForUser(userId: UUID): List<Attendance> = transaction {
        Attendances.select { Attendances.user eq userId }
            .map { row ->
                Attendance(
                    id = row[Attendances.id],
                    userId = row[Attendances.user],
                    meetupId = row[Attendances.meetup],
                    confirmed = row[Attendances.confirmed]
                )
            }
    }

}