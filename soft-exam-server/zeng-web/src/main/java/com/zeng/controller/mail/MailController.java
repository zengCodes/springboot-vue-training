package com.zeng.controller.mail;

import com.zeng.common.core.domain.ResponseBean;
import com.zeng.mail.entity.Feedback;
import com.zeng.mail.service.FeedbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/5/2 0:50
 **/
@RestController
@RequestMapping("/mail")
public class MailController {


    @Resource
    FeedbackService feedbackService;
    /**
     * 发送反馈消息给系统管理员
     * @param feedback
     * @return
     */
    @PostMapping("/feedback")
    public ResponseBean sendFeedbackToMail(@RequestBody Feedback feedback){
        try{
            feedbackService.sendMessage(feedback);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return ResponseBean.success("请求邮件发送成功！收到会及时添加~");
        }
    }
}

