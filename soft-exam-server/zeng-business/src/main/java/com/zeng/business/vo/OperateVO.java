package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/8 13:25
 **/
@Data
public class OperateVO {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date time;

    private String info;

}
