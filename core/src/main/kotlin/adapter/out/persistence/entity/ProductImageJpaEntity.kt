package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(name = "product_image")
class ProductImageJpaEntity(

    @Column(nullable = false)
    private val productId: Long,

    @Column(nullable = false, unique = true)
    private val imageUrl: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseTimeEntity()
