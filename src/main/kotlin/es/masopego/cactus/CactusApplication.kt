package es.masopego.cactus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CactusApplication

fun main(args: Array<String>) {
    runApplication<CactusApplication>(*args)
}
