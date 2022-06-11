package com.zeng.business.service;

import com.zeng.business.vo.ChapterVO;
import com.zeng.business.vo.LessonTreeNode;

import java.util.List;


public interface ChapterService {

    public List<ChapterVO> getLessonChapter(Integer lesson);

    List<LessonTreeNode> getChapterTreeList();

    ChapterVO edit(Long id);

    int add(ChapterVO chapterVO);

    int update(Long id, ChapterVO chapterVO);

    int delete(Long id);

    int updateChapterStatus(Long id, Boolean status);
}
