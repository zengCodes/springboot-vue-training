package com.zeng.business.service;


import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.entity.LessonApproval;
import com.zeng.business.vo.LessonApprovalVO;
import com.zeng.business.vo.PageVO;

public interface LessonApprovalService {
    int delete(Long id);

    int updateApprovalStatus(Long id, ApprovalDTO approvalDTO);

    PageVO<LessonApprovalVO> getLessonApprovalList(Integer pageNum, Integer pageSize, LessonApprovalVO lecturerApproval);
}
