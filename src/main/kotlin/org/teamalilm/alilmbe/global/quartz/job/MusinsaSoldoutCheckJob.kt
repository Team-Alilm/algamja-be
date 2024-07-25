package org.teamalilm.alilmbe.domain.tracer

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.BasketJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.repository.basket.BasketRepository
import org.teamalilm.alilmbe.global.email.service.EmailService
import org.teamalilm.alilmbe.global.slack.service.SlackService

/**
 *  재고가 없는 상품을 체크하는 Job
 *  재고가 있다면 사용자에게 메세지를 보내고 해당 바구니를 삭제한다.
 **/
@Component
@Transactional(readOnly = true)
class MusinsaSoldoutCheckJob(
    val basketRepository: BasketRepository,
    val emailService: EmailService,
    val slackService: SlackService,
) : Job {

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val baskets = basketRepository.findAll()
        val restClient = RestClient.create()
        val passList = ArrayList<Long>()

//        baskets.forEach {
//            if (passList.contains(it.product.id!!)) {
//                return@forEach
//            }
//
//            val requestUri = MUSINSA_API_URL_TEMPLATE.format(it.product.productInfo.number)
//
//            val response = restClient.get()
//                .uri(requestUri)
//                .retrieve()
//                .body<SoldoutCheckResponse>()
//
//            val isSoldOut =
//                response?.data?.basic?.firstOrNull { item -> item.name == it.product.productInfo.option1 }
//                    ?.run {
//                        if (subOptions.isNotEmpty()) {
//                            subOptions.find { subOption -> subOption.name == it.product.productInfo.option2 }
//                                ?.run {
//                                    isSoldOut
//                                }
//                                ?: throw IllegalStateException("상품 옵션2을 찾지 못했어요. 상품번호: ${it.product.productInfo.number} 옵션1: ${it.product.productInfo.option1}")
//                        } else {
//                            isSoldOut
//                        }
//                    }
//                    ?: throw IllegalStateException("상품 옵션1을 찾지 못했어요. 상품번호: ${it.product.productInfo.number} 옵션1: ${it.product.productInfo.option2}")
//
//            if (!isSoldOut) {
//                passList.add(it.product.id)
//
//                basketRepository.findAllByProductId(it.product.id).forEach {
//                    emailService.sendMail(getEmailMessage(it), it.member.email)
//                    slackService.sendSlackMessage(getSlackMessage(it))
//                    basketRepository.delete(it)
//                }
//            }
//        }
    }

    private fun getEmailMessage(basket: BasketJpaEntity): String {
        return """
            <html>
    <body>
        <h1>Alilm</h1>
        <div style="width:580px; height:252px; background-color: #F3F3F3; display: flex; flex-direction: column; gap: 40px;">
            <div style="display: flex; flex-direction: column;">
                <h2>${basket.member.nickname}님이 등록하신 제품이</h2>
                <h2>재입고 되었습니다!</h2>
            </div>
            <div style="display: flex; gap: 12px;">
                <img src="${basket.product.imageUrl}" width="68px" height="80px" />
                <div>
                    <p>상품 옵션 : ${basket.product.name}//</p>
                    <p>재입고 시각 : ${basket.product.createdBy}</p>
                </div>
            </div>
        </div>
        <div>
            <p>${basket.member.nickname}님이 등록하신 상품의 재입고 소식을 알려드리러 왔어요.</p>
            <p>상품은 재입고 시각으로 부터 다시 품절이 될 수 있음을 유의해주세요!</p>
            <p>저희 알림 서비스를 이용해주셔서 감사합니다 :) </p>
        </div>
        <button style="width: 580px; height: 252px; background-color: #1B1A3B;">
            <h2 style="color: white;">재입고 상품 구매하러 가기 👉</h2>
        </button>
    </body>
</html>

        """.trimIndent()
    }

    private fun getSlackMessage(basket: BasketJpaEntity): String {
        return """
            ${basket.product.name} 상품이 재 입고 되었습니다.
            바구니에서 삭제되었습니다.
        """.trimIndent()
    }

    companion object {
        const val MUSINSA_API_URL_TEMPLATE =
            "https://goods-detail.musinsa.com/goods/%s/options?goodsSaleType=SALE"
    }
}