package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/1 14:24
 **/
@Data
@Table(name = "exam_course")
@Accessors(chain = true)
public class Course implements Serializable {

    private static final long serialVersionUID = -40734620613632518L;
    /**
     * 科目编号
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    /**
     * 科目名称
     */
    private String name;

    /**
     * 科目考试时间
     */
    private Date time;
    /**
     * 备注
     */
    private String remark;

    /**
     * 科目封面
     */
    private String img;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifiedTime;
    /**
     * 一级分类id
     */
    private Long oneCategoryId;
    /**
     * 二级分类id
     */
    private Long twoCategoryId;
    /**
     * 三级分类id
     */
    private Long threeCategoryId;
    /**
     * 是否锁定：0锁定，1开放
     */
    private Integer status;

}
