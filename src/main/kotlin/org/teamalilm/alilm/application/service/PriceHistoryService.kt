package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.PriceHistoryUseCase
import org.teamalilm.alilm.application.port.out.LoadPricePort
import org.teamalilm.alilm.common.error.ErrorMessage
import org.teamalilm.alilm.common.error.NotFoundPriceException
import org.teamalilm.alilm.domain.Product

@Service
@Transactional(readOnly = true)
class PriceHistoryService(
    val loadPricePort: LoadPricePort
) : PriceHistoryUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    override fun priceHistory(command: PriceHistoryUseCase.PriceHistoryCommand): PriceHistoryUseCase.PriceHistoryResult {
        val priceHistory = loadPricePort.loadPrice(
            Product.ProductId(command.productId)
        ) ?.map {
            PriceHistoryUseCase.PriceHistoryResult.PriceHistory(it.price, it.date)
        } ?: let {
            log.info("가격 히스토리가 없습니다. productId: ${command}")
            throw NotFoundPriceException(ErrorMessage.NOT_FOUND_PRICE)
        }

        return PriceHistoryUseCase.PriceHistoryResult(priceHistory)
    }

}