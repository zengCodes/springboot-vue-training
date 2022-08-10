package com.zeng.business.service;


import com.zeng.business.vo.CourseVO;
import com.zeng.business.vo.PageVO;
import org.omg.CORBA.SystemException;

public interface CourseService {

    /**
     * 添加科目
     *
     * @param courseVO
     */
    int add(CourseVO courseVO);


    /**
     * 商品列表
     *
     * @param pageNum
     * @param pageSize
     * @param courseVO
     * @return
     */
    PageVO<CourseVO> findCourseList(Integer pageNum, Integer pageSize, CourseVO courseVO);


    /**
     * 编辑科目
     *
     * @param id
     * @return
     */
    CourseVO edit(Long id);

    /**
     * 更新商品
     *
     * @param id
     * @param courseVO
     */
    void update(Long id, CourseVO courseVO);

    /**
     * 更新状态
     *  @param id
     * @param courseVO
     * @return
     */
    int updateStatus(Long id, CourseVO courseVO);

    /**
     * 删除科目
     *
     * @param id
     */
    void delete(Long id);


    PageVO<CourseVO> getCourseList(Integer pageNum, Integer pageSize);
}
