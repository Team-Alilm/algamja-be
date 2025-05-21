package org.team_alilm.domain.product

enum class ProductCategory(val description: String) {
    ALL("전체"),
    TOPS("상의"),
    OUTERWEAR("아우터"),
    PANTS("바지"),
    DRESSES_SKIRTS("원피스/스커트"),
    SHOES("신발"),
    BAGS("가방"),
    FASHION_ACCESSORIES("패션소품"),
    UNDERWEAR_HOMEWEAR("속옷/홈웨어"),
    SPORTS_LEISURE("스포츠/레저");

    override fun toString(): String = name // Swagger 문서에서 값으로 표시됨
}