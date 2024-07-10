package org.teamalilm.alilmbe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
* https://tjdtls690.github.io/studycontents/java/2023-05-22-hexagonal_architecture/
* 위 블로그의 내용을 기반으로 아키텍처를 설계했어요.
* */
@EnableJpaAuditing
@SpringBootApplication
class AlilmBeApplication

fun main(args: Array<String>) {
    runApplication<AlilmBeApplication>(*args)
}
