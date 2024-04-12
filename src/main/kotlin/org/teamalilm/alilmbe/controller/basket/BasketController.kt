package org.teamalilm.alilmbe.controller.basket

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.domain.basket.repository.data.CountBasketsGroupByProductIdWithProduct
import org.teamalilm.alilmbe.domain.basket.service.BasketService

@Tag(name = "장바구니 API")
@RestController
@RequestMapping("/api/v1/basketS")
class BasketController(
    private val basketService: BasketService
) {

    @GetMapping
    fun findAll(
        @PageableDefault(
            size = 20,
            sort = ["count"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Slice<CountBasketsGroupByProductIdWithProduct>> {
        return ResponseEntity.ok(basketService.findAll(pageable));
    }

}