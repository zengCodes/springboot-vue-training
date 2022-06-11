package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 支付日志表(ExamPayLog)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-29 15:09:25
 */
@Data
@Table(name = "exam_pay_log")
@Accessors(chain = true)
public class PayLog implements Serializable {
    private static final long serialVersionUID = 842465788264562999L;

    @Id
    private String id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 支付完成时间
     */
    private Date payTime;
    /**
     * 支付金额（分）
     */
    private Double totalFee;
    /**
     * 交易流水号
     */
    private String transactionId;
    /**
     * 交易状态
     */
    private Integer tradeState;
    /**
     * 支付类型（1：微信 2：支付宝）
     */
    private Integer payType;
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
