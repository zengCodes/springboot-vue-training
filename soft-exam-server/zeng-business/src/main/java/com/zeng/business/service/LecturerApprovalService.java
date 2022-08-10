package com.zeng.business.service;

import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.entity.LecturerApproval;
import com.zeng.business.entity.LessonApproval;
import com.zeng.business.vo.LecturerApprovalVO;
import com.zeng.business.vo.PageVO;

public interface LecturerApprovalService {

    /**
     * 讲师审核列表
     *
     * @param pageNum
     * @param pageSize
     * @param lecturerApprovalVO
     * @return
     */
    PageVO<LecturerApprovalVO> getLecturerApprovalList(Integer pageNum, Integer pageSize, LecturerApprovalVO lecturerApprovalVO);

    /**
     * 审核
     *
     * @param id
     * @param approvalDTO
     * @return
     */
    int updateApprovalStatus(Long id, ApprovalDTO approvalDTO);

}
