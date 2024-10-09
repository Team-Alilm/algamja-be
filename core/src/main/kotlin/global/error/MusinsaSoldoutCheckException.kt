package org.team_alilm.global.error

class MusinsaSoldoutCheckException : RuntimeException() {
    override val message: String
        get() = "무신사 품절 체크 API 호출에 실패했습니다."
}