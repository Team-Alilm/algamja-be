package org.team_alilm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["org.team_alilm.*"])
class SecurityApplication

fun main(args: Array<String>) {
    runApplication<SecurityApplication>(*args)
}