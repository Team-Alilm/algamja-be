package org.team_alilm.adapter.`in`.web.controller.member

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.team_alilm.application.port.`in`.use_case.MyAlilmReadAllUseCase
import org.team_alilm.application.port.`in`.use_case.MyAlilmReadUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "내 알림 모두 읽음처리 API", description = "내 알림을 모두 읽음 처리하는 API")
class MyAlilmReadAllController(
    private val myAlilmReadAllUseCase: MyAlilmReadAllUseCase
) {

    @Operation(
        summary = "내 알림 모두 읽음처리 API",
        description = """
            내 알림을 모두 읽음처리합니다.
        """)
    @PatchMapping("/my-alilm-read-all")
    fun myAlilmReadAll(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
    ) : ResponseEntity<Unit> {
        val command = MyAlilmReadAllUseCase.MyAlilmReadCommand(
            member = customMemberDetails.member
        )

        myAlilmReadAllUseCase.myAlilmReadAll(command)

        return ResponseEntity.ok().build()
    }

}