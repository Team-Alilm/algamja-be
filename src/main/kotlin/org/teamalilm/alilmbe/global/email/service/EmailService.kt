package org.teamalilm.alilmbe.global.email.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.global.email.data.EmailMessage

@Service
class EmailService(
    private val emailSender: JavaMailSender,
) {

    fun sendMail(emailMessage: EmailMessage) {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setFrom(emailMessage.from)
        helper.setTo(emailMessage.to)
        helper.setSubject(emailMessage.subject)

        // HTML 형식의 이메일 본문
        val htmlContent = """
            <html>
            <body>
                <h2>안녕하세요 팀 알림(빨간색으로 바꾸고 싶어요!) 입니다.</h2>
                <p>${emailMessage.text}</p>
                <p>이쁘게 만들어 주실 수 있나요 ?</p>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)
        emailSender.send(message)
    }
}