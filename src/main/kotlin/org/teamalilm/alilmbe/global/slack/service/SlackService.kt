package org.teamalilm.alilmbe.global.slack.service

import com.slack.api.Slack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SlackService(
    @Value("\${webhook.slack.url}")
    private val SLACK_WEBHOOK_URL: String,
) {

    private val log: Logger = LoggerFactory.getLogger(SlackService::class.java)

    private val slackClient: Slack = Slack.getInstance()
        ?: throw IllegalStateException("Slack client is not initialized")

    fun sendSlackMessage(message: String) {
        val payload = """
            {
                "text": "$message"
            }
        """.trimIndent()

        log.info("SLACK_WEBHOOK_URL : $SLACK_WEBHOOK_URL")
        slackClient.send(SLACK_WEBHOOK_URL, payload)
    }
}