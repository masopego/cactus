package es.masopego.cactus.auth.infrastructure.config

import es.masopego.cactus.auth.infrastructure.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security configuration for the application.
 *
 * @property jwtAuthFilter Custom filter that validates JWT tokens on protected endpoints
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter
) {

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * Security configuration:
     * 1. **CSRF Protection**: Disabled since this is a stateless API
     * 2. **Authorization Rules**:
     *    - `/api/auth/validate-supabase` → Public (allows initial authentication)
     *    - All other endpoints → Require authentication
     * 3. **Session Management**: Stateless (no server-side sessions created)
     * 4. **JWT Filter**: Applied before Spring Security's default authentication filter
     *
     * Filter order: JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → Other filters
     *
     * @param http Spring Security's HttpSecurity builder for configuration
     * @return Configured [SecurityFilterChain] that will be applied to all requests
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/validate-supabase").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }


    /**
     * Provides a password encoder bean for the application.
     *
     * @return [BCryptPasswordEncoder] instance for password hashing
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

