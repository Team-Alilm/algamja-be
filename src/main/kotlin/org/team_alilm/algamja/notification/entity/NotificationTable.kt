package org.team_alilm.algamja.notification.entity

import org.team_alilm.algamja.common.entity.BaseLongIdTable

object NotificationTable : BaseLongIdTable("notification") {
    val productId = long("product_id")
    val memberId  = long("member_id")
    val readYn    = bool("read_yn").default(false)

    init {
        // 조회/필터 최적화 인덱스
        index(false, memberId)                    // 회원별 알림 목록 조회
        index(false, memberId, id)                // 회원별 최신순 페이징
        index(false, productId)                   // 상품 관련 알림 조회
        // 필요 시 유니크 제약 추가 가능 (예: 같은 상품에 대한 중복 알림 방지)
        // uniqueIndex("ux_member_product", memberId, productId)
    }
}