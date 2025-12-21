package es.masopego.cactus.auth.infrastructure.supabase

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class SupabaseValidationService(
    private val supabaseProperties: SupabaseProperties,
    private val restTemplate: RestTemplate
) {

    fun validateTokenAndGetUser(token: String): SupabaseUserResponse? {
        return try {
            val headers = HttpHeaders().apply {
                set("Authorization", "Bearer $token")
                set("apikey", supabaseProperties.anonKey)
                set("Content-Type", "application/json")
            }

            val entity = HttpEntity<String>(headers)
            val response = restTemplate.exchange(
                "${supabaseProperties.url}/auth/v1/user",
                HttpMethod.GET,
                entity,
                SupabaseUserResponse::class.java
            )

            response.body
        } catch (e: HttpClientErrorException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}