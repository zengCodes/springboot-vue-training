package com.zeng.business.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * (ExamLesson)实体类
 *
 * @author zenghuiqing
 * @since 2021-11-22 16:27:54
 */
@Data
@Table(name = "exam_lesson")
@Accessors(chain = true)
public class Lesson implements Serializable {
    private static final long serialVersionUID = -40748987382607665L;
    /**
     * 主键
     */

    @GeneratedValue(generator = "JDBC")
    @Id
    private Long id;

    /**
     * 课程类型
     */
    @ApiModelProperty(value = "课程类型ID")
    private Long type;
    /**
     * 状态(1:正常，0:禁用)
     */
    @ApiModelProperty(value = "状态(1:正常，0:禁用)")
    private Integer status;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 讲师用户编码
     */
    @ApiModelProperty(value = "讲师用户编码")
    private Long userNo;
    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String name;
    /**
     * 课程封面
     */
    @ApiModelProperty(value = "课程封面")
    private String logo;
    /**
     * 课程介绍
     */
    @ApiModelProperty(value = "课程介绍")
    private String introduce;
    /**
     * 是否免费：1免费，0收费
     */
    @ApiModelProperty(value = "是否免费：1免费，0收费")
    private Integer isFree;
    /**
     * 原价
     */
    @ApiModelProperty(value = "原价")
    private BigDecimal original;
    /**
     * 优惠折扣
     */
    @ApiModelProperty(value = "优惠折扣")
    private BigDecimal discount;
    /**
     * 购买人数
     */
    @ApiModelProperty(value = "购买人数")
    private Integer buyCount;
    /**
     * 学习人数
     */
    @ApiModelProperty(value = "学习人数")
    private Integer studyCount;

    /**
     * 是否上架(1:上架，0:下架)
     */
    @ApiModelProperty(value = "是否上架(1:上架，0:下架)")
    private Integer isPutAway;
    /**
     * 总课时数
     */
    @ApiModelProperty(value = "总课时数")
    private Integer periodTotal;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date modifiedTime;


    /**
     * 授课时间
     */
    @ApiModelProperty(value = "授课时间")
    private Date teachTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
