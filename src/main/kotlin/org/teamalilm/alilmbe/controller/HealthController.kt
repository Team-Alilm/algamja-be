package org.teamalilm.alilmbe.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/health")
class HealthController {

    @GetMapping
    fun health() : String {
        return "Hello Alilm"
    }
}