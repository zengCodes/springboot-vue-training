package com.zeng.business.converter;

import com.zeng.business.entity.LecturerApproval;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.entity.Paper;
import com.zeng.business.mapper.LessonLecturerMapper;
import com.zeng.business.vo.LecturerApprovalVO;
import com.zeng.business.vo.PaperVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/6 21:52
 **/
@Component
@Data
public class LecturerApprovalConverter {

    public LecturerApprovalConverter lecturerApprovalConverter;


    public static SysUserMapper sysUserMapper;


    public static LessonLecturerMapper lessonLecturerMapper;

    @Autowired
    private void setSysUserMapper(SysUserMapper sysUserMapper) {
        lecturerApprovalConverter.sysUserMapper = sysUserMapper;
    }

    @Autowired
    private void setLessonLecturerMapper(LessonLecturerMapper lessonLecturerMapper) {
        lecturerApprovalConverter.lessonLecturerMapper = lessonLecturerMapper;
    }


    /**
     * 转voList
     *
     * @param lecturerApprovals
     * @return
     */
    public static List<LecturerApprovalVO> converterToVOList(List<LecturerApproval> lecturerApprovals) {
        List<LecturerApprovalVO> lecturerApprovalVOList = new ArrayList<>();
        if (!StringUtils.isEmpty(lecturerApprovals)) {
            for (LecturerApproval approval : lecturerApprovals) {
                LecturerApprovalVO lecturerApprovalVO = converterToLecturerApprovalVO(approval);
                lecturerApprovalVOList.add(lecturerApprovalVO);
            }
        }
        return lecturerApprovalVOList;
    }


    /***
     * 转VO
     * @param lecturerApproval
     * @return
     */
    public static LecturerApprovalVO converterToLecturerApprovalVO(LecturerApproval lecturerApproval) {
        LecturerApprovalVO approvalVO = new LecturerApprovalVO();
        BeanUtils.copyProperties(lecturerApproval, approvalVO);
        // 根据讲师id查询讲师
        LessonLecturer lecturer = new LessonLecturer();
        lecturer.setId(lecturerApproval.getLecturer());
        LessonLecturer lessonLecturer = lessonLecturerMapper.selectOne(lecturer);
        if (StringUtils.isNotNull(lessonLecturer)) {
            approvalVO.setLessonLecturer(LessonLecturerConverter.converterToLessonLecturerVO(lessonLecturer));
        }
        approvalVO.setReason(lessonLecturer.getReason());
        return approvalVO;
    }
}
