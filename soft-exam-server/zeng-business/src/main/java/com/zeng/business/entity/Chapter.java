package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamChapter)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-25 16:02:51
 */
@Data
@Table(name = "exam_chapter")
@Accessors(chain = true)
public class Chapter implements Serializable {
    private static final long serialVersionUID = -78614791334413798L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 课程id
     */
    private Long lesson;
    /**
     * 章节名称
     */
    private String title;
    /**
     * 排序
     */
    private Integer sort;

    /**
     * 封面
     */
    private String coverImg;
    /**
     * 视频资源
     */
    private String video;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;

}
