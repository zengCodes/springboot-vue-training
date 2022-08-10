package com.zeng.business.service;

import com.zeng.business.dto.PayDTO;

public interface PayLogService {

    int createNative(PayDTO payDTO);

    int queryPayStatus(String orderNum);
}
