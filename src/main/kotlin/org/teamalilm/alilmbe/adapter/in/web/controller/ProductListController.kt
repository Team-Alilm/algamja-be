package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductInfo

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "products", description = "상품 전체 조회 api")
class ProductListController(
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
    fun productList(
        @ParameterObject
        @Valid
        productListParameter: ProductListParameter

    ): ResponseEntity<Slice<ProductListResponse>> {
//        val pageRequest = PageRequest.of(
//            productListParameter.page,
//            productListParameter.size,
//            Sort.by(Sort.Direction.DESC, "id")
//        )
//
//        val command = BasketFindAllCommand(pageRequest)
//
//        val result = productListService.listProduct(command)
//
//        val response = result.map {
//            ProductListResponse(
//                id = it.id,
//                name = it.name,
//                brand = it.brand,
//                imageUrl = it.imageUrl,
//                price = it.price,
//                category = it.category,
//                productInfo = it.productInfo,
//                waitingCount = it.waitingCount,
//                oldestCreationTime = it.oldestCreationTime
//            )
//        }

        return ResponseEntity.ok(null)
    }

    @Schema(description = "상품 조회 파라미터")
    data class ProductListParameter(
        @NotBlank(message = "사이즈는 필수에요.")
        @Min(value = 1, message = "사이즈는 1 이상이어야 합니다.")
        @Schema(description = "페이지 사이즈", defaultValue = "10")
        val size: Int,

        @NotBlank(message = "페이지 번호는 필수에요.")
        @Schema(description = "페이지 번호", defaultValue = "0")
        @Min(value = 0, message = "페이지 번호는 1 이상이어야 합니다.")
        val page: Int
    )

    data class ProductListResponse(
        val id: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val productInfo: ProductInfo,
        val waitingCount: Long,
        val oldestCreationTime: Long
    )

}