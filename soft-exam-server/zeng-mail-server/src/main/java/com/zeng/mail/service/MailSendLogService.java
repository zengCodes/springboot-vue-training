package com.zeng.mail.service;

import com.zeng.mail.entity.MailSendLog;

import java.util.Date;
import java.util.List;

public interface MailSendLogService {

    List<MailSendLog> getMailSendLogsByStatus(Integer delivering);

    void updateMailSendLogStatus(String msgId, Integer failure);

    void updateCount(String msgId, Date date);

    String getMsgById(String msgId);
}
