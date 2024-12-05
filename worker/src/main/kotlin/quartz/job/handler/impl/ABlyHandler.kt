package org.team_alilm.quartz.job.handler.impl

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndMembersList
import org.team_alilm.quartz.job.handler.PlatformHandler


@Component
class ABlyHandler : PlatformHandler {

    override fun process(productAndEmailList: ProductAndMembersList) {
        TODO("Not yet implemented")
    }

}