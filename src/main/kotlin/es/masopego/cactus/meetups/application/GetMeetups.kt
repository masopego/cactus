package es.masopego.cactus.meetups.application

import es.masopego.cactus.meetups.domain.Meetup
import es.masopego.cactus.meetups.domain.MeetupRepository
import org.springframework.stereotype.Service

@Service
class GetMeetups(
    val repository: MeetupRepository
) {
    fun getMeetups(): List<Meetup> = repository.getMeetups()

}