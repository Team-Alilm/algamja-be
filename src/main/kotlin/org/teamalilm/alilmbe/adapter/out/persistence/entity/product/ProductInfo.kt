package org.teamalilm.alilmbe.adapter.out.persistence.entity.product

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class ProductInfo(
    @Column(name = "store", nullable = false)
    @Enumerated(EnumType.STRING)
    val store: Store,

    @Column(name = "number", nullable = false)
    val number: Int,

    @Column(name = "option1", nullable = false)
    val option1: String,

    @Column(name = "option2")
    val option2: String?,

    @Column(name = "option3")
    val option3: String?
) {


}
