package org.team_alilm.algamja.banner.repository

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.algamja.banner.entity.BannerRow
import org.team_alilm.algamja.banner.entity.BannerTable
import org.team_alilm.algamja.common.entity.insertAudited
import org.team_alilm.algamja.common.entity.softDeleteById
import org.team_alilm.algamja.common.entity.updateAudited

@Repository
class BannerRepository {

    fun fetchActiveBanners(): List<BannerRow> {
        val currentTime = System.currentTimeMillis()
        return BannerTable
            .selectAll()
            .where {
                (BannerTable.isActive eq true) and
                (BannerTable.isDelete eq false) and
                (BannerTable.startDate lessEq currentTime) and
                (BannerTable.endDate greaterEq currentTime)
            }
            .orderBy(BannerTable.priority to SortOrder.DESC, BannerTable.createdDate to SortOrder.DESC)
            .map { BannerRow.wrapRow(it) }
    }

    fun fetchById(id: Long): BannerRow? =
        BannerTable
            .selectAll()
            .where { (BannerTable.id eq id) and (BannerTable.isDelete eq false) }
            .map { BannerRow.wrapRow(it) }
            .firstOrNull()

    fun fetchAll(): List<BannerRow> =
        BannerTable
            .selectAll()
            .where { BannerTable.isDelete eq false }
            .orderBy(BannerTable.priority to SortOrder.DESC, BannerTable.createdDate to SortOrder.DESC)
            .map { BannerRow.wrapRow(it) }

    fun create(
        title: String,
        imageUrl: String,
        clickUrl: String?,
        priority: Int,
        startDate: Long,
        endDate: Long,
        isActive: Boolean = true
    ): Long {
        val stmt = BannerTable.insertAudited {
            it[this.title] = title
            it[this.imageUrl] = imageUrl
            it[this.clickUrl] = clickUrl
            it[this.priority] = priority
            it[this.startDate] = startDate
            it[this.endDate] = endDate
            it[this.isActive] = isActive
        }
        return stmt[BannerTable.id].value
    }

    fun update(
        id: Long,
        title: String,
        imageUrl: String,
        clickUrl: String?,
        priority: Int,
        startDate: Long,
        endDate: Long,
        isActive: Boolean
    ): Int =
        BannerTable.updateAudited(
            where = { BannerTable.id eq id }
        ) {
            it[this.title] = title
            it[this.imageUrl] = imageUrl
            it[this.clickUrl] = clickUrl
            it[this.priority] = priority
            it[this.startDate] = startDate
            it[this.endDate] = endDate
            it[this.isActive] = isActive
        }

    fun softDelete(id: Long): Int =
        BannerTable.softDeleteById(id)

    fun updateActiveStatus(id: Long, isActive: Boolean): Int =
        BannerTable.updateAudited(
            where = { BannerTable.id eq id }
        ) {
            it[this.isActive] = isActive
        }
}