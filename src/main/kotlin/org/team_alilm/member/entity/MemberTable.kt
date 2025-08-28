package org.team_alilm.member.entity

import org.team_alilm.common.entity.BaseLongIdTable
import org.team_alilm.common.enums.Provider

object MemberTable : BaseLongIdTable("member") {
    val provider   = enumerationByName<Provider>("provider", 20)
    val providerId = varchar("provider_id", 64)
    val email      = varchar("email", 255)
    val nickname   = varchar("nickname", 50)

    init {
        uniqueIndex("uk_member_provider_pid", provider, providerId)
    }
}