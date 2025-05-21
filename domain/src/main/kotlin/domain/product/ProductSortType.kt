package org.team_alilm.domain.product

enum class ProductSortType(val description: String) {
    WAITING_COUNT("함께 기다리는 사람이 많은 순"),
    LATEST("최신 등록순"),
    PRICE_ASC("낮은 가격 순"),
    PRICE_DESC("높은 가격 순");

    override fun toString(): String = name // Swagger 문서에서 값으로 표시됨
}