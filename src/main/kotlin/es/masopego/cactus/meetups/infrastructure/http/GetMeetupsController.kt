package es.masopego.cactus.meetups.infrastructure.http

import es.masopego.cactus.meetups.application.GetMeetups
import es.masopego.cactus.meetups.domain.Meetup
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/meetups")
class GetMeetupsController(val service: GetMeetups) {


    @GetMapping()
    fun getMeetups(): ResponseEntity<List<Meetup>> {

        return ResponseEntity.ok(service.getMeetups())
    }
}

