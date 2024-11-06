package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase.*
import org.team_alilm.domain.Basket
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.ProductV2
import org.team_alilm.global.error.BasketAlreadyExistsException
import org.team_alilm.global.util.StringConstant

@Service
@Transactional(readOnly = true)
class AlilmRegistrationService(
    private val loadProductPort: org.team_alilm.application.port.out.LoadProductPort,
    private val addProductPort: org.team_alilm.application.port.out.AddProductPort,
    private val loadBasketPort: org.team_alilm.application.port.out.LoadBasketPort,
    private val addBasketPort: org.team_alilm.application.port.out.AddBasketPort,
    private val slackGateway: SlackGateway
) : org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun alilmRegistration(command: AlilmRegistrationCommand) {
        val product = getProduct(command)
        saveBasket(command, product)
    }

    override fun alilmRegistrationV2(command: AlilmRegistrationCommandV2) {
        val product = getProductV2(command)
        saveBasketV2(command, productV2Id = product.id!!)
    }

    private fun saveBasket(
        command: AlilmRegistrationCommand,
        product: Product,
    ) {

        val basket = loadBasketPort.loadBasketIncludeIsDelete(
            memberId = command.member.id!!,
            productId = product.id!!
        ) ?.let {
            if(it.isReRegisterable().not()) throw BasketAlreadyExistsException()

            it
        } ?: run {
            Basket(
                id = Basket.BasketId(null),
                memberId = command.member.id,
                productId = product.id,
                isHidden = false,
            )
        }

        addBasketPort.addBasket(
            basket = basket,
            memberId = command.member.id,
            productId = product.id
        )

        slackGateway.sendMessage(
            message = """
                |알림 등록 완료
                |회원: ${command.member.nickname}
                |상품명: ${product.name}
                |링크: ${StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(product.number)}
            """.trimIndent()
        )
    }

    private fun saveBasketV2(
        command: AlilmRegistrationCommandV2,
        productV2Id: ProductId,
    ) {

        val basket = loadBasketPort.loadBasketIncludeIsDelete(
            memberId = command.member.id!!,
            productId = productV2Id
        ) ?.let {

            if(it.isReRegisterable().not()) {
                log.info("장바구니가 이미 존재합니다. memberId: ${command.member.id}, productId: ${productV2Id.value}")
                throw BasketAlreadyExistsException()
            }

            it
        } ?: run {
            log.info("장바구니를 등록 합니다.")
            Basket(
                id = Basket.BasketId(null),
                memberId = command.member.id,
                productId = productV2Id,
                isHidden = false,
            )
        }

        addBasketPort.addBasket(
            basket = basket,
            memberId = command.member.id,
            productId = productV2Id
        )
    }

    private fun getProductV2(command: AlilmRegistrationCommandV2) : ProductV2 =
        loadProductPort.loadProductV2(
            number = command.number,
            store = command.store,
            firstOption = command.firstOption,
            secondOption = command.secondOption,
            thirdOption = command.thirdOption
        ) ?: run {
            addProductPort.addProduct(
                ProductV2(
                    id = null,
                    number = command.number,
                    name = command.name,
                    brand = command.brand,
                    store = command.store,
                    category = command.category,
                    price = command.price,
                    firstOption = command.firstOption,
                    secondOption = command.secondOption,
                    thirdOption = command.thirdOption
                )
            )
        }


    private fun getProduct(command: AlilmRegistrationCommand) =
        loadProductPort.loadProduct(
            number = command.number,
            store = command.store,
            firstOption = command.firstOption,
            secondOption = command.secondOption,
            thirdOption = command.thirdOption
        ) ?: run {
            addProductPort.addProduct(
                Product(
                    id = null,
                    number = command.number,
                    name = command.name,
                    brand = command.brand,
                    store = command.store,
                    imageUrl = command.imageUrl,
                    category = command.category,
                    price = command.price,
                    firstOption = command.firstOption,
                    secondOption = command.secondOption,
                    thirdOption = command.thirdOption
                )
            )
        }

}
