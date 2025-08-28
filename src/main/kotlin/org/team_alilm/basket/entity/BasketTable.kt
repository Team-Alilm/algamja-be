package org.team_alilm.basket.entity

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.or
import org.team_alilm.common.entity.BaseLongIdTable

object BasketTable : BaseLongIdTable("basket") {
    // 나중에 FK로 바꿀 계획이면 reference(...) 사용 고려 (현재는 Long 유지)
    val memberId         = long("member_id")
    val productId        = long("product_id")

    val isNotification   = bool("is_notification").default(false)
    val notificationDate = long("notification_date").nullable()

    val isHidden         = bool("is_hidden").default(false)

    init {
        // 자주 쓰는 조건: memberId + is_delete=false
        index("idx_basket_member_active", false, memberId, isDelete)

        // 자주 쓰는 조건: productId + is_delete=false (대기 인원수 집계 등)
        index("idx_basket_product_active", false, productId, isDelete)

        // 단건 조회/중복 방지: 같은 member 가 같은 product 를 여러 번 담지 못하게
        uniqueIndex("uk_basket_member_product", memberId, productId)

        // (선택) 비즈니스 규칙: 알림을 켜면 날짜가 있어야 한다
        //  DB가 지원하면 체크 제약 추가
        check(
            name = "ck_basket_notification_date_required",
            op = { (isNotification eq false) or notificationDate.isNotNull() }
        )
    }

    /** 공통 where 절에 붙일 ‘활성(미삭제)’ 필터 */
    fun active() = (isDelete eq false)

    /** 정렬 헬퍼 (최근 담은 순) */
    fun defaultOrder() = id to SortOrder.DESC
}