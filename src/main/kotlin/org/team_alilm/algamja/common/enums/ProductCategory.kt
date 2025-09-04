package org.team_alilm.algamja.common.enums

enum class ProductCategory(val englishKey: String, val koreanNames: List<String>) {
    SPORTS_LEISURE("SPORTS_LEISURE", listOf("스포츠/레저", "스포츠", "레저", "운동", "아웃도어")),
    TOPS("TOPS", listOf("상의", "티셔츠", "셔츠", "블라우스", "탑", "후드티", "맨투맨", "니트", "스웨터")),
    SHOES("SHOES", listOf("신발", "운동화", "구두", "샌들", "부츠", "슬리퍼", "스니커즈")),
    OUTERWEAR("OUTERWEAR", listOf("아우터", "자켓", "코트", "점퍼", "패딩", "가디건", "조끼", "바람막이")),
    BAGS("BAGS", listOf("가방", "백팩", "토트백", "크로스백", "클러치", "지갑", "숄더백")),
    PANTS("PANTS", listOf("바지", "청바지", "팬츠", "레깅스", "조거팬츠", "슬랙스", "반바지", "데님")),
    UNDERWEAR_HOMEWEAR("UNDERWEAR_HOMEWEAR", listOf("속옷/홈웨어", "속옷", "홈웨어", "잠옷", "언더웨어", "이너웨어")),
    DRESSES_SKIRTS("DRESSES_SKIRTS", listOf("원피스/스커트", "원피스", "스커트", "드레스", "치마")),
    FASHION_ACCESSORIES("FASHION_ACCESSORIES", listOf("패션소품", "액세서리", "모자", "벨트", "스카프", "주얼리", "시계", "장갑", "머플러")),
    BEAUTY("BEAUTY", listOf("뷰티", "화장품", "스킨케어", "메이크업", "향수")),
    DIGITAL("DIGITAL", listOf("디지털/라이프", "디지털", "라이프", "전자제품", "가전", "테크"));

    companion object {
        /**
         * 한국어 카테고리명을 영어 키로 변환
         */
        fun mapKoreanToEnglish(koreanCategory: String?): String? {
            if (koreanCategory.isNullOrBlank()) return null
            
            val normalized = koreanCategory.trim()
            
            // 각 enum의 한국어 리스트에서 매칭되는 것 찾기
            return values().firstOrNull { category ->
                category.koreanNames.any { korean ->
                    normalized.contains(korean, ignoreCase = true) ||
                    korean.contains(normalized, ignoreCase = true)
                }
            }?.englishKey
        }
        
        /**
         * 영어 키로 enum 찾기
         */
        fun fromEnglishKey(key: String?): ProductCategory? {
            return values().find { it.englishKey == key }
        }
        
        /**
         * 영어 키를 대표 한국어명으로 변환
         */
        fun mapEnglishToKorean(englishKey: String?): String? {
            if (englishKey.isNullOrBlank()) return null
            return fromEnglishKey(englishKey)?.koreanNames?.firstOrNull()
        }
    }
}