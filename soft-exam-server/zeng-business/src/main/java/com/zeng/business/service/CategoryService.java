package com.zeng.business.service;


import com.zeng.business.vo.CategoryTreeNodeVO;
import com.zeng.business.vo.CategoryVO;
import com.zeng.business.vo.PageVO;

import java.util.List;

public interface CategoryService {
    /**
     * 添加课程类别
     *
     * @param categoryVO
     */
    void add(CategoryVO categoryVO);


    /**
     * 课程列表
     *
     * @param pageNum
     * @param pageSize
     * @param categoryVO
     * @return
     */
    PageVO<CategoryVO> findCategoryList(Integer pageNum, Integer pageSize, CategoryVO categoryVO);


    /**
     * 编辑课程类别
     *
     * @param id
     * @return
     */
    CategoryVO edit(Long id);

    /**
     * 更新课程类别
     *
     * @param id
     * @param categoryVO
     */
    void update(Long id, CategoryVO categoryVO);

    /**
     * 删除课程类别
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 分类树形
     *
     * @return
     */
    PageVO<CategoryTreeNodeVO> categoryTree(Integer pageNum, Integer pageSize, CategoryVO categoryVO);

    /**
     * 获取父级分类（2级树）
     *
     * @return
     */
    List<CategoryTreeNodeVO> getParentCategoryTree(String type);

    List<CategoryTreeNodeVO> getCategoryTree();
}
