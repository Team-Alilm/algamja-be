package org.teamalilm.alilmbe.controller.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.controller.product.data.ProductFindAllView
import org.teamalilm.alilmbe.controller.product.data.ProductSaveRequestData
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.service.ProductService

@Tag(name = "상품 API")
@RestController
@RequestMapping("/api/v1/product")
class ProductController(
    val productService: ProductService
) {

    @Operation(
        summary = "상품 등록",
        description = "상품을 등록해요."
    )
    @PostMapping
    fun registration(
        @RequestBody
        @Valid
        productSaveRequestData: ProductSaveRequestData,

        @AuthenticationPrincipal
        member: Member
    ): ResponseEntity<Unit> {
        productService.registration(productSaveRequestData, member)

        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "상품 전체 조회",
        description = "상품을 전체 조회해요."
    )
    @GetMapping
    fun findAll(): ResponseEntity<List<ProductFindAllView>> {
        val productFindAllViews: List<ProductFindAllView> = productService.findAll()

        return ResponseEntity.ok().body(productFindAllViews)
    }
}

