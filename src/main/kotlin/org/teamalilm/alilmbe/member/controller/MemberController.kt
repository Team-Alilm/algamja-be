package org.teamalilm.alilmbe.member.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.teamalilm.alilmbe.global.authority.TokenInfo
import org.teamalilm.alilmbe.global.dto.BaseResponse
import org.teamalilm.alilmbe.member.dto.MemberDtoRequest
import org.teamalilm.alilmbe.member.dto.MemberDtoResponse
import org.teamalilm.alilmbe.member.dto.MemberLoginDto
import org.teamalilm.alilmbe.member.service.MemberService

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        val resultMsg: String = memberService.signUp(memberDtoRequest)

        return BaseResponse(message = resultMsg)
    }

    /**
     * 로그인
     */
    @GetMapping("/login")
    fun login(@RequestBody @Valid memberLoginDto: MemberLoginDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.login(memberLoginDto)

        return BaseResponse(data = tokenInfo)
    }

    /**
     * 회원 정보 조회
     */
    @GetMapping("/info/{id}")
    fun findMyInfo(@PathVariable id: Long): BaseResponse<MemberDtoResponse> {
        val response = memberService.findMyInfo(id)

        return BaseResponse(data = response)
    }
}