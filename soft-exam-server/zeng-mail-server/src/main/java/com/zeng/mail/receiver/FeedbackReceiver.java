package com.zeng.mail.receiver;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.zeng.mail.entity.Feedback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Date;


@Component
@Slf4j
public class FeedbackReceiver {


    @Autowired(required = false)
    JavaMailSender javaMailSender;

    @Value("$spring.mail.username")
    private String mail;

    @Autowired
    StringRedisTemplate redisTemplate;

    @RabbitListener(queues ="${mail.queue.feedback:mail-queue-feedback}")
    public void getFeedbackMessage(Message message, Channel channel) throws IOException {
        //获取消息内容
//        String s = message.getPayload().toString();
//        String msgId="";
//        try{
//            Feedback feedback = JSON.parseObject(s, Feedback.class);
//            msgId = feedback.getId().toString();
//            System.out.println(feedback.getContent());
//            SimpleMailMessage mailMessage = new SimpleMailMessage();
//            mailMessage.setSubject("来自用户的意见反馈");
//            //读取信息
//            StringBuilder formatMessage = new StringBuilder();
//            formatMessage.append("用户编号："+feedback.getUserId()+"\n");
//            formatMessage.append("用户名："+feedback.getUsername()+"\n");
//            formatMessage.append("用户昵称："+feedback.getNickname()+"\n");
//            formatMessage.append("反馈内容："+feedback.getContent());
//            System.out.println(">>>>>>>>>>>>>>"+formatMessage+"<<<<<<<<<<<<<<<<<<");
//            //设置邮件消息
//            mailMessage.setText(formatMessage.toString());
//            mailMessage.setFrom("1964189632@qq.com");
//            mailMessage.setTo("1964189632@qq.com");
//            mailMessage.setSentDate(new Date());
//            javaMailSender.send(mailMessage);
//        }catch (Exception e){
//            log.info("【"+msgId+"】消息重新放回到了队列中");
//            e.printStackTrace();
//        }
        String s = message.getPayload().toString();
        //获取消息的唯一标志
        MessageHeaders headers = message.getHeaders();
        log.info("-----------"+JSON.parse(JSON.toJSONString(headers)));
        Long tag = ((Long) headers.get(AmqpHeaders.DELIVERY_TAG));
        String msgId = headers.get("spring_returned_message_correlation").toString();
        log.info("【"+msgId+"】-正在处理的消息");
        //是否已消费
        if (redisTemplate.opsForHash().entries("mail_log").containsKey(msgId)){
            channel.basicAck(tag,true);
            log.info("【"+msgId+"】消息出现重复消费");
            return;
        }
        try{
            Feedback feedback = JSON.parseObject(s, Feedback.class);
            System.out.println(feedback.getContent());
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("来自用户的请求添加");
            //读取信息
            StringBuilder formatMessage = new StringBuilder();
            formatMessage.append("用户编号："+feedback.getUserId()+"\n");
            formatMessage.append("用户名："+feedback.getUsername()+"\n");
            formatMessage.append("用户昵称："+feedback.getNickname()+"\n");
            formatMessage.append("请求添加咨询人描述信息："+feedback.getContent());
            System.out.println(">>>>>>>>>>>>>>"+formatMessage+"<<<<<<<<<<<<<<<<<<");
            //设置邮件消息
            mailMessage.setText(formatMessage.toString());
            mailMessage.setFrom(feedback.getEmail());
            mailMessage.setTo("1964189632@qq.com");
            mailMessage.setSentDate(new Date());
            javaMailSender.send(mailMessage);
            //消息处理完成
            // redisTemplate.opsForHash().entries("mail_log").put(msgId,msgId+1);
            redisTemplate.opsForHash().put("mail_log",msgId,feedback.getContent());
            //手动确定消息处理完成
            channel.basicAck(tag,true);
        }catch (Exception e){
            //出现异常就重新放回到队列中
            channel.basicNack(tag,false,true);
            log.info("【"+msgId+"】消息重新放回到了队列中");
            e.printStackTrace();
        }

    }
}
