package org.team_alilm.adapter.out.persistence.entity

import domain.product.Store
import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(name = "product_image",
    indexes = [
        Index(
            name = "idx_product_number_store_image_url",
            columnList = "product_number, product_store, image_url"
        ),
    ]
)
class ProductImageJpaEntity(

    @Column(nullable = false, unique = true)
    val imageUrl: String,

    @Column(nullable = false)
    val productNumber: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val productStore: Store,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseTimeEntity()
