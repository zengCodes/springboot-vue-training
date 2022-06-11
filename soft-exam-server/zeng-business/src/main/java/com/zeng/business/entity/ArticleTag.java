package com.zeng.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * (ExamArticleTag)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-31 21:24:52
 */
@Data
@Table(name = "exam_article_tag")
public class ArticleTag implements Serializable {
    private static final long serialVersionUID = -21799424062850862L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 标签id
     */
    private Long tagId;
}
