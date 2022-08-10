package com.zeng.business.converter;

import com.zeng.business.entity.Order;
import com.zeng.business.entity.Paper;
import com.zeng.business.vo.OrderVO;
import com.zeng.business.vo.PaperVO;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/7 19:27
 **/
@Component
public class OrderConverter {

    /**
     * 转voList
     *
     * @param orders
     * @return
     */
    public static List<OrderVO> converterToVOList(List<Order> orders) {
        List<OrderVO> orderVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(orders)) {
            for (Order order : orders) {
                if (!order.getIsDeleted().toString().equals("1")) {
                    OrderVO orderVO = converterToOrderVO(order);
                    orderVOS.add(orderVO);
                }
            }
        }
        return orderVOS;
    }


    /***
     * 转VO
     * @param order
     * @return
     */
    public static OrderVO converterToOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        return orderVO;
    }
}
