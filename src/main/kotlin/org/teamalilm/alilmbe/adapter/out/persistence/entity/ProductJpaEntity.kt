package org.teamalilm.alilmbe.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilmbe.domain.Product
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity

@Entity
@Table(
    name = "product",
    uniqueConstraints = [UniqueConstraint(
        name = "tag_key_number_size_color",
        columnNames = ["store", "number", "option1", "option2", "option3"]
    )]
)
class ProductJpaEntity(
    @Column(nullable = false)
    val number :Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val brand: String,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: Product.Store,

    @Column(nullable = false)
    val category: String,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    var waitingCount: Int = 0,

    @Column(nullable = false)
    val option1: String,

    @Column
    val option2: String?,

    @Column
    val option3: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()