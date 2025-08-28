package org.team_alilm.fcm.entity

import org.team_alilm.common.entity.BaseLongIdTable

object FcmTokenTable : BaseLongIdTable("fcm_token") {
    val token     = varchar("token", 512)
    val memberId  = long("member_id")
    val isActive  = bool("is_active").default(true)

    // 필요하다면 index 추가
    init {
        index(true, token) // 예시: token은 유니크해야 한다면
        index(false, memberId)
    }
}