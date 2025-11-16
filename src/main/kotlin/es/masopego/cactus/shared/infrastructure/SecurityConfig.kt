package es.masopego.cactus.shared.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {

        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/**").permitAll()
                    .anyRequest().permitAll()
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}