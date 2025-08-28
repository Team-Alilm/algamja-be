package org.team_alilm.algamja.common.slack

import com.slack.api.Slack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode

@Component
class SlackClient(
    @Value("\${webhook.slack.url}")
    private val SLACK_NOTICE_CH_WEBHOOK_URL: String,
) {

    private val log: Logger = LoggerFactory.getLogger(SlackClient::class.java)

    private val slackClient: Slack = Slack.getInstance()
        ?: throw BusinessException(ErrorCode.SLACK_CLIENT_NOT_INITIALIZED)

    fun sendMessage(message: String) {
        val payload = """
            {
                "text": "$message"
            }
        """.trimIndent()

        slackClient.send(SLACK_NOTICE_CH_WEBHOOK_URL, payload)
    }
}