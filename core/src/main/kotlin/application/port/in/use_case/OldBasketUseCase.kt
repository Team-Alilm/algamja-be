package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface OldBasketUseCase {

    fun loadOldBasket(command: OldBasketUseCase.OldBasketCommand): OldBasketUseCase.OldBasketResult

    data class OldBasketCommand(
        val memberId: Member.MemberId
    )

    data class OldBasketResult(
        val oldProductInfo: OldProductInfo,
        val relatedProductList: List<RelateProduct>
    )

    data class OldProductInfo(
        val thumbnailUrl: String,
        val createdDate: Long
    )

    data class RelateProduct(
        val thumbnailUrl: String
    )
}