package org.team_alilm.error.exception

class NotParsingHtml : RuntimeException() {
    override val message: String
        get() = "HTML을 파싱할 수 없습니다."
}