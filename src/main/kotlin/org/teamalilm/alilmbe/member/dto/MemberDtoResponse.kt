package org.teamalilm.alilmbe.member.dto

import org.teamalilm.alilmbe.member.entity.Member
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MemberDtoResponse(
    val id: Long,
    val loginId: String,
    val name: String,
    val birthDate: String,
    val gender: String,
    val email: String,
) {

    companion object {
        fun of(member: Member) : MemberDtoResponse {
            return MemberDtoResponse(
                id = member.id!!,
                loginId = member.loginId,
                name = member.name,
                birthDate = member.birthDate.formatDate(),
                gender = member.gender.desc,
                email = member.email
            )
        }

        private fun LocalDate.formatDate(): String =
            this.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

    }

}