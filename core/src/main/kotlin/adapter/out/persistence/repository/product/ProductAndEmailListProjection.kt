package org.team_alilm.adapter.out.persistence.repository.product

import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

data class ProductAndMembersListProjection(
    val productJpaEntity: ProductJpaEntity,
    val emailList: List<String>,
    val nicknameList: List<String>
) {
    constructor(productJpaEntity: ProductJpaEntity, emailString: String, nicknameString: String) : this(
        productJpaEntity,
        emailString.split(", ").map { it.trim() },
        nicknameString.split(", ").map { it.trim() },
    )
}
