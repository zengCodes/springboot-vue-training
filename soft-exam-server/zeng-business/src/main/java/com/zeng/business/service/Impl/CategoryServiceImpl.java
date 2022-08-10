package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.CategoryConverter;
import com.zeng.business.entity.Category;
import com.zeng.business.mapper.CategoryMapper;

import com.zeng.business.service.CategoryService;
import com.zeng.business.utils.CategoryTreeBuilder;
import com.zeng.business.utils.ListPageUtils;
import com.zeng.business.vo.CategoryTreeNodeVO;
import com.zeng.business.vo.CategoryVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
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
 * @Date create in 2021/5/31 13:08
 **/
@Service("CategoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper courseCategoryDao;


    /**
     * 科目类别列表
     *
     * @param pageNum
     * @param pageSize
     * @param categoryVo
     * @return
     */
    @Override
    public PageVO<CategoryVO> findCategoryList(Integer pageNum, Integer pageSize, CategoryVO categoryVo) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(Category.class);
        Example.Criteria criteria = o.createCriteria();
        o.setOrderByClause("sort asc");
        if (StringUtils.isNotEmpty(categoryVo.getName())) {
            criteria.andLike("name", "%" + categoryVo.getName() + "%");
        }
        if (StringUtils.isNotEmpty(categoryVo.getType())) {
            criteria.andEqualTo("type", categoryVo.getType());
        }
        List<Category> categoryList = courseCategoryDao.selectByExample(o);
        List<CategoryVO> categoryVOS = CategoryConverter.converterToVOList(categoryList);
        PageInfo<Category> info = new PageInfo<>(categoryList);
        return new PageVO<>(info.getTotal(), categoryVOS);
    }


    /**
     * 添加科目类别
     *
     * @param categoryVO
     */
    @Override
    public void add(CategoryVO categoryVO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryVO, category);
        category.setStatus(CommonStatusEnum.AVAILABLE.getStatusCode())
                .setCreateTime(new Date())
                .setModifiedTime(new Date());
        courseCategoryDao.insert(category);
    }

    /**
     * 编辑科目类别
     *
     * @param id
     * @return
     */
    @Override
    public CategoryVO edit(Long id) {
        Category category = courseCategoryDao.selectByPrimaryKey(id);
        return CategoryConverter.converterToCourseCategoryVO(category);
    }

    /**
     * 更新科目类别
     *
     * @param id
     * @param categoryVO
     */
    @Override
    public void update(Long id, CategoryVO categoryVO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryVO, category);
        category.setModifiedTime(new Date());
        courseCategoryDao.updateByPrimaryKey(category);
    }

    /**
     * 删除科目类别
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        Category category = courseCategoryDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(category)) {
            throw new CustomException("该分类不存在");
        } else {
            //检查是否存在子分类
            Example o = new Example(Category.class);
            o.createCriteria().andEqualTo("parentId", id);
            int childCount = courseCategoryDao.selectCountByExample(o);
            if (childCount != 0) {
                throw new CustomException("存在子节点,无法直接删除");
            }
            courseCategoryDao.deleteByPrimaryKey(id);
        }
    }

    /**
     * 分类树形结构
     *
     * @return
     */
    @Override
    public PageVO<CategoryTreeNodeVO> categoryTree(Integer pageNum, Integer pageSize, CategoryVO categoryVO) {
        Example o = new Example(Category.class);
        if (StringUtils.isNotNull(categoryVO.getType())) {
            o.createCriteria().andLike("type", "%" + categoryVO.getType() + "%");
        }
        List<Category> categories = courseCategoryDao.selectByExample(o);
        List<CategoryVO> categoryVOList = CategoryConverter.converterToVOList(categories);
        List<CategoryTreeNodeVO> nodeVOS = CategoryConverter.converterToTreeNodeVO(categoryVOList);
        List<CategoryTreeNodeVO> tree = CategoryTreeBuilder.build(nodeVOS);
        List<CategoryTreeNodeVO> page;
        if (pageSize != null && pageNum != null) {
            page = ListPageUtils.page(tree, pageSize, pageNum);
            return new PageVO<>(tree.size(), page);
        } else {
            return new PageVO<>(tree.size(), tree);
        }
    }

    /**
     * 获取父级分类
     *
     * @return
     */
    @Override
    public List<CategoryTreeNodeVO> getParentCategoryTree(String type) {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("type", type);
        List<Category> categories = courseCategoryDao.selectByExample(example);
        List<CategoryVO> categoryVOS = CategoryConverter.converterToVOList(categories);
        List<CategoryTreeNodeVO> nodeVOS = CategoryConverter.converterToTreeNodeVO(categoryVOS);
        return CategoryTreeBuilder.buildParent(nodeVOS);
    }


    /**
     * 获取分类树数据
     *
     * @return
     */
    @Override
    public List<CategoryTreeNodeVO> getCategoryTree() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("type", "course");
        List<Category> categories = courseCategoryDao.selectByExample(example);
        List<CategoryVO> categoryVOS = CategoryConverter.converterToVOList(categories);
        List<CategoryTreeNodeVO> nodeVOS = CategoryConverter.converterToTreeNodeVO(categoryVOS);
        List<CategoryTreeNodeVO> tree = CategoryTreeBuilder.build(nodeVOS);
        CategoryTreeNodeVO categoryTreeNodeVO = new CategoryTreeNodeVO()
                .setId((long) 1)
                .setName("软件水平考试科目")
                .setChildren(tree);
        List<CategoryTreeNodeVO> p = new ArrayList<>();
        p.add(categoryTreeNodeVO);
        return p;
    }
}
