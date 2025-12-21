package es.masopego.cactus.auth.domain

import org.springframework.security.core.userdetails.UserDetails

interface TokenGenerator {
    fun generateToken(userDetails: UserDetails): String
    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String
}

