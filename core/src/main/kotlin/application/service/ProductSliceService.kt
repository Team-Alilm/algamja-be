package org.team_alilm.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase.ProductSliceResult
import org.team_alilm.application.port.out.LoadProductPort

@Service
@Transactional(readOnly = true)
class ProductSliceService (
    private val loadProductPort: LoadProductPort,
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {

        val pageRequest = when (command.sort) {
            "PRICE_ASC" -> PageRequest.of(command.page, command.size, Sort.by(Sort.Direction.ASC, "price"))
            "PRICE_DESC" -> PageRequest.of(command.page, command.size, Sort.by(Sort.Direction.DESC, "price"))
            "LATEST" -> PageRequest.of(command.page, command.size, Sort.by(Sort.Direction.DESC, "lastModifiedDate"))
            "WAITING_COUNT" -> PageRequest.of(command.page, command.size) // 정렬 없음 → JPQL에서 ORDER BY COUNT(b.id)
            else -> throw IllegalArgumentException("지원하지 않는 정렬 방식입니다.")
        }

        val productSlice = loadProductPort.loadProductSlice(
            pageRequest,
            command.category,
            command.sort,
        )

        return ProductSliceUseCase.CustomSlice(
            contents = productSlice.content.map {
                ProductSliceResult.from(product = it.product, waitingCount = it.waitingCount)
            },
            hasNext = productSlice.hasNext(),
            isLast = productSlice.isLast,
            number = productSlice.number,
            size = productSlice.size
        )
    }
}