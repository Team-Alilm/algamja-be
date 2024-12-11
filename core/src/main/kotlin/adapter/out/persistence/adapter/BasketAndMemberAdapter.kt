package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.BasketMapper
import org.team_alilm.adapter.out.persistence.mapper.MemberMapper
import org.team_alilm.adapter.out.persistence.repository.BasketRepository
import org.team_alilm.application.port.out.LoadBasketAndMemberPort
import org.team_alilm.application.port.out.LoadBasketAndMemberPort.*
import org.team_alilm.domain.product.Product

@Component
class BasketAndMemberAdapter(
    private val basketRepository: BasketRepository,
    private val memberMapper: MemberMapper,
    private val basketMapper: BasketMapper
) : LoadBasketAndMemberPort {

    override fun loadBasketAndMember(product: Product) : List<BasketAndMember> {
        val productId = product.id?.value ?: return emptyList()

        return basketRepository.findBasketAndMemberByProductNumberAndMemberId(productId = productId)
            .map {
                BasketAndMember(
                    basket = basketMapper.mapToDomainEntity(it.basketJpaEntity),
                    member = memberMapper.mapToDomainEntity(it.memberJpaEntity)
                )
            }
    }


}