package org.team_alilm.global.error

class NotParserProduct : RuntimeException() {
    override val message: String
        get() = "상품 정보를 추출할 수 없습니다."
}