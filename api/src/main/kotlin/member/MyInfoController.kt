package org.team_alilm.member

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.MyInfoUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "내 정보 API", description = "내 정보 관련 API")
class MyInfoController(
    private val myInfoUseCase: MyInfoUseCase
) {

    @Operation(
        summary = "내 정보 조회 API",
        description = """
            내 정보를 조회합니다.
            사용자의 닉네임, 이메일을 가지고 오는 api 입니다.
    """)
    @GetMapping
    fun myInfo(@AuthenticationPrincipal customMemberDetails: CustomMemberDetails) : ResponseEntity<MyInfoResponse> {
        // alilm에 대한 로직이 복잡해 지면 테이블을 분리하고 고도화 작업을 진행하면 좋을 것 같습니다.
        // 현재는 장바구니 테이블에 의존적으로 개발 합니다.
        val command = MyInfoUseCase.MyInfoCommand(customMemberDetails.member)
        val result = myInfoUseCase.myInfo(command)

        return ResponseEntity.ok(MyInfoResponse(result.nickname, result.email))
    }

    data class MyInfoResponse(
        val nickname: String,
        val email: String
    )

    @Operation(
        summary = "내 정보 수정 API",
        description = """
            내 정보를 수정합니다.
            사용자의 닉네임을 수정하는 api 입니다.
    """)
    @PostMapping
    fun updateMyInfo(
        @AuthenticationPrincipal
        customMemberDetails: CustomMemberDetails,
        @RequestBody request: UpdateMyInfoRequest
    ) : ResponseEntity<Unit> {
        val command = MyInfoUseCase.UpdateMyInfoCommand(
            member = customMemberDetails.member,
            nickname = request.nickname,
            email = request.email
        )

        myInfoUseCase.updateMyInfo(command)

        return ResponseEntity.ok().build()
    }

    data class UpdateMyInfoRequest(
        @field:Schema(
            example = "team.alilms@gmail.com",
            description = "이메일",
            required = true,
        )
        val email: String,
        val nickname: String,
    )

}

