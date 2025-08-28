package org.team_alilm.product.crawler.util

object CategoryMapper {
    
    private val categoryMappings = mapOf(
        // 상의
        "상의" to listOf(
            "티셔츠", "반팔티", "반팔티셔츠", "긴팔티", "긴팔티셔츠", "맨투맨", "스웨트셔츠", "셔츠", "블라우스", 
            "니트", "스웨터", "카디건", "조끼", "베스트", "탱크탑", "민소매", "캐미솔", "크롭탑", "튜닉"
        ),
        
        // 아우터
        "아우터" to listOf(
            "자켓", "코트", "점퍼", "패딩", "다운", "후드집업", "후드", "가디건", "블레이저", "트렌치코트",
            "바람막이", "야상", "무스탕", "레더자켓", "덤블코트", "롱코트", "숏코트", "아우터"
        ),
        
        // 바지
        "바지" to listOf(
            "바지", "팬츠", "진", "청바지", "데님", "슬랙스", "치노", "조거팬츠", "와이드팬츠", "스키니",
            "부트컷", "스트레이트", "반바지", "숏팬츠", "하프팬츠", "크롭팬츠", "레깅스", "트레이닝팬츠"
        ),
        
        // 원피스/스커트
        "원피스/스커트" to listOf(
            "원피스", "드레스", "스커트", "미니스커트", "맥시스커트", "미디스커트", "A라인스커트", "플리츠스커트"
        ),
        
        // 스포츠/레저
        "스포츠/레저" to listOf(
            "운동복", "트레이닝", "스포츠웨어", "요가복", "피트니스", "골프웨어", "수영복", "보드복",
            "등산복", "아웃도어", "레저", "스포츠", "헬스", "러닝", "조깅"
        ),
        
        // 신발
        "신발" to listOf(
            "신발", "운동화", "스니커즈", "구두", "부츠", "샌들", "슬리퍼", "로퍼", "하이힐", "플랫슈즈",
            "슬립온", "워커", "첼시부츠", "앵클부츠", "롱부츠", "컨버스", "플립플롭"
        ),
        
        // 가방
        "가방" to listOf(
            "가방", "백팩", "크로스백", "토트백", "숄더백", "클러치", "파우치", "지갑", "벨트백", "힙색",
            "메신저백", "보스턴백", "더플백", "캐리어", "배낭", "백"
        ),
        
        // 속옷/홈웨어
        "속옷/홈웨어" to listOf(
            "속옷", "언더웨어", "브라", "팬티", "홈웨어", "잠옷", "파자마", "가운", "슬립", "런닝셔츠",
            "속바지", "내의", "보정속옷", "란제리", "잠바지"
        ),
        
        // 패션소품
        "패션소품" to listOf(
            "액세서리", "모자", "캡", "비니", "버켓햇", "베레모", "목걸이", "귀걸이", "반지", "팔찌",
            "시계", "선글라스", "안경", "벨트", "스카프", "머플러", "장갑", "양말", "스타킹", "타이츠",
            "헤어액세서리", "브로치", "넥타이", "보타이"
        ),
        
        // 뷰티
        "뷰티" to listOf(
            "화장품", "스킨케어", "메이크업", "향수", "헤어케어", "바디케어", "네일", "미용도구",
            "파운데이션", "립스틱", "아이섀도우", "마스카라", "클렌징", "토너", "세럼", "크림", "오일"
        ),
        
        // 디지털
        "디지털" to listOf(
            "디지털", "전자제품", "스마트폰", "태블릿", "노트북", "이어폰", "헤드폰", "스피커", "케이스",
            "충전기", "케이블", "액세서리", "가전", "IT", "테크"
        )
    )
    
    fun mapCategory(originalCategory: String?): String {
        if (originalCategory.isNullOrBlank()) return "기타"
        
        val normalizedCategory = originalCategory.lowercase().replace(" ", "")
        
        // 직접 매칭 시도
        for ((targetCategory, keywords) in categoryMappings) {
            if (keywords.any { keyword -> 
                normalizedCategory.contains(keyword.lowercase()) || 
                keyword.lowercase().contains(normalizedCategory)
            }) {
                return targetCategory
            }
        }
        
        // 부분 매칭 시도 (더 유연한 매칭)
        for ((targetCategory, keywords) in categoryMappings) {
            if (keywords.any { keyword -> 
                val normalizedKeyword = keyword.lowercase().replace(" ", "")
                normalizedCategory.contains(normalizedKeyword) || 
                normalizedKeyword.contains(normalizedCategory) ||
                isSimilarCategory(normalizedCategory, normalizedKeyword)
            }) {
                return targetCategory
            }
        }
        
        return "기타"
    }
    
    private fun isSimilarCategory(category1: String, category2: String): Boolean {
        // 간단한 유사도 체크 (편집 거리 기반)
        if (category1.length < 2 || category2.length < 2) return false
        
        val shorter = if (category1.length < category2.length) category1 else category2
        val longer = if (category1.length >= category2.length) category1 else category2
        
        return longer.contains(shorter) || 
               (shorter.length >= 3 && longer.contains(shorter.substring(0, 3)))
    }
}