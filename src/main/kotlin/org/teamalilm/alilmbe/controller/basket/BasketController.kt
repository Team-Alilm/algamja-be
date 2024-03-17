package org.teamalilm.alilmbe.controller.basket

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.domain.basket.service.BasketService

@RestController
@RequestMapping("/api/v1/basket")
class BasketController(
    private val basketService: BasketService
) {

    @GetMapping
    fun findAll(): String {
        return "Hello Alilm"
    }

}