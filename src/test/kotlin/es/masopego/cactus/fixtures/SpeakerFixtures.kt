package es.masopego.cactus.fixtures

import es.masopego.cactus.speakers.domain.Speaker
import java.util.*

object SpeakerFixtures {

    fun createSpeaker(
        id: UUID = UUID.randomUUID(),
        firstName: String = "John",
        lastName: String = "Doe",
        biography: String = "Test biography",
        company: String? = "Test Company"
    ) = Speaker(
        id = id,
        firstName = firstName,
        lastName = lastName,
        biography = biography,
        company = company
    )
    
}

