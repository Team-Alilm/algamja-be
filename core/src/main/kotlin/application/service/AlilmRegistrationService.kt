package org.team_alilm.application.service

import domain.Basket
import domain.Member
import domain.product.Product
import domain.product.ProductImage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase.*
import org.team_alilm.global.error.BasketAlreadyExistsException

@Service
@Transactional(readOnly = true)
class AlilmRegistrationService(
    private val loadProductPort: org.team_alilm.application.port.out.LoadProductPort,
    private val addProductPort: org.team_alilm.application.port.out.AddProductPort,
    private val loadBasketPort: org.team_alilm.application.port.out.LoadBasketPort,
    private val addBasketPort: org.team_alilm.application.port.out.AddBasketPort,
    private val slackGateway: SlackGateway,
    private val addProductImagePort: org.team_alilm.application.port.out.AddProductImagePort
) : org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun alilmRegistration(command: AlilmRegistrationCommand) {
        val product = createAndSaveProductWithImages(command)
        saveBasket(
            memberId = command.member.id!!,
            productId = product.id!!
        )

        slackGateway.sendMessage(
            message = """
                |알림 등록 완료
                |회원: ${command.member.nickname}
                |상품명: ${product.name}
                |링크: ${product.getStoreUrl()}
            """.trimIndent()
        )
    }

    private fun saveBasket(
        memberId: Member.MemberId,
        productId: Long,
    ) {

        val basket = loadBasketPort.loadBasketIncludeIsDelete(
            memberId = memberId,
            productId = productId
        ) ?.let {
            if(it.isReRegisterable().not()) throw BasketAlreadyExistsException()

            it
        } ?: run {
            Basket(
                id = Basket.BasketId(null),
                memberId = memberId,
                productId = productId,
                isHidden = false,
            )
        }

        addBasketPort.addBasket(
            basket = basket,
            memberId = memberId,
            productId = productId
        )
    }

    private fun createAndSaveProductWithImages(command: AlilmRegistrationCommand) : Product =
        loadProductPort.loadProduct(
            number = command.number,
            store = command.store,
            firstOption = command.firstOption,
            secondOption = command.secondOption,
            thirdOption = command.thirdOption
        ) ?: run {
            val product = addProductPort.addProduct(
                Product(
                    id = null,
                    number = command.number,
                    name = command.name,
                    brand = command.brand,
                    store = command.store,
                    thumbnailUrl = command.thumbnailUrl,
                    firstCategory = command.firstCategory,
                    secondCategory = command.secondCategory,
                    price = command.price,
                    firstOption = command.firstOption,
                    secondOption = command.secondOption,
                    thirdOption = command.thirdOption
                )
            )

            try{
                addProductImagePort.add(
                    command.imageUrlList.map { ProductImage(
                        id = null,
                        imageUrl = it,
                        productNumber = product.number,
                        productStore = product.store
                    ) }
                )
            } catch (e: Exception) {
                log.info("상품 이미지 등록 중 오류 발생", e)
            }

            return product
        }
}
