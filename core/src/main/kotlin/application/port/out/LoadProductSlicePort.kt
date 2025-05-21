package org.team_alilm.application.port.out

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.team_alilm.adapter.out.persistence.adapter.data.ProductAndWaitingCount

interface LoadProductSlicePort {

    fun loadProductSlice(pageRequest: PageRequest, category: String?): Slice<ProductAndWaitingCount>
}
