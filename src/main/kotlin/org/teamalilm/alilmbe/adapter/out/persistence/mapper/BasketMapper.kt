package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.BasketJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.product.Product

@Component
class BasketMapper {

    fun mapToJpaEntity(basket: Basket, memberJpaEntity: MemberJpaEntity, productJpaEntity: ProductJpaEntity): BasketJpaEntity {
        return BasketJpaEntity(
            id = basket.id.value,
            memberJpaEntity = memberJpaEntity,
            productJpaEntity = productJpaEntity
        )
    }

    fun mapToDomainEntity(basketJpaEntity: BasketJpaEntity?): Basket? {
        basketJpaEntity ?: return null

        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberJpaEntity.id),
            productId = Product.ProductId(basketJpaEntity.productJpaEntity.id)
        )
    }

    fun mapToDomainEntity(basketJpaEntity: BasketJpaEntity): Basket {
        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberJpaEntity.id),
            productId = Product.ProductId(basketJpaEntity.productJpaEntity.id)
        )
    }
}