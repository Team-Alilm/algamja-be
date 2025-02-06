package org.team_alilm.error

/**
 * 에러 코드 정의
 */
enum class ErrorCode(val code: String, val message: String) {
    CM29_API_ERROR("CM29-001", "29CM API 요청 중 오류가 발생했습니다."),
    CM29_HTML_PARSING_ERROR("CM29-002", "29CM HTML 파싱 중 오류가 발생했습니다."),
    CM29_INVALID_RESPONSE("CM29-003", "29CM 응답 데이터가 유효하지 않습니다."),
    CM29_PRODUCT_NOT_FOUND("CM29-004", "상품 정보를 찾을 수 없습니다."),

    MUSINSA_API_ERROR("MUSINSA-001", "무신사 API 요청 중 오류가 발생했습니다."),
    MUSINSA_URL_PARSING_ERROR("MUSINSA-002", "무신사 URL 파싱 중 오류가 발생했습니다."),
    MUSINSA_INVALID_RESPONSE("MUSINSA-003", "무신사 응답 데이터가 유효하지 않습니다."),
    MUSINSA_PRODUCT_NOT_FOUND("MUSINSA-004", "상품 정보를 찾을 수 없습니다."),

    ZIGZAG_API_ERROR("ZIGZAG-001", "지그재그 API 요청 중 오류가 발생했습니다."),
    ZIGZAG_HTML_PARSING_ERROR("ZIGZAG-002", "지그재그 HTML 파싱 중 오류가 발생했습니다."),
    ZIGZAG_INVALID_RESPONSE("ZIGZAG-003", "지그재그 응답 데이터가 유효하지 않습니다."),
    ZIGZAG_PRODUCT_NOT_FOUND("ZIGZAG-004", "상품 정보를 찾을 수 없습니다."),
}
