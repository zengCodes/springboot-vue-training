package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/30 15:19
 **/
@Data
public class OrderVO {

    @Id
    private String id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 课程名称
     */
    private String courseTitle;
    /**
     * 课程封面
     */
    private String courseCover;
    /**
     * 讲师名称
     */
    private String teacherName;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户账号
     */
    private String account;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户手机
     */
    private String mobile;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date payTime;

    private Integer type;
    /**
     * 订单金额（分）
     */
    private Double totalFee;
    /**
     * 订单状态（0：未支付 1：待支付 2:已支付）
     */
    private Integer status;
    /**
     * 逻辑删除 1（true）已删除， 0（false）未删除
     */
    private Integer isDeleted;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;
}
