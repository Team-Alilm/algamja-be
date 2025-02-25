package org.team_alilm.application.service

import domain.Alilm
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.application.port.`in`.use_case.BasketAlilmUseCase
import org.team_alilm.application.port.out.*
import org.team_alilm.application.port.out.gateway.SendMailGateway
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional
class BasketAlilmService(
    private val sendMailGateway: SendMailGateway,
    private val loadMemberPort: LoadMemberPort,
    private val fcmSendGateway: FcmSendGateway,
    private val addBasketPort: AddBasketPort,
    private val loadProductPort: LoadProductPort,
    private val loadBasket: LoadBasketPort,
    private val addAlilmPort: AddAlilmPort,
    private val loadFcmTokenPort: LoadFcmTokenPort
): BasketAlilmUseCase {

    private val log = LoggerFactory.getLogger(BasketAlilmService::class.java)

    @Transactional
    override fun basketAlilm(command: BasketAlilmUseCase.BasketAlilmCommand) {
        val product = loadProductPort.loadProduct(command.productId) ?: throw NotFoundProductException()
        val basketList = loadBasket.loadBasketList(product.id!!)

        log.info("basketList: $basketList")
        log.info("basketList size: ${basketList.size}")

        basketList.forEach {
            // 회원 중복 없음
            val member = loadMemberPort.loadMember(it.memberId.value) ?: throw NotFoundProductException()

            sendMailGateway.sendMail(member, product)
            addAlilmPort.addAlilm(Alilm.from(basket = it))

            val fcmTokenList = loadFcmTokenPort.loadFcmTokenAllByMember(member.id!!.value)
            log.info("fcmToken: $fcmTokenList")
            log.info("fcmToken size: ${fcmTokenList.size}")
            fcmTokenList.forEach { token -> fcmSendGateway.sendFcmMessage(product= product, fcmToken = token,"ios") }
            it.sendAlilm()
            addBasketPort.addBasket(it, memberId = it.memberId, productId = product.id!!)
        }
    }
}