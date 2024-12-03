package org.team_alilm.adapter.`in`.web.controller.product

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductCrawlingUseCase

@RestController
@Tag(name = "상품 크롤링 조회 API", description = "상품 크롤링 조회 API를 제공합니다.")
@RequestMapping("/api/v1/products")
class ProductCrawlingController(
    private val productCrawlingUseCase: ProductCrawlingUseCase
) {

    @

}