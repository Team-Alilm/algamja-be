package org.teamalilm.alilmbe.global

import org.springframework.data.domain.Sort

enum class SortCondition(
    val sort: Sort
) {
    LAST_MODIFIED_DATE(Sort.by(Sort.Direction.DESC, "lastModifiedDate"))


}