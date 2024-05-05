package org.teamalilm.alilmbe.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "health", description = "Health Check API")
class HealthController {

    @GetMapping("/health-check")
    @Operation(summary = "Health Check API", description = "Health Check API")
    fun health(): String {
        return "Hello Alilm"
    }

}
