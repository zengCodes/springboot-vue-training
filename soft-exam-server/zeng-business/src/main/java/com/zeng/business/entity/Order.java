package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单(ExamOrder)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-29 15:09:11
 */
@Data
@Table(name = "exam_order")
@Accessors(chain = true)
public class Order implements Serializable {
    private static final long serialVersionUID = 764175404547953423L;

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
    /**
     * 订单金额（分）
     */
    private Double totalFee;
    /**
     * 订单状态（0：待支付  1:已支付 2：已取消）
     */
    private Integer status;
    /**
     * 逻辑删除 1（true）已删除， 0（false）未删除
     */
    private Integer isDeleted;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifiedTime;
}
