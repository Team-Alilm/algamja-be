package org.teamalilm.alilm.adapter.out.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.teamalilm.alilm.application.port.out.gateway.SendMailGateway

@Service
class MailGateway(
    @Value("\${spring.mail.subject}") private val subject: String,
    @Value("\${spring.mail.from}") private val from: String,
    @Value("\${spring.mail.username}") private val emailId: String,

    private val emailSender: JavaMailSender,
) : SendMailGateway {

    override fun sendMail(message: String, to: String) {
        val mimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setFrom(emailId, from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(message, true)

        emailSender.send(mimeMessage)

    }

}