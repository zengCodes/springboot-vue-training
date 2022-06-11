package com.zeng.business.service.Impl;

import com.zeng.business.converter.ChapterConverter;
import com.zeng.business.converter.LessonConverter;
import com.zeng.business.entity.Chapter;
import com.zeng.business.entity.Lesson;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.mapper.ChapterMapper;
import com.zeng.business.mapper.LessonLecturerMapper;
import com.zeng.business.mapper.LessonMapper;
import com.zeng.business.service.ChapterService;
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
 * @Date create in 2021/12/25 16:45
 **/
@Service("ChapterService")
public class ChapterServiceImpl implements ChapterService {


    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private LessonMapper lessonMapper;


    @Resource
    private LessonLecturerMapper lessonLecturerMapper;


    @Override
    public List<LessonTreeNode> getChapterTreeList() {
        // 查询所有课程
        List<LessonVO> lessonVOS = LessonConverter.converterToVOList(lessonMapper.selectAll());
        List<LessonTreeNode> nodeVOS = LessonConverter.converterToTreeList(lessonVOS);
        for (LessonTreeNode lessonTreeNode : nodeVOS) {
            // 查询该课程下的所有章节
            Example o = new Example(Chapter.class);
            Example.Criteria criteria = o.createCriteria();
            criteria.andEqualTo("lesson", lessonTreeNode.getId());
            o.setOrderByClause("sort asc");
            try {
                List<Chapter> chapters = chapterMapper.selectByExample(o);
                if (StringUtils.isNotNull(chapters)) {
                    lessonTreeNode.setChildren(ChapterConverter.converterToTreeList(chapters));
                }
            } catch (Exception e) {

            }
        }
        return nodeVOS;
    }

    @Override
    public List<ChapterVO> getLessonChapter(Integer lesson) {
        List<ChapterVO> chapterVOS = new ArrayList<>();
        Example o = new Example(Chapter.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotNull(lesson)) {
            criteria.andEqualTo("lesson", lesson);
        }
        o.setOrderByClause("sort asc");
        chapterVOS = ChapterConverter.converterToVOList(chapterMapper.selectByExample(o));
        return chapterVOS;
    }

    @Override
    public ChapterVO edit(Long id) {
        Chapter chapter = chapterMapper.selectByPrimaryKey(id);
        return ChapterConverter.converterToChapterVO(chapter);
    }

    @Override
    public int add(ChapterVO chapterVO) {
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(chapterVO, chapter);
        // 查询课程 的讲师编号
        Long lecturer = null;
        Lesson lesson = lessonMapper.selectByPrimaryKey(chapter.getLesson());
        if (StringUtils.isNotNull(lesson)) {
            lecturer = lesson.getUserNo();
        }
        // 查询用户编号
        Long userId = null;
        if (StringUtils.isNotNull(lecturer)) {
            LessonLecturer lessonLecturer = lessonLecturerMapper.selectByPrimaryKey(lecturer);
            if (StringUtils.isNotNull(lessonLecturer)) {
                userId = lessonLecturer.getLecturerUserNo();
            }
        }
        // 如果该用户不是该课程的讲师，返回0
        if (userId.equals(SecurityUtils.getUserId())) {
            chapter.setCreateTime(new Date());
            return chapterMapper.insert(chapter);
        } else {
            return 0;
        }
    }

    @Override
    public int update(Long id, ChapterVO chapterVO) {
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(chapterVO, chapter);
        if (chapterVO.getVideo().indexOf("dev-api") > 0) {
            chapter.setVideo(chapterVO.getVideo().replaceAll("/dev-api", ""));
        }
        chapter.setId(id)
                .setStatus(chapterVO.getStatus())
                .setModifiedTime(new Date());
        int n = chapterMapper.updateByPrimaryKey(chapter);
        return n;
    }

    @Override
    public int delete(Long id) {
        Chapter chapter = chapterMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(chapter)) {
            throw new CustomException("章节不存在！");
        } else {
            return chapterMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public int updateChapterStatus(Long id, Boolean status) {
        Chapter chapter = chapterMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(chapter)) {
            throw new CustomException("要更新状态的章节不存在");
        } else {
            chapter.setStatus(status ? CommonStatusEnum.AVAILABLE.getStatusCode() :
                    CommonStatusEnum.DISABLE.getStatusCode());
            int n = chapterMapper.updateByPrimaryKey(chapter);
            return n;
        }
    }
}
