package org.team_alilm.quartz.job

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.port.out.LoadMemberPort

@Component
@Transactional(readOnly = true)
class MemberCountJob(
    private val loadMemberPort: LoadMemberPort,
    private val slackGateway: SlackGateway
) : Job {

    override fun execute(context: JobExecutionContext) {
        slackGateway.sendMessage("목표를 향한 현재까지 회원 수 : ${loadMemberPort.loadMemberCount()}")
    }
}