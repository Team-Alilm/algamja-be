package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.OldBasketUseCase
import org.team_alilm.application.port.out.LoadBasketPort
import org.team_alilm.application.port.out.LoadRelateProductPort

@Service
@Transactional(readOnly = true)
class OldBasketService(
    private val loadBasketPort: LoadBasketPort,
    private val loadRelateProductPort: LoadRelateProductPort
) : OldBasketUseCase {

    override fun loadOldBasket(command: OldBasketUseCase.OldBasketCommand): OldBasketUseCase.OldBasketResult {
        val productAndBasket = loadBasketPort.loadOldBasket(
            command.memberId
        )

        val relatedProductList = loadRelateProductPort.loadRelateProduct(
            productAndBasket.product.firstCategory,
            productAndBasket.product.secondCategory
        )

        return OldBasketUseCase.OldBasketResult(
            oldProductInfo = OldBasketUseCase.OldProductInfo(
                productId = productAndBasket.product.id!!.value,
                thumbnailUrl = productAndBasket.product.thumbnailUrl,
                brand = productAndBasket.product.brand,
                store = productAndBasket.product.store.name,
                price = productAndBasket.product.price,
                category = productAndBasket.product.firstCategory,
                createdDate = productAndBasket.basket.createdDate
            ),
            relatedProductList = relatedProductList.map {
                OldBasketUseCase.RelateProduct(
                    productId = it.id!!.value,
                    thumbnailUrl = it.thumbnailUrl,
                    brand = it.brand,
                    store = it.store.name,
                    price = it.price,
                    category = it.firstCategory
                )
            }
        )
    }
}