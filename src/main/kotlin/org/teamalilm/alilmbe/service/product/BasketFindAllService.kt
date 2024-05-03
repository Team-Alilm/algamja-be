package org.teamalilm.alilmbe.service.product

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.controller.BasketFindAllController.BasketFindAllResponse
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import java.util.Comparator

@Service
class BasketFindAllService(
    private val basketRepository: BasketRepository
) {

    fun findAll(basketFindAllCommand: BasketFindAllCommand): Slice<BasketFindAllResponse> {
        val baskets = basketRepository.findDistinctByProductAndOldestCreationTimeWithCount(pageable = basketFindAllCommand.pageRequest)

        return baskets.map {
            BasketFindAllResponse(
                id = it.getBasket().product.id!!,
                name = it.getBasket().product.name,
                imageUrl = it.getBasket().product.imageUrl,
                productInfo = ProductInfo(
                    store = it.getBasket().product.productInfo.store,
                    number = it.getBasket().product.productInfo.number,
                    option1 = it.getBasket().product.productInfo.option1,
                    option2 = it.getBasket().product.productInfo.option2,
                    option3 = it.getBasket().product.productInfo.option3
                ),
                createdDate = it.getBasket().createdDate,
                count = it.getCount()
            )
        }
    }

    data class BasketFindAllCommand(
        val pageRequest: PageRequest
    )
}
