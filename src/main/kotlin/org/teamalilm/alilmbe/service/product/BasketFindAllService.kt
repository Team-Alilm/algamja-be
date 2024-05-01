package org.teamalilm.alilmbe.service.product

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.controller.BasketFindAllController.BasketFindAllResponse
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo

@Service
class BasketFindAllService(
    private val basketRepository: BasketRepository
) {

    private val log = getLogger(this::class.java)

    fun findAll(basketFindAllCommand: BasketFindAllCommand): Slice<BasketFindAllResponse> {
        log.info("findAll service called")
        return basketRepository.findAll(pageRequest = basketFindAllCommand.pageRequest)
            .map {
                BasketFindAllResponse(
                    id = it.product.id!!,
                    name = it.product.name,
                    imageUrl = it.product.imageUrl,
                    productInfo = ProductInfo(
                        store = it.product.productInfo.store,
                        number = it.product.productInfo.number,
                        option1 = it.product.productInfo.option1,
                        option2 = it.product.productInfo.option2,
                        option3 = it.product.productInfo.option3
                    )
                )
            }
    }

    data class BasketFindAllCommand(
        val pageRequest: PageRequest
    )

}