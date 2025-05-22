package org.team_alilm.controller.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.domain.product.ProductCategory
import org.team_alilm.domain.product.ProductSortType
import org.team_alilm.global.error.RequestValidateException
import org.team_alilm.response.ApiResponse
import org.team_alilm.response.ApiResponseFactory

@RestController
@RequestMapping("/api/v3/products")
@Tag(
    name = "장바구니 메인 조회 API V3",
    description = "메인 page에서 사용하는 API를 제공합니다."
)
class ProductSliceControllerV3(
    private val productSliceUseCase: ProductSliceUseCase
) {

    @Operation(summary = "상품 조회 API V3", description = "사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.")
    @GetMapping
    fun productSliceV3(
        @ParameterObject @Valid param: ProductListParameterV3,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<ProductSliceResponse>> {
        if (bindingResult.hasErrors()) throw RequestValidateException(bindingResult)

        val command = ProductSliceUseCase.ProductSliceCommand(
            size = param.size,
            category = if (param.category == ProductCategory.ALL) null else param.category.description,
            sort = param.sort.name,
            lastProductId = param.lastProductId,
            waitingCount = param.waitingCount,
            price = param.price
        )

        val response = ProductSliceResponse(productSliceUseCase.productSlice(command))
        return ApiResponseFactory.ok(response)
    }

    data class ProductSliceResponse(
        val customSlice: ProductSliceUseCase.CustomSlice
    )

    @Schema(description = "상품 조회 파라미터")
    data class ProductListParameterV3(

        @field:Min(1)
        @Schema(description = "페이지 사이즈", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        val size: Int = 10,

        @Schema(description = "카테고리 null 가능", example = "전체", requiredMode = Schema.RequiredMode.REQUIRED)
        val category: ProductCategory = ProductCategory.ALL,

        @Schema(description = "정렬 조건", example = "WAITING_COUNT", requiredMode = Schema.RequiredMode.REQUIRED)
        val sort: ProductSortType = ProductSortType.WAITING_COUNT,

        @field:Min(1)
        @Schema(description = "마지막 상품 ID", example = "1")
        val lastProductId: Long?,

        @field:Min(0)
        @Schema(description = "마지막 상품 대기 인원 수", example = "1")
        val waitingCount: Long?,

        @field:Min(0)
        @Schema(description = "상품 가격", example = "1")
        val price: Int? = null
    )
}