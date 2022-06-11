package com.zeng.business.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/11 22:33
 **/
@Data
@Table(name = "exam_paper")
public class Paper implements Serializable {
    private static final long serialVersionUID = -90135625811285961L;

    @Id
    private Long id;
    /**
     * 试卷名称
     */
    private String name;
    /**
     * 考试科目
     */
    private Long courseId;
    /**
     * 题目数量
     */
    private Integer questionCount;
    /**
     * 试卷总分
     */
    private Integer score;
    /**
     * 建议时间（分钟）
     */
    private Integer suggestTime;

    /**
     * 考试时间
     */
    private String time;

    /**
     * 创建人
     */
    private String user;
    /**
     * 试卷开放状态 0:不开放  1:开放
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifiedTime;

}
