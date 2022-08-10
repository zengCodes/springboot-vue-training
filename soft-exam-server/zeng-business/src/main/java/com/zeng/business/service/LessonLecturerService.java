package com.zeng.business.service;


import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.business.vo.PageVO;
import org.omg.CORBA.SystemException;

public interface LessonLecturerService {

    PageVO<LessonLecturerVO> findLessonLecturerList(Integer pageNum, Integer pageSize, LessonLecturerVO lessonLecturerVO);

    /**
     * 添加讲师
     *
     * @param lessonLecturerVO
     */
    int add(LessonLecturerVO lessonLecturerVO);

    /**
     * 更新
     *
     * @param id
     * @param lessonLecturerVO
     */
    int update(Long id, LessonLecturerVO lessonLecturerVO);

    /**
     * 更新状态
     *
     * @param id
     * @param status
     */
    int updateStatus(Long id, Boolean status) throws SystemException;

    /**
     * 删除
     *
     * @param id
     */
    int delete(Long id);

    LessonLecturerVO edit(Long id);
}
