package org.team_alilm.global.error

enum class Error(
    val code: String,
    val message: String
) {
    BASKET_ALREADY_EXISTS("ALILM-002", "Basket already exists"),
    NOT_FOUND_MEMBER("ALILM-003", "회원을 찾지 못했습니다."),
    NOT_FOUND_ROLE("ALILM-004", " 권환을 찾지 못했습니다."),
    NOT_FOUND_PRODUCT("ALILM-005", "상품을 찾지 못했습니다."),
    NOT_FOUND_BASKET("ALILM-007", "장바구니를 찾지 못했습니다."),
    DUPLICATE_BASKET("ALILM-008", "장바구니에 이미 담긴 상품입니다."),
    DUPLICATE_FCM_TOKEN("ALILM-009", "이미 등록된 FCM 토큰입니다."),
    NOT_FOUND_STORE("ALILM-010", "지원하지 않는 스토어입니다."),
    NOT_FOUND_ALILM("ALILM-0011", "알림을 찾지 못했습니다."),
}