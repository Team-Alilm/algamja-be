package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.Store
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(
    name = "product",
    uniqueConstraints = [UniqueConstraint(
        name = "tag_key_number_size_color",
        columnNames = ["store", "number", "first_option", "second_option", "third_option"]
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
    val thumbnailUrl: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: Store,

    @Column(nullable = false)
    val category: String,

    @Column
    val firstCategory: String?,

    @Column
    val secondCategory: String?,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    val firstOption: String,

    @Column
    val secondOption: String?,

    @Column
    val thirdOption: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseTimeEntity()