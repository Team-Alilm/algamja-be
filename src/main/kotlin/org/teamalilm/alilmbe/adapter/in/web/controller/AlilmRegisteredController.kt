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
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationUseCase
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.web.adapter.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/alilms")
@Tag(name = "product-registered", description = "상품 정보를 등록하는 API")
class AlilmRegisteredController(
    private val alilmRegistrationUseCase: AlilmRegistrationUseCase
) {

    @Operation(
        summary = "상품 등록을 실행하는 API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 등록하는 API 입니다.
        """
    )
    @PostMapping("/registered")
    fun registered(
        @RequestBody @Valid request: AlilmRegistrationRequest,
        @AuthenticationPrincipal member: Member,
        bindingResult: BindingResult
    ): ResponseEntity<Unit> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        alilmRegistrationUseCase.alilmRegistration(
            AlilmRegistrationCommand.from(request, member)
        )

        return ResponseEntity.ok().build()
    }

    @Schema(description = "상품 등록 요청")
    data class AlilmRegistrationRequest(
        @Schema(
            example = "405656",
            description = "상품 번호",
            format = "int64",
            required = true,
            type = "integer"
        )
        val number: Long,
        @Schema(
            example = "나이키 에어맥스 97",
            description = "상품 이름",
            required = true
        )
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