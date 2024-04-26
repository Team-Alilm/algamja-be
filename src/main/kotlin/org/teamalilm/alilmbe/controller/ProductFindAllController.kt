package org.teamalilm.alilmbe.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import org.teamalilm.alilmbe.global.SortCondition
import org.teamalilm.alilmbe.service.product.ProductFindAllService

@RestController
@RequestMapping("api/v1/products")
@Tag(name = "product", description = "상품 전체 조회 api")
class ProductFindAllController(
    private val productFindAllService: ProductFindAllService
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
    fun findAll(@ParameterObject @Valid productFindAllParameter: ProductFindAllParameter): ResponseEntity<ProductFindAllResponse> {
        val pageRequest = PageRequest.of(
            productFindAllParameter.page,
            productFindAllParameter.size,
            productFindAllParameter.sortCondition.sort
        )

        productFindAllService.findAll(
            ProductFindAllService.ProductFindAllCommand(pageRequest)
        )

        return ResponseEntity.ok().build()
    }

    data class ProductFindAllParameter(
        @NotBlank(message = "사이즈는 필수에요.")
        @Min(value = 1, message = "사이즈는 1 이상이어야 합니다.")
        val size: Int,
        @NotBlank(message = "페이지 번호는 필수에요.")
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        val page: Int,
        @NotBlank(message = "정렬 조건은 필수에요.")
        val sortCondition: SortCondition
    )

    data class ProductFindAllResponse(
        val name: String,
        val imageUrl: String,
        val productInfo: ProductInfo,
    )
}