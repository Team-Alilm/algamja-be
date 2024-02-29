package org.teamalilm.alilmbe.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.member.entity.MemberRole

interface MemberRoleRepository : JpaRepository<MemberRole, Long>