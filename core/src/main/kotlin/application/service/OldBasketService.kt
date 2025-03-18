package org.team_alilm.application.service

import domain.Member
import domain.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.persistence.adapter.data.ProductAndBasket
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
                thumbnailUrl = productAndBasket.product.thumbnailUrl,
                createdDate = productAndBasket.basket.createdDate
            ),
            relatedProductList = relatedProductList.map {
                OldBasketUseCase.RelateProduct(
                    thumbnailUrl = it.thumbnailUrl
                )
            }
        )
    }
}