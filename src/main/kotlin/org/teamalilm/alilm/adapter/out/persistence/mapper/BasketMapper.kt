package org.teamalilm.alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

@Component
class BasketMapper {

    fun mapToJpaEntity(basket: Basket, memberJpaEntity: MemberJpaEntity, productJpaEntity: ProductJpaEntity): BasketJpaEntity {
        return BasketJpaEntity(
            id = basket.id?.value,
            memberJpaEntity = memberJpaEntity,
            productJpaEntity = productJpaEntity,
            isAlilm = basket.isAlilm,
            alilmDate = basket.alilmDate,
            isHidden = basket.isHidden,
        )
    }

    fun mapToDomainEntity(basketJpaEntity: BasketJpaEntity): Basket {
        return Basket(
            id = Basket.BasketId(basketJpaEntity.id),
            memberId = Member.MemberId(basketJpaEntity.memberJpaEntity.id ?: error("Member ID is null")),
            productId = Product.ProductId(basketJpaEntity.productJpaEntity.id ?: error("Product ID is null")),
            isAlilm = basketJpaEntity.isAlilm,
            alilmDate = basketJpaEntity.alilmDate,
            isHidden = basketJpaEntity.isHidden,
        )
    }

    fun mapToDomainEntityOrNull(basketJpaEntity: BasketJpaEntity?): Basket? {
        return basketJpaEntity?.let { mapToDomainEntity(it) }
    }

}
