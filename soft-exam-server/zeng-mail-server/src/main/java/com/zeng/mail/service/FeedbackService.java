package com.zeng.mail.service;

import com.zeng.mail.entity.Feedback;

public interface FeedbackService {

    int sendMessage(Feedback feedback);
}
