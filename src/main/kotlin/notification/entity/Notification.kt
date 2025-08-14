package org.team_alilm.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.hibernate.annotations.Comment
import org.team_alilm.common.jpa.base.BaseEntity

@Entity
class Notification(

    @Comment("상품 ID")
    @Column(nullable = false, updatable = false)
    val productId: Long,

    @Comment("회원 ID")
    @Column(nullable = false, updatable = false)
    val memberId: Long,

    @Comment("읽음 여부")
    @Column(nullable = false)
    var readYn: Boolean

) : BaseEntity<Long>()