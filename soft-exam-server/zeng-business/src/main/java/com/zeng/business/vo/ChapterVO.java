package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zeng.business.entity.Lesson;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/25 16:06
 **/
@Data
public class ChapterVO {

    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 课程
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
     * 视频资源
     */
    private String video;

    private String coverImg;

    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;

}
