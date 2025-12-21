package es.masopego.cactus.auth.infrastructure.http

import es.masopego.cactus.auth.application.AuthenticateWithSupabaseUseCase
import es.masopego.cactus.auth.infrastructure.http.dto.AuthenticationResponse
import es.masopego.cactus.auth.infrastructure.http.dto.TokenValidationRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val authenticateWithSupabaseUseCase: AuthenticateWithSupabaseUseCase
) {

    @PostMapping("/validate-supabase")
    fun validateSupabaseToken(
        @RequestBody request: TokenValidationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticateWithSupabaseUseCase.authenticateWithSupabase(request.supabaseToken)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

