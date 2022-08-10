package com.zeng.mail.service.Impl;

import com.alibaba.fastjson.JSON;
import com.zeng.common.exception.CustomException;
import com.zeng.mail.entity.Feedback;
import com.zeng.mail.entity.MailConstants;
import com.zeng.mail.entity.MailSendLog;
import com.zeng.mail.mapper.FeedbackMapper;
import com.zeng.mail.mapper.MailSendLogMapper;
import com.zeng.mail.service.FeedbackService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/5/2 0:52
 **/
@Service("FeedbackService")
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper;

    @Resource
    private MailSendLogMapper mailSendLogMapper;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Value("${mail.exchange:mail-exchange}")
    private String mailExchange;

    @Value("${mail.queue.feedback:mail-queue-feedback}")
    private String mailQueueFeedback;

    @Value("${mail.route.feedback:mail-route-feedback}")
    private String mailRouteFeedback;

    @Override
    public int sendMessage(Feedback feedback) {
        feedback.setId(UUID.randomUUID().toString());
        int n = feedbackMapper.insertSelective(feedback);
        //将内容转化为JSON字符串
        String record = JSON.toJSONString(feedback);
        String msgId=UUID.randomUUID().toString();
        MailSendLog sendLog = new MailSendLog();
        sendLog.setMsgId(msgId);
        sendLog.setContent(record);
        sendLog.setContentType(MailConstants.FEEDBACK_TYPE);
        sendLog.setCount(1);
        sendLog.setCreateTime(new Date());
        sendLog.setUpdateTime(new Date());
        //当前超过一分钟后开始重试
        sendLog.setTryTime(new Date(System.currentTimeMillis()+1000*60*MailConstants.MEG_TIMEOUT));
        sendLog.setExchange(mailExchange);
        sendLog.setRouteKey(mailRouteFeedback);
        sendLog.setStatus(MailConstants.DELIVERING);
        //新增消息发送记录
        mailSendLogMapper.insert(sendLog);
        // 投递信息
        rabbitTemplate.convertAndSend(mailExchange,mailRouteFeedback,record,new CorrelationData(msgId));
        return n;
    }
}
