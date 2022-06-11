package com.zeng.business.converter;


import com.zeng.business.entity.Category;
import com.zeng.business.vo.CategoryTreeNodeVO;
import com.zeng.business.vo.CategoryVO;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/31 20:35
 **/
@Component
public class CategoryConverter {

    /**
     * 转vo
     *
     * @param category
     * @return
     */
    public static CategoryVO converterToCourseCategoryVO(Category category) {
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    /**
     * 转voList
     *
     * @param categoryList
     * @return
     */
    public static List<CategoryVO> converterToVOList(List<Category> categoryList) {
        List<CategoryVO> categoryVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = converterToCourseCategoryVO(category);
                categoryVOS.add(categoryVO);
            }
        }
        return categoryVOS;
    }

    /**
     * 转树节点
     *
     * @param categoryVOList
     * @return
     */
    public static List<CategoryTreeNodeVO> converterToTreeNodeVO(List<CategoryVO> categoryVOList) {
        List<CategoryTreeNodeVO> nodes = new ArrayList<>();
        if (!StringUtils.isEmpty(categoryVOList)) {
            for (CategoryVO categoryVO : categoryVOList) {
                CategoryTreeNodeVO categoryTreeNodeVO = new CategoryTreeNodeVO();
                BeanUtils.copyProperties(categoryVO, categoryTreeNodeVO);
                nodes.add(categoryTreeNodeVO);
            }
        }
        return nodes;
    }
}
