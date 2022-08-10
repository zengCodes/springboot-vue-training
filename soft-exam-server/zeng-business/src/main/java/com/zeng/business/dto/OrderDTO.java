package com.zeng.business.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/30 13:59
 **/
@Data
@ApiModel(value = "订单创建内容")
public class OrderDTO {

    private Long lesson;

    private Long user;

    private String phone;

    private Integer type;

    private Double pay;
}
