package org.team_alilm.adapter.`in`.web.controller.alilm

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase
import org.team_alilm.application.port.`in`.use_case.AlilmRecentUseCase
import org.team_alilm.domain.product.Product

@RestController
@RequestMapping("/api/v1/alilms")
@Tag(name = "알림 API", description = """
    알림 API 들을 모아둔 컨트롤러 입니다.
""")
class AlilmController(
    private val alilmRecentUseCase: AlilmRecentUseCase,
    private val alilmHistoryUseCase: AlilmHistoryUseCase
) {

    @Operation(
        summary = "최근 알림 조회 API",
        description = """
            최근 일주일 이내에 재 입고 상품 목록을 반환합니다.
            최대 12개 입니다.
    """)
    @GetMapping("/recent")
    fun alilmRecent(): ResponseEntity<AlilmRecentResponse> {
        val result = alilmRecentUseCase.alilmRecent()
        val productList = result.productList.map { RecentProduct.from(it) }

        val response = AlilmRecentResponse(productList = productList)
        return ResponseEntity.ok(response)
    }

    data class AlilmRecentResponse(
        val productList: List<RecentProduct>
    )

    data class RecentProduct(
        val id: Long,
        val name: String,
        val imageUrl: String,
    ) {
        companion object {
            fun from(product: Product): RecentProduct {
                return RecentProduct(
                    id = product.id?.value ?: 0,
                    name = product.name,
                    imageUrl = product.thumbnailUrl
                )
            }
        }
    }

    @Operation(
        summary = "알림 기록 조회 API",
        description = """
            실질적으로 장바구니에서 카운팅 해서 내려가는 데이터 입니다.
            장바구니에 대한 알림을 조회합니다.
            한국 시간으로 운영되고 있습니다.
    """)
    @GetMapping("/count")
    fun alilmHistory(): ResponseEntity<AlilmHistoryResponse> {
        val result = alilmHistoryUseCase.alilmHistory()
        return ResponseEntity.ok(AlilmHistoryResponse.from(result))
    }

    data class AlilmHistoryResponse(
        val allCount: Long,
        val dailyCount: Long,
    ) {
        companion object {
            fun from(alilmHistoryResult: AlilmHistoryUseCase.AlilmHistoryResult): AlilmHistoryResponse {
                return AlilmHistoryResponse(
                    allCount = (alilmHistoryResult.allCount ?: 0) + 10,
                    dailyCount = (alilmHistoryResult.dailyCount ?: 0) + 2,
                )
            }
        }
    }
}

