package org.teamalilm.alilmbe.domain.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class ProductInfo(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: Store,

    @Column(nullable = false)
    val number: String,

    @Column(nullable = false)
    val option1: String,

    @Column
    val option2: String?,

    @Column
    val option3: String?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductInfo

        if (store != other.store) return false
        if (number != other.number) return false
        if (option1 != other.option1) return false
        if (option2 != other.option2) return false
        if (option3 != other.option3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = store.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + option1.hashCode()
        result = 31 * result + (option2?.hashCode() ?: 0)
        result = 31 * result + (option3?.hashCode() ?: 0)
        return result
    }
}