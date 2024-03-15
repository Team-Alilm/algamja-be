package org.teamalilm.alilmbe.domain.basket.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository

@Service
@Transactional(readOnly = true)
class BasketService(
    private val basketRepository: BasketRepository
) {

    @Transactional
    fun findAll(): List<BasketFindAllAnswer> {
        val baskets = basketRepository.findAll()

        return baskets.map { BasketFindAllAnswer(it) }
    }
}

data class BasketFindAllAnswer(
    val id: Long,
) {

    constructor(basket: Basket) : this(
        id = basket.id ?: 1
    )
}