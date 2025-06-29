package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface OldBasketUseCase {

    fun loadOldBasket(command: OldBasketCommand): OldBasketResult?

    data class OldBasketCommand(
        val memberId: Member.MemberId
    )

    data class OldBasketResult(
        val oldProductInfo: OldProductInfo,
        val relatedProductList: List<RelateProduct>
    )

    data class OldProductInfo(
        val productId: Long,
        val thumbnailUrl: String,
        val brand: String,
        val store: String,
        val price: Int,
        val category: String,
        val createdDate: Long
    )

    data class RelateProduct(
        val productId: Long,
        val thumbnailUrl: String,
        val brand: String,
        val store: String,
        val price: Int,
        val category: String
    )
}