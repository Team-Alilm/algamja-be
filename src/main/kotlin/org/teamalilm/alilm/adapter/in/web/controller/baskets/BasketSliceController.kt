package org.teamalilm.alilm.adapter.`in`.web.controller.baskets

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilm.application.port.`in`.use_case.BasketSliceUseCase
import org.teamalilm.alilm.common.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "장바구니 메인 조회 API", description = "메인 page에서 사용하는 API를 제공합니다.")
class BasketSliceController(
    private val basketSliceUseCase: BasketSliceUseCase
) {

    @Operation(
        summary = "상품 조회 API",
        description = """
            사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.
            정렬 조건, 페이지, 사이즈를 입력받아요.
            
            기본은 기다리는 사람이 많은 순 이에요.
    """
    )
    @GetMapping
    fun productSlice(
        @ParameterObject
        @Valid
        productListParameter: ProductListParameter,

        bindingResult: BindingResult
    ): ResponseEntity<Slice<ProductListResponse>> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }
        val command = BasketSliceUseCase.BasketListCommand.from(productListParameter)

        val resultSlice = basketSliceUseCase.basketSlice(command)

        return ResponseEntity.ok(resultSlice.map { ProductListResponse.from(it) })
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
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val waitingCount: Long,
    ) {

        companion object {
            fun from(result: BasketSliceUseCase.BasketListResult): ProductListResponse {
                return ProductListResponse(
                    id = result.id,
                    number = result.number,
                    name = result.name,
                    brand = result.brand,
                    imageUrl = result.imageUrl,
                    category = result.category,
                    price = result.price,
                    firstOption = result.firstOption,
                    secondOption = result.secondOption,
                    thirdOption = result.thirdOption,
                    waitingCount = result.waitingCount,
                )
            }
        }
    }

}