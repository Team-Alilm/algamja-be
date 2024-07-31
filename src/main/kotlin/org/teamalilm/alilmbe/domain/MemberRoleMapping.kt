package org.teamalilm.alilmbe.domain

class MemberRoleMapping (
    val id: Long,
    val memberId: Member.MemberId,
    val roleId: Role.RoleId
)