package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/25 23:04
 **/
@Data
@Accessors(chain = true)
public class LessonLecturerVO {


    @Id
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;
    /**
     * 状态(true:正常，false:禁用)
     */
    private Boolean status;


    private Integer approvalStatus;

    /**
     * 讲师用户编号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lecturerUserNo;
    /**
     * 讲师名称
     */
    private String lecturerName;
    /**
     * 讲师手机
     */
    private String lecturerMobile;
    /**
     * 讲师邮箱
     */
    private String lecturerEmail;
    /**
     * 封面
     */
    private String coverImg;
    /**
     * 简介
     */
    private String introduce;
    /**
     * 申请理由
     */
    private String reason;
    /**
     * 讲师分成比例
     */
    private BigDecimal lecturerProportion;

}
