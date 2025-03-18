package org.team_alilm.controller.alilm

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AlilmRestockRankingUseCase

@RestController
@RequestMapping("/api/v1/alilms/restock/ranking")
class AlilmRestockRankingController(
    private val alilmRestockRankingUseCase: AlilmRestockRankingUseCase
) {

    @Operation(
        summary = "최근 알림 조회 API",
        description = """
            최근 재 입고 상품 목록 수를 반환합니다.
    """)
    @GetMapping
    fun alilmRestockRanking(
        @RequestParam count: Int
    ) : ResponseEntity<AlilmRestockRankingResponse> {
        val command = AlilmRestockRankingUseCase.AlilmRestockRankingCommand(
            count = count
        )
        val result = alilmRestockRankingUseCase.alilmRestockRanking(command)
        val alilmRestockRankingProducts = result.map {
            AlilmRestockRankingProduct.from(
                productId = it.id!!.value,
                productThumbnailUrl = it.thumbnailUrl
            )
        }

        return ResponseEntity.ok(AlilmRestockRankingResponse(alilmRestockRankingProducts))
    }

    data class AlilmRestockRankingResponse(
        val productList: List<AlilmRestockRankingProduct>
    )

    data class AlilmRestockRankingProduct(
        val productId: Long,
        val productThumbnailUrl: String,
    ) {

        companion object {
            fun from(productId: Long, productThumbnailUrl: String): AlilmRestockRankingProduct {
                return AlilmRestockRankingProduct(
                    productId = productId,
                    productThumbnailUrl = productThumbnailUrl
                )
            }
        }
    }
}