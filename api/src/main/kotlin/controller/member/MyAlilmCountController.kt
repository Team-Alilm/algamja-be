package org.team_alilm.controller.member

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.MyAlilmCountUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "내 알림 API", description = "내 알림 관련 API")
class MyAlilmCountController(
    private val myAlilmCountUseCase: MyAlilmCountUseCase
) {

    @Operation(
        summary = "내 알림 개수 조회 API",
        description = """
            내 알림 개수를 조회합니다.
            사용자의 알림 개수를 가지고 오는 api 입니다.
    """)
    @GetMapping("/my-alilm-count")
    fun myAlilmCount(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<MyAlilmCountResponse> {
        val command = MyAlilmCountUseCase.MyAlilmCountCommand(customMemberDetails.member)

        val result = myAlilmCountUseCase.myAlilmCount(command)

        val response = MyAlilmCountResponse(result.alilmCount, result.basketCount)

        return ResponseEntity.ok(response)
    }

    data class MyAlilmCountResponse(
        @Schema(description = """
            내가 받은 알림 수
            """)
        val alilmCount: Int,
        @Schema(description = """
            내가 등록한 장바구니 수
            """)
        val basketCount: Int
    )

}
