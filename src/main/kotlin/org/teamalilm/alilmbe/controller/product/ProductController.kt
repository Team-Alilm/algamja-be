package org.teamalilm.alilmbe.controller.product

import ProductFindAllData
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
import org.teamalilm.alilmbe.controller.product.data.ProductSaveRequestBody
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.service.ProductService

@Tag(name = "상품 API")
@RestController
@RequestMapping("/api/v1/products")
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
        productSaveRequestBody: ProductSaveRequestBody,

        @AuthenticationPrincipal
        member: Member
    ): ResponseEntity<Unit> {
        productService.registration(productSaveRequestBody, member)

        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "등록된 상품 전체 조회",
        description = "현재 basket에 등록된 상품을 출력해요."
    )
    @GetMapping("")
    fun findAll(): ResponseEntity<List<ProductFindAllData>> {
        val productFindAllData: List<ProductFindAllData> = productService.findAll()

        return ResponseEntity.ok().body(productFindAllData)
    }

}

