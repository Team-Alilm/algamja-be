package org.team_alilm.adapter.`in`.web.controller.notifications

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase

@RestController
@RequestMapping( "/api/v1/alilms")
@Tag(name = "알림 히스토리 API", description = """
    알림 히스토리 관련 API
    alilm 건 수를 반환하는 API 입니다.
    
    현재 2개의 주소로 운영되고 있으며
    향후 /api/v1/notifications는 사용을 종료 합니다.
    """)
class AlilmCountController(
    private val alilmHistoryUseCase: AlilmHistoryUseCase
) {
    // 현재는 장바구니 테이블에 의존적으로 개발 합니다.
    @Operation(
        summary = "알림 기록 조회 API",
        description = """
            실질적으로 장바구니에서 카운팅 해서 내려가는 데이터 입니다.
            장바구니에 대한 알림을 조회합니다.
            
            한국 시간으로 운영되고 있습니다.
    """)
    @GetMapping("/count")
    fun alilmHistory(): ResponseEntity<AlilmHistoryResponse> {
        val result = alilmHistoryUseCase.alilmHistory()

        return ResponseEntity.ok(AlilmHistoryResponse.from(result))
    }

    data class AlilmHistoryResponse(
        val allCount : Long,
        val dailyCount : Int,
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
