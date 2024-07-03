package org.teamalilm.alilmbe.service.product

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.controller.BasketController
import org.teamalilm.alilmbe.controller.BasketController.BasketFindAllResponse
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo

@Service
class BasketService(
    private val basketRepository: BasketRepository,
) {

    fun findAll(basketFindAllCommand: BasketFindAllCommand): Slice<BasketFindAllResponse> {
        val baskets =
            basketRepository.findDistinctByProductAndOldestCreationTimeWithCount(pageable = basketFindAllCommand.pageRequest)

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

    fun findMyBasket(basketFindBasketCommand: BasketFindBasketCommand): List<BasketController.BasketFindMyBasketResponse> {
        return basketRepository.findAllByMember(basketFindBasketCommand.member).map {
            BasketController.BasketFindMyBasketResponse(
                id = it.product.id!!,
                name = it.product.name,
                imageUrl = it.product.imageUrl,
                productInfo = ProductInfo(
                    store = it.product.productInfo.store,
                    number = it.product.productInfo.number,
                    option1 = it.product.productInfo.option1,
                    option2 = it.product.productInfo.option2,
                    option3 = it.product.productInfo.option3
                ),
                createdDate = it.createdDate,
            )
        }
    }

    data class BasketFindAllCommand(
        val pageRequest: PageRequest
    )

    data class BasketFindBasketCommand(
        val member: Member
    )


}
