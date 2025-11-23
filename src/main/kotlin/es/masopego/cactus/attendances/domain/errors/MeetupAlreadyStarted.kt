package es.masopego.cactus.attendances.domain.errors

import java.util.*

class MeetupAlreadyStarted(meetupId: UUID) :
    RuntimeException("Cannot register for meetup $meetupId because it has already started")

