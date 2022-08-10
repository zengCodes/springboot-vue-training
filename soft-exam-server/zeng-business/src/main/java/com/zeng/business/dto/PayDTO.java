package com.zeng.business.dto;

import lombok.Data;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/8 17:57
 **/
@Data
public class PayDTO {

    private String orderNum;

    private Integer type;

    private Double total;

}
