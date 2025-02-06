package org.team_alilm.error.code

/**
 * 무신사 크롤링 관련 에러 코드 정의
 */
enum class MusinsaErrorCode(val code: Int, val message: String) {
    /** 크롤링 요청이 실패했을 때 */
    CRAWLING_REQUEST_FAILED(1, "무신사 크롤링 요청에 실패했습니다."),

    /** HTML 파싱 오류 */
    HTML_PARSING_ERROR(2, "무신사 HTML 파싱 중 오류가 발생했습니다."),

    /** API 응답이 비어 있을 때 */
    EMPTY_RESPONSE(3, "무신사 API 응답이 비어 있습니다."),

    /** 알 수 없는 오류 */
    UNKNOWN_ERROR(4, "무신사 크롤링 중 알 수 없는 오류가 발생했습니다.")
}
