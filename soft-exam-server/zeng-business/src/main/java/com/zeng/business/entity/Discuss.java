package com.zeng.business.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/4 0:24
 **/
@Data
@Table(name = "exam_discuss")
public class Discuss implements Serializable {
    private static final long serialVersionUID = 808302457021274175L;
    /**
     * 自增id
     */
    @Id
    private Long id;

    /**
     * 讨论者
     */
    private Long parentId;

    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 标题
     */
    private String title;
    /**
     * 浏览量
     */
    private Integer browseNum;
    /**
     * 点赞数
     */
    private Integer likeNum;
    /**
     * 讨论问题
     */
    private Long question;

    /**
     * 问题类型
     */
    private Long type;
    /**
     * 发表内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;
}
