package org.teamalilm.alilmbe.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import org.teamalilm.alilmbe.service.product.BasketService
import org.teamalilm.alilmbe.service.product.BasketService.BasketFindAllCommand
import org.teamalilm.alilmbe.service.product.BasketService.BasketFindBasketCommand

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "baskets", description = "상품 전체 조회 api")
class BasketController(
    private val basketService: BasketService
) {

    @Operation(
        summary = "상품 조회 API",
        description = """
            사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.
            정렬 조건, 페이지, 사이즈를 입력받아요.
            
            기다리는 사람이 0명이라도 조회 응답 데이터에 포함되어 있어요.
            
            기본은 기다리는 사람이 많은 순이며 같다면 상품명 순 입니다.
            
            기다리는 사람 순, 업데이트된 최신 순 으로 정렬 가능해요.
    """
    )
    @GetMapping
    fun findAll(
        @ParameterObject
        @Valid
        basketFindAllParameter: BasketFindAllParameter
    ): ResponseEntity<Slice<BasketFindAllResponse>> {
        val pageRequest = PageRequest.of(
            basketFindAllParameter.page,
            basketFindAllParameter.size,
            Sort.by(Sort.Direction.DESC, "id")
        )

        return ResponseEntity.ok(
            basketService.findAll(
                BasketFindAllCommand(pageRequest)
            )
        )
    }

    @Schema(description = "상품 조회 파라미터")
    data class BasketFindAllParameter(
        @NotBlank(message = "사이즈는 필수에요.")
        @Min(value = 1, message = "사이즈는 1 이상이어야 합니다.")
        @Schema(description = "페이지 사이즈", defaultValue = "10")
        val size: Int,

        @NotBlank(message = "페이지 번호는 필수에요.")
        @Schema(description = "페이지 번호", defaultValue = "0")
        @Min(value = 0, message = "페이지 번호는 1 이상이어야 합니다.")
        val page: Int
    )

    data class BasketFindAllResponse(
        val count: Long,
        val id: Long,
        val name: String,
        val imageUrl: String,
        val productInfo: ProductInfo,
        val createdDate: Long
    )

    @Operation(
        summary = "상품 상세 조회 API",
        description = """
            사용자들이 등록한 상품을 상세 조회할 수 있는 기능을 제공해요.
            상품 id를 입력받아요.
    """
    )
    @GetMapping("/{id}")
    fun findMyBasket(@AuthenticationPrincipal member: Member): ResponseEntity<List<BasketFindMyBasketResponse>> {
        return ResponseEntity.ok(basketService.findMyBasket(BasketFindBasketCommand(member)))
    }

    data class BasketFindMyBasketResponse(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val productInfo: ProductInfo,
        val createdDate: Long,
    )
}