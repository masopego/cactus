package es.masopego.cactus.meetups.domain

interface MeetupRepository {

    fun getMeetups(): List<Meetup>
}