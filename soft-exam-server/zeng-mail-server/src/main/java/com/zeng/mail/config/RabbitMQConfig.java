package com.zeng.mail.config;

import com.zeng.mail.entity.MailConstants;
import com.zeng.mail.service.MailSendLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/5/2 0:47
 **/
@Configuration
@Slf4j
public class RabbitMQConfig {

    @Value("${mail.exchange:mail-exchange}")
    private String mailExchange;

    @Value("${mail.queue.feedback:mail-queue-feedback}")
    private String mailQueueFeedback;

    @Value("${mail.route.feedback:mail-route-feedback}")
    private String mailRouteFeedback;


    @Bean
    DirectExchange mailExchange(){
        return new DirectExchange(mailExchange,true,false);
    }

    /**
     * 反馈消息队列
     * @return
     */
    @Bean
    Queue mailQueueFeedback(){
        return new Queue(mailQueueFeedback,true);
    }

    @Bean
    Binding mailQueueFeedbackBinding(){
        return BindingBuilder.bind(mailQueueFeedback()).to(mailExchange()).with(mailRouteFeedback);
    }

    @Autowired
    CachingConnectionFactory cachingConnectionFactory;

    @Autowired
    MailSendLogService mailSendLogService;

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        //成功投递消息到Broker交换机站点的回调函数
        rabbitTemplate.setConfirmCallback((data,ack,cause)->{
            String msgId = data.getId();
            if(ack){
                log.info(msgId+"消息发送成功");
                //修改数据库中的记录，消息发送成功，将status设为1
                mailSendLogService.updateMailSendLogStatus(msgId, MailConstants.SUCCESS);
            }else{
                log.error(msgId+"消息发送失败！");
            }
        });
        //消息投递到Queue队列失败的回调函数
        rabbitTemplate.setReturnCallback((msg,repCode,repText,exchange,routingKey)->{
            log.error(msg.getBody()+"----消息从交换机投递到队列失败！\n错误原因："+repText);
            log.error("发送错误的交换机："+exchange+",发生错误的路由key："+routingKey);
        });
        return rabbitTemplate;
    }
}

