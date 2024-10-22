package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

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

    @Column(name = "member_id", nullable = false)
    val memberJpaEntityId: Long,

    @Column(name = "product_id", nullable = false)
    val productJpaEntityId: Long,

    @Column(nullable = false)
    val isAlilm: Boolean = false,

    @Column
    val alilmDate: Long? = null,

    @Column(nullable = false)
    val isHidden: Boolean = false
) : BaseTimeEntity()