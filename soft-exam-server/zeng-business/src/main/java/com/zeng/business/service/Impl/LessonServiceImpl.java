package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.CourseConverter;
import com.zeng.business.converter.LessonConverter;
import com.zeng.business.entity.*;
import com.zeng.business.mapper.CategoryMapper;
import com.zeng.business.mapper.LessonApprovalMapper;
import com.zeng.business.mapper.LessonLecturerMapper;
import com.zeng.business.mapper.LessonMapper;
import com.zeng.business.service.LessonService;
import com.zeng.business.vo.*;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/25 22:50
 **/
@Service("LessonService")
public class LessonServiceImpl implements LessonService {


    @Resource
    private LessonMapper lessonMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private LessonLecturerMapper lessonLecturerMapper;

    @Resource
    private LessonApprovalMapper lessonApprovalMapper;


    @Override
    public PageVO<LessonVO> getLessonList(Integer pageNum, Integer pageSize, LessonVO lessonVO) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(Lesson.class);
        Example.Criteria criteria = o.createCriteria();
        // 根据课程名称查询
        if (StringUtils.isNotEmpty(lessonVO.getName())) {
            criteria.andEqualTo("name", lessonVO.getName());
        }
        // 课程类型查询
        if (StringUtils.isNotNull(lessonVO.getType())) {
            criteria.andEqualTo("type", lessonVO.getType());
        }
        // 上下架状态查询
        if (StringUtils.isNotNull(lessonVO.getIsPutAway())) {
            criteria.andEqualTo("isPutAway", lessonVO.getIsPutAway());
        }
        // 是否启用或者禁用状态
        if (StringUtils.isNotNull(lessonVO.getStatus())) {
            criteria.andEqualTo("status", lessonVO.getStatus());
        }
        List<Lesson> lessons = lessonMapper.selectByExample(o);
        List<LessonVO> lessonVOS = LessonConverter.converterToVOList(lessons);
        PageInfo<Lesson> lessonPageInfo = new PageInfo<>(lessons);
        return new PageVO<>(lessonPageInfo.getTotal(), lessonVOS);
    }

    /**
     * 前台用户展示---分类树形结构
     *
     * @return
     */
    @Override
    public List<LessonTreeNodeVO> lessonTree(Boolean status) {
        List<LessonTreeNodeVO> lessonTreeNodeVOS = new ArrayList<>();
        // 查询所有课程分类
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("type", "lesson");
        List<Category> categories = categoryMapper.selectByExample(example);
        for (Category category : categories) {
            LessonTreeNodeVO lessonTreeNodeVO = new LessonTreeNodeVO();
            // 根据分类,查询该分类下的所有课程，插入child
            Example o = new Example(Lesson.class);
            Example.Criteria criteria = o.createCriteria();
            if (StringUtils.isNotNull(category.getId())) {
                criteria.andEqualTo("type", category.getId());
            }
            // 根据状态
            if(StringUtils.isNotNull(status)){
                criteria.andEqualTo("isPutAway", status?CommonStatusEnum.AVAILABLE.getStatusCode():CommonStatusEnum.DISABLE.getStatusCode());
            }
            List<Lesson> lessons = lessonMapper.selectByExample(o);
            lessonTreeNodeVO.setName(category.getName());
            lessonTreeNodeVO.setChildren(lessons);
            lessonTreeNodeVO.setTypeId(category.getId());
            lessonTreeNodeVO.setSort(category.getSort());
            lessonTreeNodeVOS.add(lessonTreeNodeVO);
        }
        // 返回树
        return lessonTreeNodeVOS;
    }

    @Override
    public LessonVO edit(Long id) {
        Lesson lesson = lessonMapper.selectByPrimaryKey(id);
        return LessonConverter.converterToLessonVO(lesson);
    }

    @Override
    public int saveLessonStudy(Long id) {
        // 查询是否第一次进来
        // 查询记录
        Lesson lesson = lessonMapper.selectByPrimaryKey(id);
        Integer num = lesson.getStudyCount();
        lesson.setStudyCount(num + 1);
        int i = lessonMapper.updateByPrimaryKeySelective(lesson);
        return i;
    }

    @Override
    public LessonVO getLessonInfo(Long id) {
        Lesson lesson = lessonMapper.selectByPrimaryKey(id);
        return LessonConverter.converterToLessonVO(lesson);
    }

    @Override
    public int add(LessonVO lessonVO) {
        Lesson lesson = new Lesson();
        BeanUtils.copyProperties(lessonVO, lesson);
        // 先查询是否成为了讲师
        LessonLecturer lessonLecturer = new LessonLecturer();
        lessonLecturer.setLecturerUserNo(SecurityUtils.getUserId());
        if (StringUtils.isNull(lessonLecturerMapper.selectOne(lessonLecturer))) {
            return 0;
        } else {
            lesson.setUserNo(lessonLecturerMapper.selectOne(lessonLecturer).getId());
        }
        lesson.setIsPutAway(CommonStatusEnum.DISABLE.getStatusCode())
                .setStatus(CommonStatusEnum.DISABLE.getStatusCode())
                .setCreateTime(new Date())
                .setStudyCount(CommonStatusEnum.DISABLE.getStatusCode())
                .setBuyCount(CommonStatusEnum.DISABLE.getStatusCode());
        int n = lessonMapper.insert(lesson);
        // 保存到审核表 新增记录默认待审核
        LessonApproval lessonApproval = new LessonApproval();
        lessonApproval.setLesson(lesson.getId())
                .setApprovalStatus(CommonStatusEnum.DISABLE.getStatusCode())
                .setApprovalTime(new Date());
        lessonApprovalMapper.insert(lessonApproval);
        return n;
    }

    @Override
    public int update(Long id, LessonVO lessonVO) {
        Lesson lesson = new Lesson();
        BeanUtils.copyProperties(lessonVO, lesson);
        if (lessonVO.getLogo().indexOf("dev-api") > 0) {
            lesson.setLogo(lessonVO.getLogo().replaceAll("/dev-api", ""));
        }
        lesson.setIsPutAway(lessonVO.getIsPutAway() ? CommonStatusEnum.AVAILABLE.getStatusCode() : CommonStatusEnum.DISABLE.getStatusCode())
                .setId(id)
                .setModifiedTime(new Date());
        int n = lessonMapper.updateByPrimaryKey(lesson);
        return n;
    }

    @Override
    public int delete(Long id) {
        Lesson lesson = lessonMapper.selectByPrimaryKey(id);
        if (lesson.getIsPutAway() != 1) {
            throw new CustomException("课程上架中，不可删");
        } else {
            return lessonMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public int updateStatus(Long id, Boolean status) {
        Lesson lesson = lessonMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(lesson)) {
            throw new CustomException("要更新状态的课程不存在");
        } else {
            lesson.setIsPutAway(status ? CommonStatusEnum.AVAILABLE.getStatusCode() :
                    CommonStatusEnum.DISABLE.getStatusCode());
            int n = lessonMapper.updateByPrimaryKey(lesson);
            return n;
        }
    }


    @Override
    public int updateLessonStatus(Long id, Integer status) {
        Lesson lesson = lessonMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(lesson)) {
            throw new CustomException("要更新状态的课程不存在");
        } else {
            lesson.setStatus(status);
            lesson.setModifiedTime(new Date());
            int n = lessonMapper.updateByPrimaryKey(lesson);
            return n;
        }
    }


}
