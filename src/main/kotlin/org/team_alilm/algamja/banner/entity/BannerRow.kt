package org.team_alilm.algamja.banner.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BannerRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BannerRow>(BannerTable)

    var title by BannerTable.title
    var imageUrl by BannerTable.imageUrl
    var clickUrl by BannerTable.clickUrl
    var priority by BannerTable.priority
    var startDate by BannerTable.startDate
    var endDate by BannerTable.endDate
    var isActive by BannerTable.isActive
    var isDelete by BannerTable.isDelete
    var createdAt by BannerTable.createdAt
    var updatedAt by BannerTable.updatedAt
}