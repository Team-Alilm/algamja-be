package org.team_alilm.adapter.out.persistence.mapper

import domain.Basket
import domain.Member
import domain.product.ProductId
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity

@Component
class BasketMapper {

    fun mapToJpaEntity(basket: Basket, memberId: Long, productId: Long): BasketJpaEntity {
        return BasketJpaEntity(
            id = basket.id?.value,
            memberId = memberId,
            productId = productId,
            isAlilm = basket.isAlilm,
            alilmDate = basket.alilmDate,
            isHidden = basket.isHidden,
        )
    }

    fun mapToDomainEntity(basketJpaEntity: BasketJpaEntity): Basket {
        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberId),
            productId = ProductId(basketJpaEntity.productId),
            isAlilm = basketJpaEntity.isAlilm,
            alilmDate = basketJpaEntity.alilmDate,
            isHidden = basketJpaEntity.isHidden,
            isDelete = basketJpaEntity.isDelete
        )
    }

    fun mapToDomainEntityOrNull(basketJpaEntity: BasketJpaEntity?): Basket? {
        return basketJpaEntity?.let { mapToDomainEntity(it) }
    }

}
