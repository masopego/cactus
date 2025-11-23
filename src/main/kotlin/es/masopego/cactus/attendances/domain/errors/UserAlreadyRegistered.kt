package es.masopego.cactus.attendances.domain.errors

import java.util.*

class UserAlreadyRegistered(userId: UUID, meetupId: UUID) :
    RuntimeException("User $userId is already registered for meetup $meetupId")

