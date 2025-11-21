package es.masopego.cactus.meetups.infrastructure.persistence.entity

import org.jetbrains.exposed.sql.Table

object MeetupSpeakers : Table("meetup_speaker") {
    val meetup = uuid("meetup")
    val speaker = uuid("speaker")

    override val primaryKey = PrimaryKey(meetup, speaker)
}