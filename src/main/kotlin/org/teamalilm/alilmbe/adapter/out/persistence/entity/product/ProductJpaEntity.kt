package org.teamalilm.alilmbe.adapter.out.persistence.entity.product

import jakarta.persistence.*
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "tag_key_number_size_color",
        columnNames = ["store", "number", "option1", "option2", "option3"]
    )]
)
class ProductJpaEntity(
    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "brand")
    val brand: String,

    @Column(name = "image_url")
    val imageUrl: String,

    @Column(name = "store")
    @Enumerated(EnumType.STRING)
    val store: Store,

    @Column(name = "category")
    val category: String,

    @Column(name = "price")
    val price: Int,

    @Column(name = "waiting_count")
    var waitingCount: Int = 0,

    @Embedded
    val productInfo: ProductInfo,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()