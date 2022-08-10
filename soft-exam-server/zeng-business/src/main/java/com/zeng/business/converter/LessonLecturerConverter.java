package com.zeng.business.converter;

import com.zeng.business.entity.LecturerApproval;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.mapper.LecturerApprovalMapper;
import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/25 23:11
 **/
@Component
@Data
public class LessonLecturerConverter {


    public static LessonLecturerConverter lessonLecturerConverter;

    public static SysUserMapper sysUserMapper;

    public static LecturerApprovalMapper lecturerApprovalMapper;


    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        lessonLecturerConverter.sysUserMapper = sysUserMapper;
    }

    @Autowired
    public void setLecturerApprovalMapper(LecturerApprovalMapper lecturerApprovalMapper) {
        lessonLecturerConverter.lecturerApprovalMapper = lecturerApprovalMapper;
    }

    /**
     * 转VOList
     *
     * @param lessonLecturers
     * @return
     */
    public static List<LessonLecturerVO> converterToVOList(List<LessonLecturer> lessonLecturers) {
        List<LessonLecturerVO> lessonLecturerVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(lessonLecturers)) {
            for (LessonLecturer lessonLecturer : lessonLecturers) {
                LessonLecturerVO lessonLecturerVO = converterToLessonLecturerVO(lessonLecturer);
                lessonLecturerVOS.add(lessonLecturerVO);
            }
        }
        return lessonLecturerVOS;
    }

    /**
     * 转VO
     *
     * @param lessonLecturer
     * @return
     */
    public static LessonLecturerVO converterToLessonLecturerVO(LessonLecturer lessonLecturer) {
        LessonLecturerVO lessonLecturerVO = new LessonLecturerVO();
        BeanUtils.copyProperties(lessonLecturer, lessonLecturerVO);
        SysUser sysUser = sysUserMapper.selectUserById(lessonLecturer.getLecturerUserNo());
        if (StringUtils.isNotNull(sysUser)) {
            lessonLecturerVO.setLecturerMobile(sysUser.getPhone());
        }
        lessonLecturerVO.setStatus(lessonLecturer.getStatus() == 1);
        // 查询审核状态
        LecturerApproval l = new LecturerApproval();
        l.setLecturer(lessonLecturer.getId());
        LecturerApproval lessonApproval = lecturerApprovalMapper.selectOne(l);
        if (StringUtils.isNotNull(lessonApproval)) {
            lessonLecturerVO.setApprovalStatus(lessonApproval.getApprovalStatus());
        }
        return lessonLecturerVO;
    }

}
