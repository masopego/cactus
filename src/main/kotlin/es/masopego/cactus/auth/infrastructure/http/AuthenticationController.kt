package es.masopego.cactus.auth.infrastructure.http

import es.masopego.cactus.auth.application.AuthenticationService
import es.masopego.cactus.auth.infrastructure.http.dto.AuthenticationResponse
import es.masopego.cactus.auth.infrastructure.http.dto.TokenValidationRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/validate-supabase")
    fun validateSupabaseToken(
        @RequestBody request: TokenValidationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticationService.authenticateWithSupabase(request.supabaseToken)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "Auth user with role ROLE_CUSTOMER"))
    }
}

