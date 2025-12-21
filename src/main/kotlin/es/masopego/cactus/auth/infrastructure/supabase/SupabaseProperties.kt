package es.masopego.cactus.auth.infrastructure.supabase

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "supabase")
data class SupabaseProperties(
    var url: String = "",
    var anonKey: String = ""
)