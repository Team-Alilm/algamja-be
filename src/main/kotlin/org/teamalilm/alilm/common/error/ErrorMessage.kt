package org.teamalilm.alilm.common.error

enum class ErrorMessage(
    val code: String,
    val message: String
) {
    BASKET_ALREADY_EXISTS("ALILM-002", "이미 등록된 상품입니다."),
    NOT_FOUND_MEMBER("ALILM-003", "회원을 찾지 못했습니다."),
    NOT_FOUND_ROLE("ALILM-004", " 권환을 찾지 못했습니다."),
    NOT_FOUND_PRODUCT("ALILM-005", "상품을 찾지 못했습니다."),
}