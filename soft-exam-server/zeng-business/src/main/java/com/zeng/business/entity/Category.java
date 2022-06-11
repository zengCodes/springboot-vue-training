package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamCourse)实体类
 *
 * @author zenghuiqing
 * @since 2021-05-31 13:31:59
 */
@Data
@Table(name = "exam_category")
@Accessors(chain = true)
public class Category implements Serializable {

    private static final long serialVersionUID = -89128117321207803L;
    /**
     * 分类编号
     */
    @Id
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 是否锁定：1锁定，0开放
     */
    private Integer status;
    /**
     * 父级id
     */
    private Long parentId;
    /**
     * 分类类型
     */
    private String type;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifiedTime;

}
