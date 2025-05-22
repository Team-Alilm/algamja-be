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
import org.team_alilm.global.error.RequestValidateException

@RestController
@RequestMapping("/api")
@Tag(name = "장바구니 메인 조회 API", description = """
    메인 page에서 사용하는 API를 제공합니다.
""")
class ProductSliceControllerV2(
    private val productSliceUseCase: ProductSliceUseCase
) {

    @Operation(summary = "상품 조회 API V2", description = """
            사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.
        """)
    @GetMapping("/v2/products")
    fun productSliceV2(
        @ParameterObject @Valid param: ProductListParameterV2,
        bindingResult: BindingResult
    ): ResponseEntity<ProductSliceResponse> {
        validate(bindingResult)

        val command = ProductSliceUseCase.ProductSliceCommandV2(
            size = param.size,
            page = param.page,
            category = param.parsedCategory(),
        )

        val result = productSliceUseCase.productSliceV2(command)

        val response = ProductSliceResponse(customSlice = result)

        return ResponseEntity.ok(response)
    }

    private fun validate(bindingResult: BindingResult) {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }
    }

    data class ProductSliceResponse(
        val customSlice: ProductSliceUseCase.CustomSlice
    )

    @Schema(description = "상품 조회 파라미터")
    data class ProductListParameterV2(

        @field:Min(1)
        @Schema(description = "페이지 사이즈", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        val size: Int = 10,

        @field:Min(0)
        @Schema(description = "페이지 번호", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        val page: Int = 0,

        @Schema(description = "카테고리 null 가능", example = "전체", requiredMode = Schema.RequiredMode.REQUIRED)
        val category: ProductCategory = ProductCategory.ALL,
    )

    fun ProductListParameterV2.parsedCategory(): String? {
        return if (category == ProductCategory.ALL) null else category.description
    }
}
