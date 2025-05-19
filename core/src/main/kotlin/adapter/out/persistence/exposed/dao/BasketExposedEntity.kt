package org.team_alilm.adapter.out.persistence.exposed.dao

import domain.Basket
import domain.Basket.BasketId
import domain.Member.MemberId
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.team_alilm.adapter.out.persistence.exposed.table.BasketExposedTable

class BasketExposedEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BasketExposedEntity>(BasketExposedTable)

    var createdDate by BasketExposedTable.createdDate
    var lastModifiedDate by BasketExposedTable.lastModifiedDate
    var alilmDate by BasketExposedTable.alilmDate

    var isDeleted by BasketExposedTable.isDeleted
    var isAlilm by BasketExposedTable.isAlilm
    var isHidden by BasketExposedTable.isHidden

    var memberId by BasketExposedTable.memberId
    var productId by BasketExposedTable.productId

    fun toDomain(): Basket {
        return Basket(
            id = BasketId(this.id.value),
            memberId = MemberId(memberId),
            productId = productId,
            isAlilm = isAlilm,
            alilmDate = alilmDate,
            isHidden = isHidden,
            isDelete = isDeleted,
            createdDate = createdDate
        )
    }

}