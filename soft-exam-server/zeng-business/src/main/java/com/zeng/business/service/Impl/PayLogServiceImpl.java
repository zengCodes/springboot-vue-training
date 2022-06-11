package com.zeng.business.service.Impl;

import com.zeng.business.dto.PayDTO;
import com.zeng.business.entity.Order;
import com.zeng.business.entity.PayLog;
import com.zeng.business.mapper.OrderMapper;
import com.zeng.business.mapper.PayLogMapper;
import com.zeng.business.service.PayLogService;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/29 15:15
 **/
@Service("PayLogService")
public class PayLogServiceImpl implements PayLogService {

    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private OrderMapper orderMapper;

    //生成微信支付二维码
    @Override
    public int createNative(PayDTO payDTO) {
        // 改变order状态
        Order order = orderMapper.selectOne(new Order().setOrderNo(payDTO.getOrderNum()));
        order.setModifiedTime(new Date());
        order.setStatus(CommonStatusEnum.AVAILABLE.getStatusCode());
        orderMapper.updateByPrimaryKey(order);
        // 保存到支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(payDTO.getOrderNum());
        payLog.setCreateTime(new Date());
        payLog.setIsDeleted(CommonStatusEnum.DISABLE.getStatusCode());
        payLog.setTotalFee(payDTO.getTotal());
        payLog.setPayType(payDTO.getType());
        payLog.setPayTime(new Date());
        payLog.setTradeState(CommonStatusEnum.AVAILABLE.getStatusCode());
        payLog.setTransactionId("1111");
        int i = payLogMapper.insert(payLog);
        return i;
    }

    /**
     * 根据订单号查询订单状态
     *
     * @param orderNum
     */
    @Override
    public int queryPayStatus(String orderNum) {
        Order order = orderMapper.selectOne(new Order().setOrderNo(orderNum).setUserId(SecurityUtils.getUserId()));
        return order.getStatus();
    }


}
