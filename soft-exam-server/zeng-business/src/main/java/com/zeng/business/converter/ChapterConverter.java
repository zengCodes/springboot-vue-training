package com.zeng.business.converter;

import com.zeng.business.entity.Chapter;
import com.zeng.business.mapper.LessonMapper;
import com.zeng.business.vo.ChapterVO;
import com.zeng.business.vo.LessonTreeNode;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/25 18:00
 **/
@Component
public class ChapterConverter {

    public static ChapterConverter chapterConverter;

    public static LessonMapper lessonMapper;

    @Autowired
    public void setLessonMapper(LessonMapper lessonMapper) {
        chapterConverter.lessonMapper = lessonMapper;
    }

    /**
     * 转voList
     *
     * @param chapters
     * @return
     */
    public static List<ChapterVO> converterToVOList(List<Chapter> chapters) {
        List<ChapterVO> chapterVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(chapters)) {
            for (Chapter chapter : chapters) {
                ChapterVO chapterVO = converterToChapterVO(chapter);
                chapterVOS.add(chapterVO);
            }
        }
        return chapterVOS;
    }

    /***
     * 转VO
     * @param chapter
     * @return
     */
    public static ChapterVO converterToChapterVO(Chapter chapter) {
        ChapterVO chapterVO = new ChapterVO();
        BeanUtils.copyProperties(chapter, chapterVO);
        // 查询课程
        chapterVO.setLesson(chapter.getLesson());
        chapterVO.setStatus(chapter.getStatus());
        return chapterVO;
    }


    public static List<LessonTreeNode> converterToTreeList(List<Chapter> chapterS) {
        List<LessonTreeNode> lessonTreeNodes = new ArrayList<>();
        for (Chapter chapter : chapterS) {
            LessonTreeNode lessonTreeNode = new LessonTreeNode();
            BeanUtils.copyProperties(chapter, lessonTreeNode);
            lessonTreeNode.setParentId(chapter.getLesson());
            lessonTreeNode.setTitle(chapter.getTitle());
            lessonTreeNode.setSort(chapter.getSort());
            lessonTreeNodes.add(lessonTreeNode);
        }
        return lessonTreeNodes;
    }


}
