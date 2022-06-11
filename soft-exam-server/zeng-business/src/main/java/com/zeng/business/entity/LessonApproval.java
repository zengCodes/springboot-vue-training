package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamLessonApproval)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-09 17:45:04
 */

@Data
@Table(name = "exam_lesson_approval")
@Accessors(chain = true)
public class LessonApproval implements Serializable {
    private static final long serialVersionUID = 171170757677232553L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 审核状态 0：待审核 2：不通过 1：通过
     */
    private Integer approvalStatus;
    /**
     * 审核课程
     */
    private Long lesson;

    /**
     * 审核人
     */
    private Long approvalBy;
    /**
     * 审核意见
     */
    private String common;
    /**
     * 创建时间
     */
    private Date approvalTime;
    /**
     * 更新时间
     */
    private Date modifiedTime;

}
