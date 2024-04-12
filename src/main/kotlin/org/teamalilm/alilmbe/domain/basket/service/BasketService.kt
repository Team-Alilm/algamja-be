package org.teamalilm.alilmbe.domain.basket.service

import java.time.LocalDateTime
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.basket.entity.Basket
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

data class BasketFindAllAnswer(
    val id: Long,
    val memberNickname: String,
    val productName: String,
    val productImageUrl: String,
    val productOption1: String,
    val productOption2: String?,
    val productOption3: String?,
    val productSelectCount: Int,
    val createdDate: LocalDateTime,
) {

    companion object {
        fun of(basket: Basket, size: Int) = BasketFindAllAnswer(
            id = basket.id!!,
            memberNickname = basket.member.nickname,
            productName = basket.product.name,
            productImageUrl = basket.product.imageUrl,
            productOption1 = basket.product.productInfo.option1,
            productOption2 = basket.product.productInfo.option2,
            productOption3 = basket.product.productInfo.option3,
            productSelectCount = size,
            createdDate = basket.createdDate
        )
    }
}