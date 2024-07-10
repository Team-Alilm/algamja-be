package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.out.persistence.jpa.entity.member.Member
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import org.teamalilm.alilmbe.service.basket.BasketMyService
import org.teamalilm.alilmbe.service.basket.BasketMyService.*

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "my baskets", description = "내가 등록한 상품 조회 API")
class BasketMyController(
    private val basketMyService: BasketMyService
) {

    @Operation(
        summary = "나의 상품 조회 API",
        description = """
            내가 등록한 상품을 조회할 수 있는 기능을 제공해요.
        """
    )
    @GetMapping("/my")
    fun basketMy(@AuthenticationPrincipal member: Member): ResponseEntity<List<BasketMyResponse>> {
        val command = MyBasketCommand(member)

        val result = basketMyService.myBasket(command)

        val response = result.map {
            BasketMyResponse(
                id = it.id,
                name = it.name,
                brand = it.brand,
                imageUrl = it.imageUrl,
                category = it.category,
                price = it.price,
                productInfo = ProductInfo(
                    store = it.productInfo.store,
                    number = it.productInfo.number,
                    option1 = it.productInfo.option1,
                    option2 = it.productInfo.option2,
                    option3 = it.productInfo.option3
                ),
                createdDate = it.createdDate
            )
        }

        return ResponseEntity.ok(response)
    }

    data class BasketMyResponse(
        val id: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val productInfo: ProductInfo,
        val createdDate: Long,
    )
}