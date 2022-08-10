package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;


import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/25 16:45
 **/

@Table(name = "exam_lesson_lecturer")
@Data
@Accessors(chain = true)
public class LessonLecturer implements Serializable {

    private static final long serialVersionUID = 756471205012639861L;

    /**
     * 自增id
     */

    @GeneratedValue(generator = "JDBC")
    @Id
    private Long id;
    /**
     * 讲师编号
     */
    private Long lecturerUserNo;
    /**
     * 简介
     */
    private String introduce;

    /**
     * 封面
     */
    private String coverImg;

    /**
     * 申请备注
     */
    private String reason;
    /**
     * 分成比例
     */
    private BigDecimal lecturerProportion;
    /**
     * 禁用状态
     */
    private Integer status;

    private String lecturerName;


    private String lecturerEmail;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;

}
