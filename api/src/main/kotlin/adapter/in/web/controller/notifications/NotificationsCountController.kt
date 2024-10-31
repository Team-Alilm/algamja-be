package org.team_alilm.adapter.`in`.web.controller.notifications

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "알림 히스토리 API", description = "알림 히스토리 관련 API")
class NotificationsCountController(
    private val alilmHistoryUseCase: AlilmHistoryUseCase
) {

    // todo alilm에 대한 로직이 복잡해 지면 테이블을 분리하고 고도화 작업을 진행하면 좋을 것 같습니다.
    // 현재는 장바구니 테이블에 의존적으로 개발 합니다.
    @Operation(
        summary = "알림 기록 조회 API",
        description = """
            실질적으로 장바구니에서 카운팅 해서 내려가는 데이터 입니다.
            장바구니에 대한 알림을 조회합니다.
    """)
    @GetMapping("/count")
    fun alilmHistory(): ResponseEntity<AlilmHistoryResponse> {
        val result = alilmHistoryUseCase.alilmHistory()

        return ResponseEntity.ok(AlilmHistoryResponse.from(result))
    }

    data class AlilmHistoryResponse(
        val allCount : Long,
        val dailyCount : Long,
    ) {

        companion object {
            fun from(alilmHistoryResult: AlilmHistoryUseCase.AlilmHistoryResult) : AlilmHistoryResponse {
                return AlilmHistoryResponse(
                    allCount = alilmHistoryResult.allCount + 10,
                    dailyCount = alilmHistoryResult.dailyCount + 2,
                )
            }
        }
    }

}
