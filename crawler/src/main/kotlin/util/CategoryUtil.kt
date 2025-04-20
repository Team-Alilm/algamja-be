package org.team_alilm.util

import org.slf4j.LoggerFactory

class CategoryUtil {

    companion object {
        private val log = LoggerFactory.getLogger(CategoryUtil::class.java)

        private val categoryMap: Map<String, Regex> = mapOf(
            "상의" to Regex("반소매 티셔츠|긴소매 티셔츠|블라우스|셔츠|스웨트셔츠|슬리브리스|후디|피케 티셔츠|카라 티셔츠|폴로|칼라|반소매 셔츠|베스트|니트웨어|터틀넥|집업|카라니트"),
            "아우터" to Regex("재킷|점퍼|바람막이|레더 재킷|후드 집업|블루종|트렌치 코트|하프코트|블레이저|야상|기타 아우터|경량패딩|숏코트|나일론 재킷|플리스|바시티|데님 재킷|아노락|롱코트|숏패딩|롱패딩|무스탕|퍼 재킷|로브|레인코트|카디건"),
            "바지" to Regex("팬츠|데님 팬츠|슬랙스|쇼트팬츠|와이드팬츠|부츠컷 팬츠|스트레이트 팬츠|기타 팬츠|슬림 팬츠"),
            "원피스/스커트" to Regex("롱 원피스|미니 원피스|미디 원피스|데님 원피스|롱스커트|미니스커트|미디스커트|데님스커트"),
            "신발" to Regex("스니커즈|플랫 슈즈|로퍼|힐|펌프스|샌들|슬리퍼|부츠"),
            "가방" to Regex("숄더백|크로스백|백팩|토트백|에코백|캔버스백|보스턴백|파우치|웨이스트백|캐리어|여행가방|기타가방"),
            "패션소품" to Regex("가방 액세서리|신발 액세서리|주얼리|모자|헤어 액세서리|지갑|카드케이스|양말|기타 액세서리|벨트|아이웨어|스카프|목도리|시계|머플러|넥타이|장갑"),
            "속옷/홈웨어" to Regex("홈웨어|기타 홈웨어|언더웨어|내의|내복|기타 언더웨어|팬티|브라"),
            "스포츠/레저" to Regex("트레이닝 재킷|레깅스|트레이닝 팬츠|스웻|수용복|비키니|모노키니|레저")
        )

        fun getCategories(originalCategory: String): String {
            log.info("originalCategory: $originalCategory")
            return categoryMap.entries.firstOrNull {
                it.value.containsMatchIn(originalCategory)
            }?.key ?: originalCategory
        }
    }
}
