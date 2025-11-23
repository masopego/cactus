package es.masopego.cactus.meetups.domain

import java.util.*

interface MeetupRepository {

    fun getMeetups(): List<Meetup>
    fun findById(id: UUID): Meetup?
}