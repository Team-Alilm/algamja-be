package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilm.global.jpa.base.BaseEntity

@Entity
@Table(
    name = "basket",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["member_id", "product_id"]
        )
    ]
)
class BasketJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val memberJpaEntity: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val productJpaEntity: ProductJpaEntity,

    @Column(nullable = false)
    val isAlilm: Boolean = false,

    @Column
    val alilmDate: Long? = null,

    @Column(nullable = false)
    val isHidden: Boolean = false
) : BaseEntity()