package org.teamalilm.alilmbe.domain.product.entity

import jakarta.persistence.*
import org.teamalilm.alilmbe.global.entity.BaseTimeEntity

@Entity
class Product(

    @Column(nullable = false)
    private val name: String,

    @Column(nullable = false)
    private val size: String,

    @Column(nullable = false)
    private val color: String,

    @Enumerated(value = EnumType.STRING)
    private val store: Store,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
) : BaseTimeEntity() {
}