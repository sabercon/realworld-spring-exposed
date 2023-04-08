package cn.sabercon.realworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RealworldApplication

fun main(vararg args: String) {
    runApplication<RealworldApplication>(*args)
}
