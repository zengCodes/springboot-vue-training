package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zeng.business.entity.Lesson;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/9 21:36
 **/
@Data
public class LessonApprovalVO {

    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 审核状态 1通过 0不通过
     */
    private Integer approvalStatus;
    /**
     * 审核课程
     */
    private LessonVO lesson;
    /**
     * 审核人
     */
    private String approvalBy;
    /**
     * 审核意见
     */
    private String common;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date approvalTime;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;

}
