package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamLecturerApproval)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-06 18:12:59
 */
@Data
@Table(name = "exam_lecturer_approval")
@Accessors(chain = true)
public class LecturerApproval implements Serializable {
    private static final long serialVersionUID = 824764762196059319L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 审核讲师
     */
    private Long lecturer;


    private String lecturerName;

    /**
     * 审核人
     */
    private Long approvalBy;
    /**
     * 审核意见
     */
    private String common;

    /**
     * 审核状态 0：待审核 2：不通过 1：通过
     */
    private Integer approvalStatus;

    /**
     * 审核时间
     */
    private Date approvalTime;

}
