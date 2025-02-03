package org.team_alilm.adapter.`in`.web.controller.member

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.team_alilm.application.port.`in`.use_case.MyAlilmReadUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "내 알림 읽음처리 API", description = "내 알림 읽음 처리하는 API")
class MyAlilmReadController(
    private val myAlilmReadUseCase: MyAlilmReadUseCase
) {

    @Operation(
        summary = "내 알림 읽음처리 API",
        description = """
            내 알림을 읽음처리합니다.
        """)
    @PatchMapping("/my-alilm-read")
    fun myAlilmRead(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        @Valid @RequestBody request: UpdateMyAlilmRead
    ) : ResponseEntity<Unit> {
        val command = MyAlilmReadUseCase.MyAlilmReadCommand(
            member = customMemberDetails.member,
            alilmIdList = request.alilmIdList
        )

        myAlilmReadUseCase.myAlilmRead(command)

        return ResponseEntity.ok().build()
    }

    @Schema(description = "나의 알림 읽음처리 요청")
    data class UpdateMyAlilmRead(
        @field:NotNull(message = "알림ID는 필수입니다.")
        @field:Schema(
            example = "[1, 2, 3]",
            description = "알림ID리스트",
            required = true,
        )
        val alilmIdList: List<Long> = emptyList()
    )

}