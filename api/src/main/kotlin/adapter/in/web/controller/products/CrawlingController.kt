package org.team_alilm.adapter.`in`.web.controller.products

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.CrawlingUseCase
import org.team_alilm.domain.Product
import org.team_alilm.global.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "상품 크롤링", description = "상품 정보를 쇼핑몰에서 가져오는 API (현재 무신사만 지원)")
class CrawlingController(
    private val crawlingUseCase: CrawlingUseCase
) {

    @Operation(
        summary = "상품 크롤링을 실행하는 API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 추출하는 API 입니다. 
            저희 서비스에 데이터를 저장하는 로직이 1도 없습니다.
            (현재 무신사만 지원해요.)
        """
    )
    @GetMapping("/crawling")
    fun crawling(
        @ParameterObject
        @Valid
        request: CrawlingRequest,

        bindingResult: BindingResult
    ) : ResponseEntity<CrawlingResponse> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        return ResponseEntity.ok(
            CrawlingResponse.from(
                crawlingUseCase.productCrawling(
                    CrawlingUseCase.ProductCrawlingCommand.from(request)
                )
            )
        )
    }

    @Schema(description = "Scraping 요청")
    data class CrawlingRequest(
        @field:NotBlank(message = "URL은 필수입니다.")
        @field:Schema(description = "상품 URL (무신사 URL만 지원해요) 앱 공유 링크도 지원해요.", example = "https://musinsaapp.page.link/ymDEcMU5pFeQDvea9")
        val _url: String?
    ) {

        val url: String
            get() = _url!!
    }

    @Schema(description = "상품 데이터 응답")
    data class CrawlingResponse(
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Product.Store,
        val firstOptions: List<String>,
        val secondOptions: List<String>,
        val thirdOptions: List<String>
    ) {

        companion object {
            fun from(result: CrawlingUseCase.CrawlingResult): CrawlingResponse {
                return CrawlingResponse(
                    number = result.number,
                    name = result.name,
                    brand = result.brand,
                    imageUrl = result.imageUrl,
                    category = result.category,
                    price = result.price,
                    store = result.store,
                    firstOptions = result.firstOptions,
                    secondOptions = result.secondOptions,
                    thirdOptions = result.thirdOptions
                )
            }
        }
    }
}