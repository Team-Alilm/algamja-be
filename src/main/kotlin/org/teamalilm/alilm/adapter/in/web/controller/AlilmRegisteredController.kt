package org.teamalilm.alilm.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails
import org.teamalilm.alilm.application.port.`in`.use_case.AlilmRegistrationUseCase
import org.teamalilm.alilm.common.error.RequestValidateException
import org.teamalilm.alilm.domain.Product

@RestController
@RequestMapping("/api/v1/alilms")
@Tag(name = "상품 등록 API", description = "상품 정보를 등록하는 API")
class AlilmRegisteredController(
    private val alilmRegistrationUseCase: AlilmRegistrationUseCase
) {

    @Operation(
        summary = "상품 등록 API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 등록하는 API 입니다.
        """
    )
    @PostMapping("/registered")
    fun registered(
        @RequestBody @Valid request: AlilmRegistrationRequest,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,

        bindingResult: BindingResult
    ): ResponseEntity<Unit> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        alilmRegistrationUseCase.alilmRegistration(
            AlilmRegistrationUseCase.AlilmRegistrationCommand.from(request, customMemberDetails.member)
        )

        return ResponseEntity.ok().build()
    }

    @Schema(description = "상품 등록 요청")
    data class AlilmRegistrationRequest(
        @field:NotNull(message = "URL은 필수입니다.")
        @field:Schema(
            example = "3262292",
            description = "상품 번호",
            format = "int64",
            required = true,
            type = "integer"
        )
        val number: Long,
        @field:NotBlank(message = "상품 이름은 필수입니다.")
        @field:Schema(
            example = "오버사이즈 립 포켓 하프 셔츠 블랙",
            description = "상품 이름",
            required = true
        )
        val name: String,
        @field:NotBlank(message = "브랜드는 필수입니다.")
        @field:Schema(
            example = "굿라이프웍스",
            description = "브랜드",
            required = true
        )
        val brand: String,
        @field:NotBlank(message = "이미지 URL은 필수입니다.")
        @field:Schema(
            example = "https://image.msscdn.net/images/goods_img/20230426/3262292/3262292_16885217563686_500.jpg",
            description = "이미지 URL",
            required = true
        )
        val imageUrl: String,
        @field:NotBlank(message = "카테고리는 필수입니다.")
        @field:Schema(
            example = "상의 > 셔츠/블라우스",
            description = "카테고리",
            required = true
        )
        val category: String,
        @field:NotNull(message = "가격은 필수입니다.")
        @field:Schema(
            example = "31300",
            description = "가격",
            format = "int32",
            required = true,
            type = "integer"
        )
        val price: Int,
        @field:NotNull(message = "스토어는 필수입니다.")
        @field:Schema(
            description = "스토어",
            example = "MUSINSA",
            required = true
        )
        val store: Product.Store,
        @field:NotBlank(message = "옵션은 필수입니다.")
        @field:Schema(
            description = "옵션1",
            example = "M",
            required = true
        )
        val option1: String,
        @field:Schema(
            description = "옵션2",
            example = "블랙"
        )
        val option2: String?,
        @field:Schema(
            description = "옵션3",
            example = "포켓"
        )
        val option3: String?
    )

}