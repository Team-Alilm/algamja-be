package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductRegistrationCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductRegistrationUseCase
import org.teamalilm.alilmbe.web.adapter.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "product-registered", description = "상품 정보를 등록하는 API")
class ProductRegisteredController(
    private val useCase: ProductRegistrationUseCase
) {

    @Operation(
        summary = "상품 등록을 실행하는 API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 등록하는 API 입니다.
        """
    )
    @PostMapping("/registered")
    fun registered(
        @RequestBody @Valid request: ProductRegistrationRequest,
        @AuthenticationPrincipal member: Member,
        bindingResult: BindingResult
    ): ResponseEntity<Unit> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        useCase.invoke(
            ProductRegistrationCommand.from(request, member)
        )

        return ResponseEntity.ok().build()
    }

    @Schema(description = "상품 등록 요청")
    data class ProductRegistrationRequest(
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Store,
        val option1: String,
        val option2: String?,
        val option3: String?
    )

}