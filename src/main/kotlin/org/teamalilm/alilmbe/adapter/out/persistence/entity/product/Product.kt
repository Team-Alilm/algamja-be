package org.teamalilm.alilmbe.adapter.out.persistence.entity.product

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "tag_key_number_size_color",
        columnNames = ["store", "number", "option1", "option2", "option3"]
    )]
)
class Product(
    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "brand")
    val brand: String,

    @Column(name = "image_url")
    val imageUrl: String,

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