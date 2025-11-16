package es.masopego.cactus

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<CactusApplication>().with(TestcontainersConfiguration::class).run(*args)
}
