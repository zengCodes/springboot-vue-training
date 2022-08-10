package com.zeng.business.converter;

import com.zeng.business.entity.*;
import com.zeng.business.mapper.*;
import com.zeng.business.vo.ChapterVO;
import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.business.vo.LessonTreeNode;
import com.zeng.business.vo.LessonVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/7 23:50
 **/
@Component
@Data
public class LessonConverter {

    public static LessonConverter lessonConverter;


    // 类型 讲师
    public static CategoryMapper categoryMapper;


    public static SysUserMapper sysUserMapper;


    public static LessonLecturerMapper lessonLecturerMapper;

    // 审核已通过
    public static LessonApprovalMapper lessonApprovalMapper;

    public static ChapterMapper chapterMapper;


    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        lessonConverter.sysUserMapper = sysUserMapper;
    }

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        lessonConverter.categoryMapper = categoryMapper;
    }

    @Autowired
    public void setLessonApprovalMapper(LessonApprovalMapper lessonApprovalMapper) {
        lessonConverter.lessonApprovalMapper = lessonApprovalMapper;
    }

    @Autowired
    public void setLessonLecturerMapper(LessonLecturerMapper lessonLecturerMapper) {
        lessonConverter.lessonLecturerMapper = lessonLecturerMapper;
    }

    @Autowired
    public void setChapterMapper(ChapterMapper chapterMapper) {
        lessonConverter.chapterMapper = chapterMapper;
    }

    /**
     * 转VOList
     *
     * @param lesson
     * @return
     */
    public static List<LessonVO> converterToVOList(List<Lesson> lesson) {
        List<LessonVO> lessonLecturerVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(lesson)) {
            for (Lesson l : lesson) {
                // 返回已经审核完成的
                LessonApproval lessonApproval = new LessonApproval();
                lessonApproval.setLesson(l.getId());
                LessonVO lessonVO = converterToLessonVO(l);
                lessonLecturerVOS.add(lessonVO);
            }
        }
        return lessonLecturerVOS;
    }

    /**
     * 转VO
     *
     * @param lesson
     * @return
     */
    public static LessonVO converterToLessonVO(Lesson lesson) {
        LessonVO lessonVO = new LessonVO();
        BeanUtils.copyProperties(lesson, lessonVO);
        // 查询用户真实名称
        LessonLecturer lessonLecturer = new LessonLecturer();
        if (StringUtils.isNotNull(lesson.getUserNo())) {
            lessonLecturer = lessonLecturerMapper.selectByPrimaryKey(lesson.getUserNo());
        }
        if (StringUtils.isNotNull(lessonLecturer)) {
            SysUser sysUser = new SysUser();
            sysUser = sysUserMapper.selectUserById(lessonLecturer.getLecturerUserNo());
            if (StringUtils.isNotNull(sysUser)) {
                lessonVO.setNickName(sysUser.getNickName());
            }
        }
        if (StringUtils.isNotNull(lesson.getType())) {
            // 查询类型
            Category category = new Category();
            category.setId(lesson.getType());
            category = categoryMapper.selectOne(category);
            if (StringUtils.isNotNull(category)) {
                lessonVO.setTypeName(category.getName());
            }
        }
        // 如果课程已结束
        Long start = lessonVO.getEndTime().getTime();
        Long end = new Date().getTime();
        if (start - end < 0) {
            lessonVO.setStatus(2);
        }
        lessonVO.setIsPutAway(lesson.getIsPutAway() == 1);
        Double d = lesson.getOriginal().multiply(lesson.getDiscount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        lessonVO.setTotal(d);
        lessonVO.setDiscountNum(lesson.getOriginal().subtract(new BigDecimal(d)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        // 查询审核状态
        LessonApproval l = new LessonApproval();
        l.setLesson(lesson.getId());
        LessonApproval lessonApproval = lessonApprovalMapper.selectOne(l);
        if (StringUtils.isNotNull(lessonApproval)) {
            lessonVO.setApprovalStatus(lessonApproval.getApprovalStatus());
        }
        return lessonVO;
    }


    public static List<LessonTreeNode> converterToTreeList(List<LessonVO> lessonVOS) {
        List<LessonTreeNode> lessonTreeNodes = new ArrayList<>();
        for (LessonVO lessonVO : lessonVOS) {
            LessonTreeNode lessonTreeNode = new LessonTreeNode();
            BeanUtils.copyProperties(lessonVO, lessonTreeNode);
            lessonTreeNode.setId(lessonVO.getId());
            lessonTreeNode.setParentId((long) 0);
            lessonTreeNode.setTitle(lessonVO.getName());
            lessonTreeNode.setSort(lessonVO.getSort());
            lessonTreeNode.setCreateTime(lessonVO.getCreateTime());
            lessonTreeNode.setModifiedTime(lessonVO.getModifiedTime());
            lessonTreeNodes.add(lessonTreeNode);
        }
        return lessonTreeNodes;
    }

}
