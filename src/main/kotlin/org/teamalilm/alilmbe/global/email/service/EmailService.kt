package org.teamalilm.alilmbe.global.email.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.global.email.data.EmailMessage

@Service
class EmailService(
    private val emailSender: JavaMailSender,
) {

    fun sendMail(emailMessage: EmailMessage, type: String) {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setFrom("
    }
}