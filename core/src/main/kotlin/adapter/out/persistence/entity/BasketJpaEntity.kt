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
open class BasketJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    var isAlilm: Boolean = false,

    @Column
    var alilmDate: Long? = null,

    @Column(nullable = false)
    var isHidden: Boolean = false
) : BaseTimeEntity()