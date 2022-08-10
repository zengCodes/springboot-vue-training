package com.zeng.mail.service.Impl;

import com.zeng.mail.entity.MailSendLog;
import com.zeng.mail.service.MailSendLogService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/5/2 0:44
 **/
@Service("MailSendLogService")
public class MailSendLogServiceImpl implements MailSendLogService {

    @Override
    public List<MailSendLog> getMailSendLogsByStatus(Integer delivering) {
        return null;
    }

    @Override
    public void updateMailSendLogStatus(String msgId, Integer failure) {

    }

    @Override
    public void updateCount(String msgId, Date date) {

    }

    @Override
    public String getMsgById(String msgId) {
        return null;
    }
}
