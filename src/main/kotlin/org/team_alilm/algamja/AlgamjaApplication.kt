package org.team_alilm.algamja

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AlgamjaApplication

fun main(args: Array<String>) {
    runApplication<AlgamjaApplication>(*args)
}