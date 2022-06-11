package com.zeng.business.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamComments)实体类
 *
 * @author zenghuiqing
 * @since 2022-01-04 00:23:38
 */
@Data
@Table(name = "exam_comments")
public class Comments implements Serializable {
    private static final long serialVersionUID = 129511060705841283L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 讨论话题
     */
    private Long discuss;
    /**
     * 图片
     */
    private String img;
    /**
     * 评论内容
     */
    private String comments;
    /**
     * 点赞数
     */
    private Integer likeNum;
    /**
     * 创建时间
     */
    private Date createTime;

}
