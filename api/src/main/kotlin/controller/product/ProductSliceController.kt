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
import org.team_alilm.response.ApiResponse
import org.team_alilm.response.ApiResponseFactory

@RestController
@RequestMapping("/api")
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
        """
    )
    @GetMapping("/v2/products")
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
            category = productListParameter.parsedCategory(),
            sort = productListParameter.sort.name,
            lastProductId = productListParameter.lastProductId,
            waitingCount = productListParameter.waitingCount
        )

        val response = ProductSliceResponse(
            customSlice = productSliceUseCase.productSlice(command)
        )

        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "상품 조회 API V3",
        description = """
            사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.
        """
    )
    @GetMapping("/v3/products")
    fun productSliceV3(
        @ParameterObject
        productListParameter: ProductListParameter,

        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<ProductSliceResponse>> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        val command = ProductSliceUseCase.ProductSliceCommand(
            size = productListParameter.size,
            category = productListParameter.parsedCategory(),
            sort = productListParameter.sort.name,
            lastProductId = productListParameter.lastProductId,
            waitingCount = productListParameter.waitingCount
        )

        val response = ProductSliceResponse(
            customSlice = productSliceUseCase.productSlice(command)
        )

        return ApiResponseFactory.ok(response)
    }

    // "전체"일 경우 null로 치환
    // ProductListParameter에 확장 함수 정의
    fun ProductListParameter.parsedCategory(): String? {
        return if (category == "all") null else category
    }

    data class ProductSliceResponse(
        val customSlice: ProductSliceUseCase.CustomSlice
    )

    @Schema(description = "상품 조회 파라미터")
    data class ProductListParameter(
        @Schema(
            description = "페이지 사이즈",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        val size: Int = 10,

        @Schema(
            description = "페이지 번호",
            example = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        val page: Int = 0,

        @Schema(
            description = "카테고리 null 가능",
            example = "전체",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        val category: String = "all",

        @Schema(
            description = "정렬 조건",
            example = "WAITING_COUNT",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        val sort: ProductSortType = ProductSortType.WAITING_COUNT,

        //마지막 상품
        @Schema(
            description = "마지막 상품 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        val lastProductId: Long?,

        @Schema(
            description = "마지막 상품 대기 인원 수",
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        val waitingCount: Long?
    )

    enum class ProductSortType(val description: String) {
        WAITING_COUNT("함께 기다리는 사람이 많은 순"),
        LATEST("최신 등록순"),
        PRICE_ASC("낮은 가격 순"),
        PRICE_DESC("높은 가격 순");

        override fun toString(): String = name // Swagger 문서에서 값으로 표시됨
    }

    enum class ProductCategory(val description: String) {
        ALL("전체"),
        TOPS("상의"),
        OUTERWEAR("아우터"),
        PANTS("바지"),
        DRESSES_SKIRTS("원피스/스커트"),
        SHOES("신발"),
        BAGS("가방"),
        FASHION_ACCESSORIES("패션소품"),
        UNDERWEAR_HOMEWEAR("속옷/홈웨어"),
        SPORTS_LEISURE("스포츠/레저");

        override fun toString(): String = name // Swagger 문서에서 값으로 표시됨
    }
}
