package adapter.`in`.web.controller.alilm

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AlilmRecentUseCase
import org.team_alilm.domain.Product

@RestController
@Tag(name = "최근 알림 상품 조회 API", description = """""")
@RequestMapping("/api/v1/alilms")
class AlilmRecentController(
    private val alilmRecentUseCase: AlilmRecentUseCase
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
        val productList : List<RecentProduct>
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
                    imageUrl = product.imageUrl
                )
            }
        }
    }

}