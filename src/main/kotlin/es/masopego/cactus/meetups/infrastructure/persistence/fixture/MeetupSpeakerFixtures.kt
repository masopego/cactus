package es.masopego.cactus.meetups.infrastructure.persistence.fixture

import es.masopego.cactus.meetups.infrastructure.persistence.entity.MeetupSpeakers
import es.masopego.cactus.shared.infrastructure.persistence.fixture.Fixture
import es.masopego.cactus.speakers.persistence.fixture.SpeakerFixtures
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(4)
class MeetupSpeakerFixtures : Fixture {

    override fun load() {

        if (MeetupSpeakers.selectAll().empty()) {

            MeetupSpeakers.insert {
                it[meetup] = MeetupFixtures.createdMeetupIds["AI & Future"]!!
                it[speaker] = SpeakerFixtures.createdSpeakerIds["Laura Martínez"]!!
            }

            MeetupSpeakers.insert {
                it[meetup] = MeetupFixtures.createdMeetupIds["Web3 Developers"]!!
                it[speaker] = SpeakerFixtures.createdSpeakerIds["Carlos Ruíz"]!!
            }

            MeetupSpeakers.insert {
                it[meetup] = MeetupFixtures.createdMeetupIds["Cybersecurity 2025"]!!
                it[speaker] = SpeakerFixtures.createdSpeakerIds["Ana Torres"]!!
            }
        }
    }
}