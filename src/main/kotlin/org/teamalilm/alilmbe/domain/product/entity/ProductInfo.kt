package org.teamalilm.alilmbe.domain.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class ProductInfo(
    @Column(nullable = false)
    val number: Long,

    @Column(nullable = false)
    val option1: String?,

    @Column(nullable = false)
    val option2: String?
) {

}