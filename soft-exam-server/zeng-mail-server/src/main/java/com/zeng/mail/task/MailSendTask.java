package com.zeng.mail.task;

import com.zeng.mail.entity.MailConstants;
import com.zeng.mail.entity.MailSendLog;
import com.zeng.mail.service.MailSendLogService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;



@Component
public class MailSendTask {
    @Resource
    MailSendLogService mailSendLogService;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/10 * * * * ?")
    public void mailSendTask() {
        //TODO 这里每十秒就查询一次，可以考虑使用视图和索引提高效率
        //获取发送失败且到达指定重试时间的消息记录
        List<MailSendLog> mailSendLogList = mailSendLogService.getMailSendLogsByStatus(MailConstants.DELIVERING);
        mailSendLogList.forEach(mailSendLog -> {
            if (mailSendLog.getCount() > MailConstants.MAX_TRY_COUNT) {
                //更新状态为发送失败
                mailSendLogService.updateMailSendLogStatus(mailSendLog.getMsgId(), MailConstants.FAILURE);
            } else {
                //更新消息投递的尝试次数和时间
                mailSendLogService.updateCount(mailSendLog.getMsgId(), new Date());
                //获取消息
                String message=mailSendLogService.getMsgById(mailSendLog.getMsgId());
                //再次投递消息
                rabbitTemplate.convertAndSend(mailSendLog.getExchange(),mailSendLog.getRouteKey(), message, new CorrelationData(mailSendLog.getMsgId()));
            }
        });
    }
}
