package org.team_alilm.adapter.out.gateway

import com.slack.api.Slack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant

@Service
class SlackGateway(
    @Value("\${webhook.slack.url}")
    private val SLACK_NOTICE_CH_WEBHOOK_URL: String,
) : SendSlackGateway {

    private val log: Logger = LoggerFactory.getLogger(SlackGateway::class.java)

    private val slackClient: Slack = Slack.getInstance()
        ?: throw IllegalStateException("Slack client is not initialized")

    override fun sendMessage(message: String) {
        val payload = """
            {
                "text": "$message"
            }
        """.trimIndent()

        log.info("SLACK_WEBHOOK_URL : $SLACK_NOTICE_CH_WEBHOOK_URL")
        slackClient.send(SLACK_NOTICE_CH_WEBHOOK_URL, payload)
    }

    override fun sendMessage(product: Product) {
        slackClient.send(SLACK_NOTICE_CH_WEBHOOK_URL,
            """
            ${product.name} 상품이 재 입고 되었습니다.

            상품명: ${product.name}
            상품번호: ${product.number}
            상품 옵션1: ${product.firstOption}
            상품 옵션2: ${product.secondOption}
            상품 옵션3: ${product.thirdOption}
            상품 구매링크 : ${StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(product.number)}
            바구니에서 삭제되었습니다.
        """.trimIndent())
    }
}