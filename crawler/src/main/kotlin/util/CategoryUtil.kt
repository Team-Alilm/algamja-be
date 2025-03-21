package org.team_alilm.util

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory

class CategoryUtil {

    companion object {
        private val log = LoggerFactory.getLogger(CategoryUtil::class.java)

        private val categoryMap: Map<String, Regex> = mapOf(
            "상의" to Regex("티셔츠|긴소매 티셔츠|반소매 티셔츠|셔츠|블라우스|니트|스웨터|후드|슬리브리스"),
            "하의" to Regex("팬츠|바지|데님팬츠|일자팬츠|슬랙스|슬랙스팬츠|와이드팬츠|스키니팬츠|부츠컷팬츠|조거팬츠|숏팬츠|점프수트"),
            "원피스/스커트" to Regex("스커트|미니스커트|미디스커트|롱스커트|치마|원피스|미니원피스|미디원피스|롱원피스"),
            "속옷/홈웨어" to Regex("브라|팬티|속옷|파자마|홈웨어|잠옷|슬립|속바지"),
            "스포츠/레저" to Regex("트레이닝|트레이닝 상의|트레이닝 하의|레깅스|수영복|비키니|비치웨어|래쉬가드|비치원피스"),
            "가방" to Regex("가방|크로스백|숄더백|토트백|미니백|백팩|에코백|캔버스팩|캐리어"),
            "신발" to Regex("슈즈|신발|힐|플랫|로퍼|부츠|워커|스니커즈|운동화|샌들|슬리퍼|쪼리"),
            "패션소품" to Regex("지갑|클러치|파우치|주얼리|귀걸이|목걸이|반지|팔찌|발찌|시계|브로치|패션잡화|양말|모자|캡모자|타이즈|스타킹|스카프|머플러|목도리|네일 스티커|벨트|아이웨어|안경|장갑|액세서리"),
            "뷰티" to Regex("뷰티"),
            "라이프/푸드" to Regex("라이프/푸드")
        )

        fun getCategories(scriptJsonNode: JsonNode): String {
            val text = scriptJsonNode.asText()
            log.info("text: $text")

            return categoryMap.entries.firstOrNull { it.value.containsMatchIn(text) }?.key
                ?: scriptJsonNode.get(3).get("value").asText()
        }
    }
}