package org.team_alilm.member.controller

import common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.member.controller.docs.MemberControllerDocs
import org.team_alilm.member.controller.dto.MyInfoResponse
import org.team_alilm.member.service.MemberService

@RestController
class MemberController(
    private val memberService: MemberService
) : MemberControllerDocs {

    override fun getMyInfo(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ResponseEntity<ApiResponse<MyInfoResponse>> {
        memberService.getMyInfo(customMemberDetails.member.id)

        val response = MyInfoResponse(

        )

        return ApiResponse.success<MyInfoResponse>()
    }
}