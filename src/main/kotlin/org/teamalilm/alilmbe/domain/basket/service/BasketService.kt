package org.teamalilm.alilmbe.domain.basket.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

@Service
@Transactional(readOnly = true)
class BasketService(
    private val basketRepository: BasketRepository
) {

    fun save(basketSaveCommand: BasketSaveCommand) {
        val basket = Basket(member = basketSaveCommand.member, product = basketSaveCommand.product)

        basketRepository.save(basket)
    }
}

data class BasketSaveCommand(
    val member: Member,
    val product: Product
) {

    companion object {
        fun from(member: Member, product: Product): BasketSaveCommand {
            return BasketSaveCommand(member = member, product = product)
        }
    }
}