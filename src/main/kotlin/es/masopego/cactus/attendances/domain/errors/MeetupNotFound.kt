package es.masopego.cactus.attendances.domain.errors

import java.util.*

class MeetupNotFound(meetupId: UUID) : RuntimeException("Meetup with id $meetupId not found")
