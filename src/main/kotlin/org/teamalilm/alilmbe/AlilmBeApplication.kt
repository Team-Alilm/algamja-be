package org.teamalilm.alilmbe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class AlilmApplication

fun main(args: Array<String>) {
    runApplication<AlilmApplication>(*args)
}
