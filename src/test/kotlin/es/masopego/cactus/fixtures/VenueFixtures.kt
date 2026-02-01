package es.masopego.cactus.fixtures

import es.masopego.cactus.venues.domain.Venue
import java.util.*

object VenueFixtures {

    fun createVenue(
        id: UUID = UUID.randomUUID(),
        place: String = "Test Venue",
        latitude: Double = 36.84,
        longitude: Double = -2.46,
        address: String = "Test Address",
        seats: Int = 100
    ) = Venue(
        id = id,
        place = place,
        latitude = latitude,
        longitude = longitude,
        address = address,
        seats = seats
    )
}

