package org.team_alilm.fcm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.team_alilm.common.jpa.base.BaseEntity

@Entity
@Table(
    name = "fcm_token",
    indexes = [
        // 여기에 필요한 인덱스 추가
    ]
)
class FcmToken(

    @Column(name = "token", nullable = false, length = 512)
    @org.hibernate.annotations.Comment("FCM 토큰")
    val token: String,

    @Column(name = "member_id", nullable = false)
    @org.hibernate.annotations.Comment("회원 ID")
    val memberId: Long,

    @Column(name = "is_active", nullable = false)
    @org.hibernate.annotations.Comment("활성화 여부")
    val isActive: Boolean = true

) : BaseEntity<Long>() {

    protected constructor() : this(
        token = "",
        memberId = 0L,
        isActive = true
    )
}