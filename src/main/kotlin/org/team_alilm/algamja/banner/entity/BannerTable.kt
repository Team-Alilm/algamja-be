package org.team_alilm.algamja.banner.entity

import org.team_alilm.algamja.common.entity.BaseLongIdTable

object BannerTable : BaseLongIdTable("banner") {
    val title = varchar("title", 100)
    val imageUrl = varchar("image_url", 512)
    val clickUrl = varchar("click_url", 512).nullable()
    val priority = integer("priority").default(0)
    val startDate = long("start_date")
    val endDate = long("end_date")
    val isActive = bool("is_active").default(true)

    init {
        index("idx_banner_priority", false, priority)
        index("idx_banner_dates", false, startDate, endDate)
        index("idx_banner_active", false, isActive)
    }
}