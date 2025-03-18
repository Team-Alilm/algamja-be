package org.team_alilm.controller.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.global.error.RequestValidateException

@RestController
@RequestMapping("/api/v2/products")
@Tag(name = "장바구니 메인 조회 API", description = """
    메인 page에서 사용하는 API를 제공합니다.
""")
class ProductSliceController(
    private val productSliceUseCase: ProductSliceUseCase
) {

    @Operation(
        summary = "상품 조회 API V2",
        description = """
            사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.
            정렬 조건, 페이지, 사이즈를 입력받아요.
            
            기본은 기다리는 사람이 많은 순 이에요.
            
            기다리는 사람이 0명인 상품도 조회되고 있어요.
    """
    )
    @GetMapping
    fun productSliceV2(
        @ParameterObject
        @Valid
        productListParameter: ProductListParameter,

        bindingResult: BindingResult
    ): ResponseEntity<ProductSliceResponse> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        val command = ProductSliceUseCase.ProductSliceCommand(
            size = productListParameter.size,
            page = productListParameter.page
        )

        val response = ProductSliceResponse(
            customSlice = productSliceUseCase.productSlice(command)
        )

        return ResponseEntity.ok(response)
    }

    data class ProductSliceResponse(
        val customSlice: ProductSliceUseCase.CustomSlice
    )

    @Schema(description = "상품 조회 파라미터")
    data class ProductListParameter(
        @Schema(description = "페이지 사이즈", defaultValue = "10")
        val size: Int,
        @Schema(description = "페이지 번호", defaultValue = "0")
        val page: Int
    )
}
