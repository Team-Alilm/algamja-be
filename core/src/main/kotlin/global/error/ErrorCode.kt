package org.team_alilm.global.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // 🔄 400 BAD_REQUEST
    BASKET_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ALILM-002", "장바구니가 이미 존재합니다."),
    DUPLICATE_BASKET(HttpStatus.BAD_REQUEST, "ALILM-008", "장바구니에 이미 담긴 상품입니다."),
    DUPLICATE_FCM_TOKEN(HttpStatus.BAD_REQUEST, "ALILM-009", "이미 등록된 FCM 토큰입니다."),

    // 🔄 404 NOT_FOUND
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "ALILM-003", "회원을 찾을 수 없습니다."),
    NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "ALILM-004", "권한을 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "ALILM-005", "상품을 찾을 수 없습니다."),
    NOT_FOUND_BASKET(HttpStatus.NOT_FOUND, "ALILM-007", "장바구니를 찾을 수 없습니다."),
    NOT_FOUND_ALILM(HttpStatus.NOT_FOUND, "ALILM-011", "알림을 찾을 수 없습니다.")
}