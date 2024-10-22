package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

@Component
class BasketMapper {

    fun mapToJpaEntity(basket: Basket, memberJpaEntityId: Long, productJpaEntityId: Long): BasketJpaEntity {
        return BasketJpaEntity(
            id = basket.id?.value,
            memberJpaEntityId = memberJpaEntityId,
            productJpaEntityId = productJpaEntityId,
            isAlilm = basket.isAlilm,
            alilmDate = basket.alilmDate,
            isHidden = basket.isHidden,
        )
    }

    fun mapToDomainEntity(basketJpaEntity: BasketJpaEntity): Basket {
        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberJpaEntityId),
            productId = Product.ProductId(basketJpaEntity.productJpaEntityId),
            isAlilm = basketJpaEntity.isAlilm,
            alilmDate = basketJpaEntity.alilmDate,
            isHidden = basketJpaEntity.isHidden,
        )
    }

    fun mapToDomainEntityOrNull(basketJpaEntity: BasketJpaEntity?): Basket? {
        return basketJpaEntity?.let { mapToDomainEntity(it) }
    }

}
