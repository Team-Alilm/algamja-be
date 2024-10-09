package org.team_alilm.adapter.out.gateway

import com.slack.api.Slack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.teamalilm.alilm.application.port.out.gateway.SendSlackGateway

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
}