package org.team_alilm.adapter.out.persistence.repository.product

import org.team_alilm.domain.product.Product

data class ProductAndMembersList(
    val product: Product,
    val memberInfoList: MemberInfo
)

data class MemberInfo(
    val emailList: List<String>,
    val nicknameList: List<String>
)