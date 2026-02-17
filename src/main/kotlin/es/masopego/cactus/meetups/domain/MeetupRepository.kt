package es.masopego.cactus.meetups.domain

import java.util.*

/**
 * Repository for managing meetups.
 *
 * This interface defines query operations for the [Meetup] entity,
 * allowing to retrieve information about available events.
 */
interface MeetupRepository {

    /**
     * Gets the complete list of available meetups.
     *
     * @return List of all meetups registered in the system
     */
    fun getMeetups(): List<Meetup>

    /**
     * Finds a meetup by its unique identifier.
     *
     * @param id UUID identifier of the meetup
     * @return The found meetup or null if it doesn't exist
     */
    fun findById(id: UUID): Meetup?
}