package com.zeng.business.converter;

import com.zeng.business.entity.Lesson;
import com.zeng.business.entity.LessonApproval;
import com.zeng.business.mapper.LessonMapper;
import com.zeng.business.vo.LessonApprovalVO;
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
 * @Date create in 2021/12/9 21:41
 **/
@Component
@Data
public class LessonApprovalConverter {


    public static LessonApprovalConverter lessonApprovalConverter;

    private static LessonMapper lessonMapper;

    private static SysUserMapper sysUserMapper;

    @Autowired
    private void setLessonMapper(LessonMapper lessonMapper) {
        lessonApprovalConverter.lessonMapper = lessonMapper;
    }

    @Autowired
    private void setSysUserMapper(SysUserMapper sysUserMapper) {
        lessonApprovalConverter.sysUserMapper = sysUserMapper;
    }


    /**
     * 转voList
     *
     * @param lessonApprovals
     * @return
     */
    public static List<LessonApprovalVO> converterToVOList(List<LessonApproval> lessonApprovals) {
        List<LessonApprovalVO> lessonApprovalVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(lessonApprovals)) {
            for (LessonApproval approval : lessonApprovals) {
                LessonApprovalVO lessonApprovalVO = converterToLessonApprovalVO(approval);
                lessonApprovalVOS.add(lessonApprovalVO);
            }
        }
        return lessonApprovalVOS;
    }


    /***
     * 转VO
     * @param lessonApproval
     * @return
     */
    public static LessonApprovalVO converterToLessonApprovalVO(LessonApproval lessonApproval) {
        LessonApprovalVO approvalVO = new LessonApprovalVO();
        BeanUtils.copyProperties(lessonApproval, approvalVO);
        // 查询课程信息
        Lesson l = lessonMapper.selectByPrimaryKey(lessonApproval.getLesson());
        SysUser sysUser = sysUserMapper.selectUserById(lessonApproval.getApprovalBy());
        if (StringUtils.isNotNull(l)) {
            approvalVO.setLesson(LessonConverter.converterToLessonVO(l));
        }
        if (StringUtils.isNotNull(sysUser)) {
            approvalVO.setApprovalBy(sysUser.getNickName());
        }
        return approvalVO;
    }

}
