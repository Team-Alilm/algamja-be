package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.domain.Product
import org.team_alilm.global.error.ErrorMessage
import org.team_alilm.global.error.NotFoundPriceException

@Service
@Transactional(readOnly = true)
class PriceHistoryService(
    val loadPricePort: org.team_alilm.application.port.out.LoadPricePort
) : org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    override fun priceHistory(command: org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryCommand): org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryResult {
        val priceHistory = loadPricePort.loadPrice(
            Product.ProductId(command.productId)
        ) ?.map {
            org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryResult.PriceHistory(it.price, it.date)
        } ?: let {
            log.info("가격 히스토리가 없습니다. productId: ${command}")
            throw NotFoundPriceException(ErrorMessage.NOT_FOUND_PRICE)
        }

        return org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryResult(priceHistory)
    }

}