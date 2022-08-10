package com.zeng.business.vo;

import lombok.Data;

import javax.persistence.Id;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/6 18:30
 **/
@Data
public class LecturerApprovalVO {
    /**
     * 自增id
     */
    @Id
    private Long id;


    private String email;

    private String lecturerName;

    private LessonLecturerVO lessonLecturer;
    /**
     * 申请原因
     */
    private String reason;
    /**
     * 审核意见
     */
    private String common;

    /**
     * 审核状态 0：待审核 1：通过 2:不通过
     */
    private Integer approvalStatus;
}
