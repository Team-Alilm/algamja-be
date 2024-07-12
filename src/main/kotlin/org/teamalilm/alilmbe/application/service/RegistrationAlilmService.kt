package org.teamalilm.alilmbe.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.Basket
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Product
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductInfo
import org.teamalilm.alilmbe.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationUseCase
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository
import org.teamalilm.alilmbe.global.slack.service.SlackService

@Service
@Transactional(readOnly = true)
class AlilmRegistrationService(
    private val productRepository: ProductRepository,
    private val slackService: SlackService,
    private val basketRepository: BasketRepository
) : AlilmRegistrationUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun invoke(command: AlilmRegistrationCommand) {
        val productInfo = ProductInfo(
            store = command.store,
            number = command.number,
            option1 = command.option1,
            option2 = command.option2,
            option3 = command.option3
        )

        val product = findOrCreateProduct(command, productInfo)

        if (basketRepository.existsByMemberAndProduct(command.member, product)) {
            log.info("Product already registered: ${product.name}")
        } else {
            log.info("Registering product: ${product.name}")
            basketRepository.save(Basket(member = command.member, product = product))
        }

        notifySlack(command.member, product)
    }

}
