package org.teamalilm.alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilm.global.jpa.base.BaseEntity

@Entity
@Table(
    name = "price"
)
class PriceJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val price: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val productJpaEntity: ProductJpaEntity
) : BaseEntity()