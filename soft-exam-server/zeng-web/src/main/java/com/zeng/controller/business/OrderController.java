package com.zeng.controller.business;

import com.zeng.business.dto.OrderDTO;
import com.zeng.business.service.OrderService;
import com.zeng.business.vo.OrderDetailVO;
import com.zeng.business.vo.OrderVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.domain.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/30 0:13
 **/
@Api(tags = "业务模块-试卷管理接口")
@RestController
@RequestMapping("business/order")
@Slf4j
public class OrderController {


    @Resource
    private OrderService orderService;


    @ApiOperation(value = "获取订单列表")
    @GetMapping("/getOrderList")
    public ResponseBean getOrderList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize") Integer pageSize,
                                     OrderVO orderVO) {
        PageVO<OrderVO> orderList = orderService.getOrderList(pageNum, pageSize, orderVO);
        return ResponseBean.success(orderList);
    }


    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "创建订单")
    @PostMapping("/createOrder")
    public ResponseBean createOrder(@RequestBody @Validated OrderDTO orderDTO) {
        String str = orderService.createOrder(orderDTO);
        return ResponseBean.success(str);
    }


    /**
     * 根据订单id订单信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取订单信息", notes = "获取订单信息")
    @GetMapping("/getOrderInfo/{id}")
    public ResponseBean getOrderInfo(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderService.getOrderInfo(id);
        return ResponseBean.success(orderDetailVO);
    }


    @ApiOperation(value = "获取订单状态", notes = "获取订单状态")
    @GetMapping("/getOrderStatus/{courseId}/{memberId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "memberId", value = "用户ID", required = true, dataTypeClass = Long.class),
    })
    public ResponseBean getOrderInfoByNo(@PathVariable Long courseId, @PathVariable Long memberId) {
        boolean b = orderService.getOrderStatus(courseId, memberId);
        return ResponseBean.success(b);
    }


    @ApiOperation(value = "获取订单详细信息", notes = "获取订单详细信息")
    @GetMapping("/getOrderInfoByNo/{orderNo}")
    public ResponseBean getOrderInfoByNo(@PathVariable String orderNo) {
        OrderVO orderVO = orderService.getOrderInfoByNo(orderNo);
        return ResponseBean.success(orderVO);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除订单", notes = "删除订单信息")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        int i = orderService.delete(id);
        return ResponseBean.success(i);
    }


}
