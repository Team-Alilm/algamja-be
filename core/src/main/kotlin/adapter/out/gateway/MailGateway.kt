package org.team_alilm.adapter.out.gateway

import domain.product.Product
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.team_alilm.application.port.out.gateway.SendMailGateway
import java.lang.System.currentTimeMillis
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Service
class MailGateway(
    @Value("\${spring.mail.from}") private val from: String,
    @Value("\${spring.mail.username}") private val emailId: String,

    private val emailSender: JavaMailSender,
) : SendMailGateway {

    override fun sendMail(to: String, nickname: String, product: Product) {
        val mimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
        val subject = "[Alilm] 등록하신 [${product.name}]이 재입고 되었어요!"

        helper.setFrom(emailId, from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(
            getMailMessage(nickname, product.number, product.thumbnailUrl, product.firstOption, product.getStoreUrl()),
            true
        )

        emailSender.send(mimeMessage)
    }

    private fun getMailMessage(nickname: String, productNumber: Long, imageUrl: String, options: String?, storeLink: String): String {
        val currentTimeMillis = currentTimeMillis()

        val dateTime: LocalDateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.of("Asia/Seoul"))

        // 원하는 형식으로 포맷
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        // 포맷된 결과 출력
        val formattedDateTime = dateTime.format(formatter)

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>재입고 알림</title>
            
              <link href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css" rel="stylesheet">
            
              <style>
                  /* 전역 폰트 설정 */
                  body, table, p, h1, h2, h3, h4, a {
                      font-family: 'Pretendard', Arial, sans-serif;
                  }
                  /* a 태그의 밑줄 제거 */
                  a {
                      text-decoration: none;
                      color: white; /* 텍스트 색상을 변경하고 싶다면 추가 */
                  }
              </style>
            </head>
            <body>
            <div style="max-width: 560px; width: 580px; height: 474px;">
              <div style="width: 74px; height: 22px">
                <svg width="74" height="22" viewBox="0 0 74 22" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <g clip-path="url(#clip0_443_108)">
                    <path d="M13.6783 1.00781L20.3323 22.0007H13.5258L12.7962 19.2939H7.49257L6.7847 22.0007H0L6.65401 1.00781H13.6783ZM8.96277 13.8805H11.3804L10.1716 9.35879L8.96277 13.8805Z" fill="#1C1C1C"/>
                    <path d="M28.7724 1.00781V22.0007H22.2817V1.00781H28.7724Z" fill="#1C1C1C"/>
                    <path d="M31.6475 22V7.39628H38.1599V22H31.6475ZM34.9472 0C35.4373 0 35.8729 0.0734382 36.265 0.230806C36.657 0.388174 36.9946 0.608488 37.2778 0.881259C37.5609 1.16452 37.7787 1.48975 37.9203 1.85694C38.0619 2.23462 38.1381 2.63329 38.1381 3.07392C38.1381 3.51454 38.0619 3.89223 37.9094 4.25942C37.757 4.6371 37.5391 4.95184 37.2669 5.22461C36.9946 5.49738 36.657 5.71769 36.2541 5.87506C35.8511 6.04292 35.3937 6.11636 34.9037 6.11636C34.4136 6.11636 33.978 6.03243 33.5968 5.87506C33.2157 5.71769 32.8781 5.49738 32.5949 5.22461C32.3118 4.95184 32.094 4.62661 31.9524 4.25942C31.8108 3.88174 31.7346 3.49356 31.7346 3.07392C31.7346 2.65427 31.8108 2.23462 31.9633 1.85694C32.1157 1.47926 32.3336 1.15403 32.6058 0.881259C32.8781 0.597997 33.2157 0.388174 33.6186 0.230806C34.0216 0.0734382 34.4681 0 34.9472 0Z" fill="#1C1C1C"/>
                    <path d="M47.558 1.00781V22.0007H41.0674V1.00781H47.558Z" fill="#1C1C1C"/>
                    <path d="M55.5734 6.15921L56.2268 6.99851C56.8585 6.51591 57.501 6.20118 58.1544 6.0543C58.8078 5.90742 59.3415 5.83398 59.7444 5.83398C60.6156 5.83398 61.4215 5.98086 62.1621 6.26412C62.9026 6.54738 63.5342 7.02998 64.0461 7.70141C64.4381 7.28177 64.8302 6.94605 65.244 6.69426C65.6579 6.44247 66.0608 6.25363 66.442 6.13823C66.8231 6.02283 67.1825 5.9284 67.5092 5.89693C67.8359 5.86546 68.0973 5.83398 68.3042 5.83398C69.0992 5.83398 69.8398 5.94939 70.5367 6.19068C71.2337 6.43198 71.8327 6.82016 72.3445 7.3657C72.8564 7.91124 73.2593 8.62464 73.5534 9.49541C73.8474 10.3767 73.9999 11.4468 73.9999 12.7057V21.9904H67.5855V13.1463C67.5855 12.6532 67.4874 12.2441 67.2914 11.9294C67.0954 11.6146 66.7687 11.4677 66.3222 11.4677C65.9846 11.4677 65.6796 11.5727 65.3965 11.793C65.4074 11.9503 65.4183 12.0972 65.4183 12.2441V21.9799H59.0039V13.1358C59.0039 12.6428 58.9058 12.2336 58.7098 11.9189C58.5138 11.6041 58.1871 11.4573 57.7406 11.4573C57.5445 11.4573 57.3594 11.4887 57.1743 11.5622C56.9891 11.6356 56.804 11.772 56.6298 11.9713V21.9799H50.2153V6.15921H55.5625H55.5734Z" fill="#1C1C1C"/>
                  </g>
                  <defs>
                    <clipPath id="clip0_443_108">
                      <rect width="74" height="22" fill="white"/>
                    </clipPath>
                  </defs>
                </svg>
              </div>
              <div style="width: 100%; height: 28px;"></div>
              <div
                style="
                  width: 100%;
                  height: 252px;
                  border-radius: 8px;
                  background-color: #F3F3F3;
                  padding: 32px;
                  box-sizing: border-box;
                "
              >
                <div style="height: 68px; font-weight: 900; font-size: 24px; line-height: 34px; letter-spacing: -0.03em">
                  ${nickname}님이 등록하신 제품이
                  <br/>
                  재입고 되었습니다!
                </div>
                <div style="width: 100%; height: 20px;"></div>
                <div style="width: 100%; height: 20px;"></div>
                <div style="display: flex; flex-direction: row">
                  <img style="object-fit: cover; height: 80px; width: 68px" src="$imageUrl" alt="product-image">
                  <div style="width: 12px;"></div>
                  <div>
                      <div style="height: 28px; display: flex; align-items: center; font-weight: 500; font-size: 12px;">상품 옵션 : ${options}</div>
                      <div style="height: 28px; display: flex; align-items: center; font-weight: 500; font-size: 12px;">재입고 시간 : ${formattedDateTime}</div>
                  </div>
                </div>
              </div>
              <div style="width: 100%; height: 28px;"></div>
              <div style="font-weight: normal; font-size: 14px; line-height: 24px; letter-spacing: -0.03em">
                ${nickname}님이 등록하신 상품의 재입고 소식을 알려드리러 왔어요.
                <br/>
                상품은 재입고 시각으로부터 다시 품절이 될 수 있음을 유의해주세요!
                <br/>
                저희 알림 서비스를 이용해주셔서 감사합니다 :)
              </div>
              <div style="width: 100%; height: 28px;"></div>
              <div style="color= #ffffff; display: flex; justify-content: center; align-items: center; width: 100%; height: 44px; background-color: #1B1A3B; border-radius: 8px;" >
                <a href="https://www.musinsa.com/app/goods/${productNumber}" 
                   style="background-color: #1B1A3B; color: #ffffff; text-decoration: none;">
                   재입고 상품 구매하러 가기 👉
                </a>

              </div>
            </div>
            </body>
            </html>
        """.trimIndent()
    }

}