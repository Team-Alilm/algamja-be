package org.team_alilm.quartz.job.handler

import org.team_alilm.adapter.out.persistence.repository.product.ProductAndMembersList

interface PlatformHandler {

    fun process(productAndEmailList: ProductAndMembersList)
}