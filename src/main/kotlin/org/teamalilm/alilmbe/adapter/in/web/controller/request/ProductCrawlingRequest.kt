package org.teamalilm.alilmbe.adapter.`in`.web.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class ProductCrawlingRequest(
    @NotBlank(message = "URL은 필수에요.")
    @Schema(
        description = "크롤링할 상품 URL",
        defaultValue = "https://www.musinsa.com/app/goods/3262292"
    )
    val url: String
)