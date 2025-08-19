package org.team_alilm.member.controller

import common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.member.controller.docs.MemberDocs
import org.team_alilm.member.controller.dto.request.UpdateMyInfoRequest
import org.team_alilm.member.controller.dto.response.MyInfoResponse
import org.team_alilm.member.service.MemberService

@RestController
@RequestMapping("/api/v2/members")
class MemberController(

    private val memberService: MemberService
) : MemberDocs {

    @GetMapping("/me")
    override fun getMyInfo(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<MyInfoResponse> {
        val response = memberService.getMyInfo(
            memberId = customMemberDetails.memberRow.id
                ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
        )

        return ApiResponse.success(response)
    }

    @PutMapping
    override fun updateMyInfo(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        @RequestBody @Valid request: UpdateMyInfoRequest
    ): ApiResponse<Unit> {
        memberService.updateMyInfo(
            memberId = customMemberDetails.memberRow.id
                ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR),
            request = request
        )

        return ApiResponse.success(Unit)
    }
}