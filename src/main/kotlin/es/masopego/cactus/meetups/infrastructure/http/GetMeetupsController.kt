package es.masopego.cactus.meetups.infrastructure.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/meetups")
class GetMeetupsController {


    @GetMapping()
    fun getMeetups(): ResponseEntity<HelloResponse> {

        return ResponseEntity.ok(HelloResponse("Marisol"))
    }
}

data class HelloResponse(val name: String)
