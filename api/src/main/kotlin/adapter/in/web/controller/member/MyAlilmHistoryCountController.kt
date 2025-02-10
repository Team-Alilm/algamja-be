package org.team_alilm.adapter.`in`.web.controller.member

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.MyAlilmHistoryCountUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "내 알림 히스토리 개수 API", description = "내 알림 히스토리 개수 관련 API")
class MyAlilmHistoryCountController (
    private val myAlilmHistoryCountUseCase: MyAlilmHistoryCountUseCase
){

    @Operation(
        summary = "내 알림 히스토리 중 읽지 않은 알림 개수 조회 API",
        description = """
            내 알림 히스토리 중 읽지 않은 알림 개수를 조회합니다.
            (오늘 - 31일)까지 조회합니다.
        """
    )
    @GetMapping("/my-alilm-history/read-n-count")
    fun myAlilmHistoryCount(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<MyAlilmHistoryCountResponse> {
        val command = MyAlilmHistoryCountUseCase.MyAlilmHistoryCountCommand(customMemberDetails.member)
        val result = myAlilmHistoryCountUseCase.myAlilmHistoryCount(command)
        val response = MyAlilmHistoryCountResponse.from(result)

        return ResponseEntity.ok(response)
    }

    data class MyAlilmHistoryCountResponse(
        val readNCount: Long
    ) {
        companion object {
            fun from (myAlilmHistoryCountResult: MyAlilmHistoryCountUseCase.MyAlilmHistoryCountResult): MyAlilmHistoryCountResponse {
                return MyAlilmHistoryCountResponse(
                    readNCount = myAlilmHistoryCountResult.readYCount
                )
            }
        }
    }
}