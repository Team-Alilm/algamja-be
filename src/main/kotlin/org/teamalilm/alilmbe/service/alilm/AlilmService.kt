package org.teamalilm.alilmbe.service.alilm

import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo.Store
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository
import org.teamalilm.alilmbe.global.slack.service.SlackService

@Service
@Transactional(readOnly = true)
class AlilmService(
    private val productRepository: ProductRepository,
    private val slackService: SlackService,
    private val basketRepository: org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registration(
        alilmRegistrationCommand: AlilmRegistrationCommand
    ) {
        val productInfo = ProductInfo(
            store = alilmRegistrationCommand.store,
            number = alilmRegistrationCommand.number,
            option1 = alilmRegistrationCommand.option1,
            option2 = alilmRegistrationCommand.option2,
            option3 = alilmRegistrationCommand.option3
        )

        val product = productRepository.findByProductInfo(productInfo)
            ?: productRepository.save(
                Product(
                    name = alilmRegistrationCommand.name,
                    brand = alilmRegistrationCommand.brand,
                    imageUrl = alilmRegistrationCommand.imageUrl,
                    category = alilmRegistrationCommand.category,
                    price = alilmRegistrationCommand.price,
                    productInfo = productInfo
                )
            )

        log.info("$product")

        if (
            basketRepository.existsByMemberAndProduct(
                member =alilmRegistrationCommand.member,
                product = product
            )
        ) {
            log.info("이미 등록된 상품입니다.")
        } else {
            log.info("상품을 등록합니다.")

            basketRepository.save(
                Basket(
                    member = alilmRegistrationCommand.member,
                    product = product
                )
            )
        }

        slackService.sendSlackMessage(
            """
                id: ${alilmRegistrationCommand.member.id}
                nickname : ${alilmRegistrationCommand.member.nickname} 님이 상품을 등록했어요. 
                상품 : $product
                """.trimIndent()
        )

    }

    data class AlilmRegistrationCommand(
        val number: Number,
        val name: String,
        val brand: String,
        val store: Store,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val option1: String,
        val option2: String?,
        val option3: String?,
        val member: Member
    )

}

