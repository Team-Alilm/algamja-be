package org.team_alilm.error

class NotFoundProductNumber : RuntimeException() {
    override val message: String
        get() = "상품 번호를 찾을 수 없습니다."
}