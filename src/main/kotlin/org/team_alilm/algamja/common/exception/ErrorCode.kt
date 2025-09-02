package org.team_alilm.algamja.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,       // 서비스 내부 고유 코드
    val message: String
) {

    // Product 관련
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "상품을 찾을 수 없습니다."),
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "PRODUCT_002", "이미 등록된 상품입니다."),
    INVALID_STORE_NAME(HttpStatus.BAD_REQUEST, "PRODUCT_003", "유효하지 않은 스토어명입니다."),
    CRAWLER_NOT_FOUND(
        HttpStatus.NOT_FOUND, "CRAWLER_001", "지원하는 크롤러가 없습니다. URL을 확인해주세요."
    ),
    BASKET_NOT_FOUND(HttpStatus.NOT_FOUND, "BASKET_001", "장바구니를 찾을 수 없습니다."),
    BASKET_ALREADY_EXISTS(HttpStatus.CONFLICT, "BASKET_002", "이미 장바구니에 담긴 상품입니다."),

    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "입력 값이 유효하지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "서버 내부 오류가 발생했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "해당 회원을 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(
        HttpStatus.NOT_FOUND, "NOTIFICATION_001", "해당 알림을 찾을 수 없습니다."
    ),
    SLACK_CLIENT_NOT_INITIALIZED(
        HttpStatus.INTERNAL_SERVER_ERROR, "SLACK_001", "Slack 클라이언트가 초기화되지 않았습니다."
    ),
    MUSINSA_INVALID_RESPONSE(
        HttpStatus.BAD_REQUEST, "MUSINSA_001", "무신사 응답이 유효하지 않습니다."
    ),
    ZIGZAG_INVALID_RESPONSE(
        HttpStatus.BAD_REQUEST, "ZIGZAG_001", "지그재그 응답이 유효하지 않습니다."
    ),
    CM29_INVALID_RESPONSE(
        HttpStatus.BAD_REQUEST, "CM29_001", "29CM 응답이 유효하지 않습니다."
    ),
    ABLY_INVALID_RESPONSE(
        HttpStatus.BAD_REQUEST, "ABLY_001", "에이블리 응답이 유효하지 않습니다."
    ),
    CRAWLER_INVALID_RESPONSE(
        HttpStatus.BAD_REQUEST, "CRAWLER_002", "크롤러 응답이 유효하지 않습니다."
    ),
    CRAWLER_INVALID_URL(
        HttpStatus.BAD_REQUEST, "CRAWLER_003", "크롤러 URL이 유효하지 않습니다."
    ),

}