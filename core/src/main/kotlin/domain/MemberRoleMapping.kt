package org.team_alilm.domain

class MemberRoleMapping (
    val id: Long,
    val memberId: Member.MemberId,
    val roleId: Role.RoleId
)