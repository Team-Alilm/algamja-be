package org.teamalilm.alilmbe.controller

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.Store
import org.teamalilm.alilmbe.domain.product.service.ProductService
import java.time.LocalDateTime
import java.time.ZonedDateTime

@RestController
@RequestMapping("/api/v1/product")
class ProductController(
    val productService: ProductService
) {

    @PostMapping()
    fun registration(
        @RequestBody
        @Valid
        productSaveForm: ProductSaveForm,
    ): ResponseEntity<Any> {
        productService.registration(productSaveForm)

        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun findAll(): ResponseEntity<List<ProductFindAllView>> {
        val productFindAllViews: List<ProductFindAllView> = productService.findAll();

        return ResponseEntity.ok().body(productFindAllViews)
    }
}

data class ProductSaveForm(
    @Schema(
        description = "상품 이미지 url 입니다.",
        nullable = false,
        example = "https://image.msscdn.net/images/goods_img/20240208/3859221/3859221_17084100068855_500.jpg"
    )
    @NotBlank(message = "이미지 url은 필수에요.")
    val imageUrl: String,

    @Schema(
        description = "상품명 입니다.",
        nullable = false,
        example = "COOL 롱 슬리브 셔츠 STYLE 3 TIPE"
    )
    @NotBlank(message = "상품명은 필수에요.")
    val name: String,

    @Schema(
        description = "상품 스토어 입니다.",
        nullable = false,
        example = "MUSINSA"
    )
    @NotBlank(message = "상품을 구매한 store는 필수에요.")
    val store: Store, // 스토어 이름

    @Schema(
        description = "상품 번호 입니다.",
        nullable = false,
        example = "3859221"
    )
    @NotBlank(message = "상품 번호는 필수에요.")
    val productNumber: Long, // 상품 번호

    @Schema(
        description = "상품 옵션 1 입니다.",
        nullable = true,
        example = "(헤링본)화이트 or S, M"
    )
    val option1: String?, // 상품 사이즈

    @Schema(
        description = "상품 옵션 2 입니다.",
        nullable = true,
        example = "(헤링본)화이트 or S, M"
    )
    val option2: String? // 상품 색상
)

data class ProductFindAllView(
    val productId: Long,
    val name: String,
    val imageUrl: String,
    val store: Store,
    val size: String,
    val color: String,
    val createdDate: ZonedDateTime
) {

    companion object {
        fun of(product: Product): ProductFindAllView {
            return ProductFindAllView(
                productId = product.id!!,
                name = product.name,
                imageUrl = product.imageUrl,
                store = product.store,
                size = product.productInfo.option1!!,
                color = product.productInfo.option2!!,
                createdDate = product.createdDate!!
            )
        }
    }
}