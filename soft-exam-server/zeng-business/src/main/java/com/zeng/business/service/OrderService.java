package com.zeng.business.service;

import com.zeng.business.dto.OrderDTO;
import com.zeng.business.vo.OrderDetailVO;
import com.zeng.business.vo.OrderVO;
import com.zeng.business.vo.PageVO;

import java.util.List;

public interface OrderService {

    String createOrder(OrderDTO order);

    OrderDetailVO getOrderInfo(Long id);

    boolean getOrderStatus(Long courseId, Long memberId);

    PageVO<OrderVO> getOrderList(Integer pageNum, Integer pageSize, OrderVO orderVO);

    int delete(Long id);

    OrderVO getOrderInfoByNo(String orderNo);
}
