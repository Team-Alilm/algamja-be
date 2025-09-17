package org.team_alilm.algamja.banner.controller.dto.response

import org.team_alilm.algamja.banner.entity.BannerRow

data class BannerResponse(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val clickUrl: String?,
    val priority: Int
) {
    companion object {
        fun from(banner: BannerRow): BannerResponse {
            return BannerResponse(
                id = banner.id.value,
                title = banner.title,
                imageUrl = banner.imageUrl,
                clickUrl = banner.clickUrl,
                priority = banner.priority
            )
        }
    }
}