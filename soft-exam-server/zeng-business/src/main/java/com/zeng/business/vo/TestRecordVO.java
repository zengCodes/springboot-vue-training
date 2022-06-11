package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/19 16:01
 **/
@Data
public class TestRecordVO {

    private Long id;

    private Long userId;

    private Long paperId;

    private Integer total;

    private String paperName;

    private String courseName;

    private String nickName;

    private Integer sex;

    private String email;

    private String phone;

    private Integer type;

    private Integer num;

    private Integer questionTotal;

    private Integer responseTotal;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;
}
