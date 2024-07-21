package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.`in`.web.controller.request.AlilmRegistrationRequest
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationUseCase

@RestController
@RequestMapping("/api/v1/alilms")
@Tag(name = "alilms", description = "재 입고 알림 API")
class AlilmRegistrationController(
    private val registrationAlilmService: AlilmRegistrationUseCase

) {

    @Operation(
        summary = "재 입고 알림을 등록하는 API",
        description = """
            재 입고 알림을 등록하는 API 이며, 이미 등록된 상품을 등록 시
            상품은 등록되지 않고 사용자의 장바구니만 추가해요.
        """
    )
    @PostMapping("/registration")
    fun registration(
        @RequestBody
        @Valid
        alilmRegistrationRequestBody: AlilmRegistrationRequest,

        @AuthenticationPrincipal
        member: Member
    ): ResponseEntity<Unit> {
        val command = AlilmRegistrationCommand(
            number = alilmRegistrationRequestBody.number,
            name = alilmRegistrationRequestBody.name,
            brand = alilmRegistrationRequestBody.brand,
            store = alilmRegistrationRequestBody.store,
            imageUrl = alilmRegistrationRequestBody.imageUrl,
            category = alilmRegistrationRequestBody.category,
            price = alilmRegistrationRequestBody.price,
            option1 = alilmRegistrationRequestBody.option1,
            option2 = alilmRegistrationRequestBody.option2,
            option3 = alilmRegistrationRequestBody.option3,
            member,
        )

        registrationAlilmService(command)

        return ResponseEntity.ok().build()
    }

}
