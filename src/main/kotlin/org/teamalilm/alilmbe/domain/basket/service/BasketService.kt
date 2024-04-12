package org.teamalilm.alilmbe.domain.basket.service

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.basket.repository.data.CountBasketsGroupByProductIdWithProduct

@Service
@Transactional(readOnly = true)
class BasketService(
    private val basketRepository: BasketRepository
) {

    fun findAll(pageable: Pageable): Slice<CountBasketsGroupByProductIdWithProduct> {
        return basketRepository.countBasketsGroupByProductIdWithProduct(pageable)
    }
}