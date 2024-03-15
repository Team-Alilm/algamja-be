package org.teamalilm.alilmbe.controller.basket

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/basket")
class BasketController {

    @GetMapping
    fun findAll(): String {
        return "Hello Alilm"
    }

}