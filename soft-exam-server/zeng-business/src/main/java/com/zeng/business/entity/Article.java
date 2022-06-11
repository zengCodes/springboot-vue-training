package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/12 21:06
 **/
@Data
@Table(name = "exam_article")
@Accessors(chain = true)
public class Article implements Serializable {
    private static final long serialVersionUID = -69516361744266698L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 文章缩略图
     */
    private String articleCover;
    /**
     * 是否删除
     */
    private Integer isDelete;
    /**
     * 是否置顶
     */
    private Integer isTop;

    private Integer sort;

    /**
     * 分类id
     */
    private Long categoryId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}
