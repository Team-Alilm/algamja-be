package org.team_alilm.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Check
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLRestriction
import org.team_alilm.common.enums.Store
import org.team_alilm.common.jpa.base.BaseEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "product",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_store_store_number", columnNames = ["store", "store_number"]),
        UniqueConstraint(
            name = "uk_product_options",
            columnNames = ["store", "store_number", "first_option", "second_option", "third_option"]
        )
    ],
    indexes = [
        Index(name = "idx_brand", columnList = "brand"),
        Index(name = "idx_category1_2", columnList = "first_category, second_category"),
        Index(name = "idx_price", columnList = "price")
    ]
)
@Check(constraints = "price >= 0")
@SQLRestriction("is_delete = false")
class Product(

    @Column(name = "store_number", nullable = false)
    @Comment("외부몰(무신사/지그재그/29CM) 내 상품 식별자")
    val storeNumber :Long,

    @Column(nullable = false, length = 200)
    val name: String,

    @Column(nullable = false, length = 120)
    val brand: String,

    @Column(name = "thumbnail_url", nullable = false, length = 512)
    val thumbnailUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val store: Store,

    @Column(name = "first_category", length = 80)
    val firstCategory: String,

    @Column(name = "second_category", length = 80)
    var secondCategory: String? = null,

    // 금액은 BigDecimal 권장(원 단위면 scale=0)
    @Column(nullable = false, precision = 15, scale = 0)
    var price: BigDecimal,

    @Column(name = "first_option", length = 120)
    var firstOption: String? = null,

    @Column(name = "second_option", length = 120)
    var secondOption: String? = null,

    @Column(name = "third_option", length = 120)
    var thirdOption: String? = null,
) : BaseEntity<Long>() {

    protected constructor() : this(
        storeNumber = 0L,
        name = "",
        brand = "",
        thumbnailUrl = "",
        store = Store.MUSINSA,
        firstCategory = "",
        secondCategory = null,
        price = BigDecimal.ZERO
    )
}