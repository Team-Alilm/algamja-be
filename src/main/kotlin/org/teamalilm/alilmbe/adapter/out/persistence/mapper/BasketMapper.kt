package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Product

@Component
class BasketMapper {

    fun mapToJpaEntity(basket: Basket, memberJpaEntity: MemberJpaEntity, productJpaEntity: ProductJpaEntity): BasketJpaEntity {
        val basketJpaEntity = BasketJpaEntity(
            id = basket.id?.value,
            memberJpaEntity = memberJpaEntity,
            productJpaEntity = productJpaEntity,
            isHidden = basket.isHidden,
        )

        return basketJpaEntity
    }

    fun mapToDomainEntityOrNull(basketJpaEntity: BasketJpaEntity?): Basket? {
        basketJpaEntity ?: return null

        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberJpaEntity.id!!),
            productId = Product.ProductId(basketJpaEntity.productJpaEntity.id!!),
            isHidden = basketJpaEntity.isHidden,
            createdDate = basketJpaEntity.createdDate,
        )
    }

    fun mapToDomainEntity(basketJpaEntity: BasketJpaEntity): Basket {
        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberJpaEntity.id!!),
            productId = Product.ProductId(basketJpaEntity.productJpaEntity.id!!),
            isHidden = basketJpaEntity.isHidden,
            createdDate = basketJpaEntity.createdDate,
        )
    }
}