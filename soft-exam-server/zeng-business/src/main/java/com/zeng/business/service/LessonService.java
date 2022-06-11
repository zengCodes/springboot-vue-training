package com.zeng.business.service;

import com.zeng.business.vo.LessonTreeNodeVO;
import com.zeng.business.vo.LessonVO;
import com.zeng.business.vo.PageVO;

import java.util.List;

public interface LessonService {
    PageVO<LessonVO> getLessonList(Integer pageNum, Integer pageSize, LessonVO lessonVO);

    int add(LessonVO lessonVO);

    int update(Long id, LessonVO lessonVO);

    int delete(Long id);

    int updateStatus(Long id, Boolean status);

    int updateLessonStatus(Long id, Integer status);

    List<LessonTreeNodeVO> lessonTree(Boolean status);

    LessonVO edit(Long id);

    int saveLessonStudy(Long id);

    LessonVO getLessonInfo(Long id);
}
