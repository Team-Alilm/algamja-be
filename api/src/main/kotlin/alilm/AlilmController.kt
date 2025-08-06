package org.team_alilm.alilm

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase


@RestController
@RequestMapping("/api/v1/alilms")
@Tag(name = "알림 API", description = """
    알림 API 들을 모아둔 컨트롤러 입니다.
""")
class AlilmController(
    private val alilmHistoryUseCase: AlilmHistoryUseCase
) {

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
        return ResponseEntity.ok(
            AlilmHistoryResponse.Companion.from(
                result
            )
        )
    }

    data class AlilmHistoryResponse(
        val allCount: Long,
        val dailyCount: Long,
    ) {
        companion object {
            fun from(alilmHistoryResult: AlilmHistoryUseCase.AlilmHistoryResult): AlilmHistoryResponse {
                return AlilmHistoryResponse(
                    allCount = (alilmHistoryResult.allCount ?: 0) + 10,
                    dailyCount = (alilmHistoryResult.dailyCount ?: 0) + 2,
                )
            }
        }
    }
}

