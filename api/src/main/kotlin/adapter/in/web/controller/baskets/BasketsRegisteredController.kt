package org.team_alilm.adapter.`in`.web.controller.baskets

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
import org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase
import org.team_alilm.data.CustomMemberDetails
import org.team_alilm.domain.Product
import org.team_alilm.global.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "상품 등록 API", description = "상품 정보를 등록하는 API")
class BasketsRegisteredController(
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
        if (bindingResult.hasErrors()) throw RequestValidateException(bindingResult)


        val command = AlilmRegistrationUseCase.AlilmRegistrationCommand(
            number = request.number,
            name = request.name,
            brand = request.brand,
            imageUrl = request.imageUrl,
            category = request.category,
            price = request.price,
            store = request.store,
            firstOption = request.firstOption,
            secondOption = request.secondOption,
            thirdOption = request.thirdOption,
            member = customMemberDetails.member
        )
        alilmRegistrationUseCase.alilmRegistration(command)

        return ResponseEntity.ok().build()
    }

    @Schema(description = "상품 등록 요청")
    data class AlilmRegistrationRequest(
        @field:NotNull(message = "URL은 필수입니다.")
        @field:Schema(
            example = "4235404",
            description = "상품 번호",
            format = "int64",
            required = true,
            type = "integer"
        )
        val number: Long,

        @field:NotBlank(message = "상품 이름은 필수입니다.")
        @field:Schema(
            example = "YEEZY BOOST 380 GZ0454",
            description = "상품 이름",
            required = true
        )
        val name: String,

        @field:NotBlank(message = "브랜드는 필수입니다.")
        @field:Schema(
            example = "adidas",
            description = "브랜드",
            required = true
        )
        val brand: String,

        @field:NotBlank(message = "이미지 URL은 필수입니다.")
        @field:Schema(
            example = "https://image.msscdn.net/images/goods_img/20240705/4235404/4235404_17201553979081_500.jpg",
            description = "이미지 URL",
            required = true
        )
        val imageUrl: String,

        @field:NotBlank(message = "카테고리는 필수입니다.")
        @field:Schema(
            example = "신발",
            description = "카테고리",
            required = true
        )
        val category: String,

        @field:NotNull(message = "가격은 필수입니다.")
        @field:Schema(
            example = "95700",
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
            example = "220",
            required = true
        )
        val firstOption: String,

        @field:Schema(
            description = "옵션2",
            example = ""
        )
        val secondOption: String?,

        @field:Schema(
            description = "옵션3",
            example = ""
        )
        val thirdOption: String?

    )

}