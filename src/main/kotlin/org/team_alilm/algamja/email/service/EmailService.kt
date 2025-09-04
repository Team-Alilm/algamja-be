package org.team_alilm.algamja.email.service

import jakarta.mail.internet.MimeMessage
import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.team_alilm.algamja.product.entity.ProductRow
import java.io.UnsupportedEncodingException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendStockNotificationEmail(
        email: String,
        nickname: String,
        product: ProductRow
    ) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")
            
            helper.setTo(email)
            helper.setSubject("[알감자] ${product.name} 상품이 재입고되었습니다!")
            helper.setText(buildEmailContent(nickname, product), true)
            helper.setFrom("team.algamja@gmail.com", "알감자")
            
            mailSender.send(message)
            log.info("Email sent successfully to: {}", email)
        } catch (e: MessagingException) {
            log.error("Failed to create or configure email message for: {}", email, e)
            throw e
        } catch (e: UnsupportedEncodingException) {
            log.error("Encoding issue while sending email to: {}", email, e)
            throw e
        } catch (e: MailException) {
            log.error("Failed to send email to: {}", email, e)
            throw e
        }
    }

    private fun buildEmailContent(nickname: String, product: ProductRow): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val restockTime = LocalDateTime.now().format(formatter)
        
        return """
            <html>
                <body style="font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h1 style="color: #1B1A3B; font-size: 28px; margin-bottom: 30px;">Alilm</h1>
                        
                        <div style="background-color: #F3F3F3; padding: 40px; border-radius: 12px; margin-bottom: 30px;">
                            <h2 style="color: #333; font-size: 22px; margin-bottom: 10px;">
                                ${nickname}님이 등록하신 제품이
                            </h2>
                            <h2 style="color: #1B1A3B; font-size: 24px; font-weight: bold; margin-top: 0;">
                                재입고 되었습니다! 🎉
                            </h2>
                            
                            <div style="display: flex; align-items: center; gap: 20px; margin-top: 30px; background-color: white; padding: 20px; border-radius: 8px;">
                                <img src="${product.thumbnailUrl}" alt="${product.name}" style="width: 100px; height: 120px; object-fit: cover; border-radius: 8px;" />
                                <div>
                                    <p style="margin: 5px 0; font-size: 14px; color: #666;">
                                        <strong>브랜드:</strong> ${product.brand}
                                    </p>
                                    <p style="margin: 5px 0; font-size: 16px; color: #333; font-weight: 600;">
                                        ${product.name}
                                    </p>
                                    ${if (product.firstOption.isNotEmpty()) 
                                        "<p style='margin: 5px 0; font-size: 14px; color: #666;'><strong>옵션:</strong> ${product.firstOption}${product.secondOption?.let { " / $it" } ?: ""}${product.thirdOption?.let { " / $it" } ?: ""}</p>" 
                                    else ""}
                                    <p style="margin: 5px 0; font-size: 18px; color: #1B1A3B; font-weight: bold;">
                                        ${String.format("%,d", product.price.toInt())}원
                                    </p>
                                    <p style="margin: 10px 0 0 0; font-size: 13px; color: #999;">
                                        재입고 시각: ${restockTime}
                                    </p>
                                </div>
                            </div>
                        </div>
                        
                        <div style="background-color: #FFF8E7; padding: 20px; border-radius: 8px; border-left: 4px solid #FFB800; margin-bottom: 30px;">
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #333;">
                                <strong>${nickname}님</strong>이 등록하신 상품의 재입고 소식을 알려드리러 왔어요.
                            </p>
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #666;">
                                ⚠️ 상품은 재입고 시각으로부터 다시 품절이 될 수 있음을 유의해주세요!
                            </p>
                            <p style="margin: 0; font-size: 14px; color: #333;">
                                저희 알림 서비스를 이용해주셔서 감사합니다 😊
                            </p>
                        </div>
                        
                        <a href="${getProductUrl(product)}" 
                           style="display: block; width: 100%; padding: 18px; background-color: #1B1A3B; color: white; text-align: center; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 600; transition: background-color 0.3s;">
                            재입고 상품 구매하러 가기 👉
                        </a>
                        
                        <div style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #E0E0E0; text-align: center;">
                            <p style="font-size: 12px; color: #999; margin: 5px 0;">
                                이 메일은 발신 전용입니다. 문의사항은 고객센터를 이용해주세요.
                            </p>
                            <p style="font-size: 12px; color: #999; margin: 5px 0;">
                                © 2024 Alilm. All rights reserved.
                            </p>
                        </div>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }
    
    private fun getProductUrl(product: ProductRow): String {
        return when (product.store.name) {
            "MUSINSA" -> "https://www.musinsa.com/app/goods/${product.storeNumber}"
            "ABLY" -> "https://m.a-bly.com/goods/${product.storeNumber}"
            "ZIGZAG" -> "https://zigzag.kr/catalog/products/${product.storeNumber}"
            "29CM" -> "https://www.29cm.co.kr/product/${product.storeNumber}"
            else -> "#"
        }
    }
}