package org.teamalilm.alilmbe.member.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.global.authority.JwtProvider
import org.teamalilm.alilmbe.global.authority.TokenInfo
import org.teamalilm.alilmbe.global.exception.InvalidInputException
import org.teamalilm.alilmbe.member.dto.MemberDtoRequest
import org.teamalilm.alilmbe.member.dto.MemberDtoResponse
import org.teamalilm.alilmbe.member.dto.MemberLoginDto
import org.teamalilm.alilmbe.member.entity.MemberRole
import org.teamalilm.alilmbe.member.entity.Role
import org.teamalilm.alilmbe.member.repository.MemberRepository
import org.teamalilm.alilmbe.member.repository.MemberRoleRepository

@Transactional(readOnly = true)
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtProvider: JwtProvider
) {

    /**
     * 회원가입
     */
    @Transactional
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        if (memberRepository.existsByLoginId(memberDtoRequest.loginId)) {
            throw InvalidInputException("loginId", "이미 등록된 ID 입니다.")
        }

        val member = memberDtoRequest.toEntity()
        memberRepository.save(member)

        val memberRole = MemberRole(null, Role.MEMBER, member)
        memberRoleRepository.save(memberRole)

        return "회원가입이 완료되었습니다."
    }

    /**
     * 로그인, 토큰 발급
     */
    fun login(memberLoginDto: MemberLoginDto): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(memberLoginDto.loginId, memberLoginDto.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return jwtProvider.createToken(authentication)
    }

    fun findMyInfo(id: Long): MemberDtoResponse? {
        val member = memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("")

        return MemberDtoResponse.of(member)
    }

}