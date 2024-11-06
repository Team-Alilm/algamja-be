package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId

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
