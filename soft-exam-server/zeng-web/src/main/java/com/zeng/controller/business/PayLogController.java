package com.zeng.controller.business;

import com.zeng.business.dto.OrderDTO;
import com.zeng.business.dto.PayDTO;
import com.zeng.business.mapper.PayLogMapper;
import com.zeng.business.service.PayLogService;
import com.zeng.common.core.domain.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/30 0:12
 **/
@Api(tags = "业务模块-试卷管理接口")
@RestController
@RequestMapping("business/order")
@Slf4j
public class PayLogController {


    @Resource
    private PayLogService payLogService;

    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "创建订单")
    @PostMapping("/pay")
    public ResponseBean createOrder(@RequestBody @Validated PayDTO payDTO) {
        int n = payLogService.createNative(payDTO);
        return ResponseBean.success(n);
    }


    @ApiOperation(value = "查询订单状态")
    @GetMapping("/queryPayStatus")
    public ResponseBean queryPayStatus(@RequestParam(value = "orderNum") String orderNum) {
        int n = payLogService.queryPayStatus(orderNum);
        return ResponseBean.success(n);
    }

}
