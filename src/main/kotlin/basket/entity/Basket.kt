package org.team_alilm.basket.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.team_alilm.common.jpa.base.BaseEntity

@Entity
@Table(
    name = "basket",
    indexes = [
        Index(name = "idx_basket_member_id", columnList = "member_id"),
        Index(name = "idx_basket_product_id", columnList = "product_id"),
        Index(name = "idx_basket_member_product", columnList = "member_id, product_id")
    ]
)
@Comment("회원 장바구니")
class Basket(

    @Column(name = "member_id", nullable = false)
    @Comment("회원 ID")
    val memberId: Long,

    @Column(name = "product_id", nullable = false)
    @Comment("상품 ID")
    val productId: Long,

    @Column(name = "is_notification", nullable = false)
    @Comment("알림 신청 여부")
    var isNotification: Boolean = false,

    @Column(name = "notification_date")
    @Comment("알림 신청일(밀리초 타임스탬프)")
    var notificationDate: Long? = null,

    @Column(name = "is_hidden", nullable = false)
    @Comment("숨김 여부")
    var isHidden: Boolean = false

) : BaseEntity<Long>() {

    // JPA 기본 생성자 (protected로 제한)
    protected constructor() : this(
        memberId = 0L,
        productId = 0L
    )
}