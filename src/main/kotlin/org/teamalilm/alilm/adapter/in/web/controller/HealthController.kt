package org.teamalilm.alilm.adapter.`in`.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health-check")
    fun health(): String {
        return "Hello Alilm"
    }

}
