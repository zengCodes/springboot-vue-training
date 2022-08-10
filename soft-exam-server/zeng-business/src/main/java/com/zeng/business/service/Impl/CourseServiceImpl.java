package com.zeng.business.service.Impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.CourseConverter;
import com.zeng.business.entity.Category;
import com.zeng.business.entity.Course;
import com.zeng.business.entity.LessonApproval;
import com.zeng.business.mapper.CategoryMapper;
import com.zeng.business.mapper.CourseMapper;
import com.zeng.business.service.CourseService;
import com.zeng.business.vo.CourseVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/1 15:19
 **/
@Service("CourseService")
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseDao;

    @Resource
    private CategoryMapper categoryMapper;


    @Override
    public PageVO<CourseVO> getCourseList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> courses = courseDao.selectAll();
        List<CourseVO> courseVOS = CourseConverter.converterToVOList(courses);
        PageInfo<Course> pageInfo = new PageInfo<>(courses);
        return new PageVO<>(pageInfo.getTotal(), courseVOS);
    }

    /**
     * 科目列表
     *
     * @param pageNum
     * @param pageSize
     * @param courseVO
     * @return
     */
    @Override
    public PageVO<CourseVO> findCourseList(Integer pageNum, Integer pageSize, CourseVO courseVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> course;
        Example o = new Example(Course.class);
        Example.Criteria criteria = o.createCriteria();
        if (courseVO.getTwoCategoryId() != null) {
            criteria.andEqualTo("oneCategoryId", courseVO.getOneCategoryId())
                    .andEqualTo("twoCategoryId", courseVO.getTwoCategoryId());

        }
        if (courseVO.getOneCategoryId() != null) {
            criteria.andEqualTo("oneCategoryId", courseVO.getOneCategoryId());
        }
        o.setOrderByClause("sort asc");
        if (courseVO.getName() != null && !"".equals(courseVO.getName())) {
            criteria.andLike("name", "%" + courseVO.getName() + "%");
        }
        course = courseDao.selectByExample(o);
        List<CourseVO> categoryVOS = CourseConverter.converterToVOList(course);
        PageInfo<Course> info = new PageInfo<>(course);
        return new PageVO<>(info.getTotal(), categoryVOS);
    }


    /**
     * 添加科目
     *
     * @param courseVO
     */
    @Override
    public int add(CourseVO courseVO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseVO, course);
        course.setCreateTime(new Date());
        course.setModifiedTime(new Date());
        @NotNull(message = "分类不能为空") Long[] categoryKeys = courseVO.getCategoryKeys();
        if (categoryKeys.length == 2) {
            course.setOneCategoryId(categoryKeys[0]);
            course.setTwoCategoryId(categoryKeys[1]);
        }
        // 查询三级分类
        Calendar c = Calendar.getInstance();
        c.setTime(courseVO.getTime());
        int month = c.get(Calendar.MONTH);//只提取月
        Category category = new Category();
        if (month >= 6) {
            category = categoryMapper.selectOne(new Category().setName("下半年"));
        } else {
            category = categoryMapper.selectOne(new Category().setName("上半年"));
        }
        // 判断是否添加过该科目 科目名称+一级分类+二级分类+三级分类
        Course isCourse = courseDao.selectOne(new Course().setName(courseVO.getName()).setOneCategoryId(categoryKeys[0]).setTwoCategoryId(categoryKeys[1]).setTime(courseVO.getTime()));
        if (StringUtils.isNotNull(isCourse)) {
            return 0;
        }
        course.setThreeCategoryId(category.getId());
        course.setStatus(CommonStatusEnum.AVAILABLE.getStatusCode());//禁用
        return courseDao.insert(course);
    }

    /**
     * 编辑科目
     *
     * @param id
     * @return
     */
    @Override
    public CourseVO edit(Long id) {
        Course course = courseDao.selectByPrimaryKey(id);
        return CourseConverter.converterToCourseVO(course);
    }

    /**
     * 更新科目
     *
     * @param id
     * @param courseVO
     */
    @Override
    public void update(Long id, CourseVO courseVO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseVO, course);
        course.setModifiedTime(new Date());
        @NotNull(message = "分类不能为空") Long[] categoryKeys = courseVO.getCategoryKeys();
        if (categoryKeys.length == 2) {
            course.setOneCategoryId(categoryKeys[0])
                    .setTwoCategoryId(categoryKeys[1]);
        }
        // 查询三级分类
        Calendar c = Calendar.getInstance();
        c.setTime(courseVO.getTime());
        int month = c.get(Calendar.MONTH);//只提取月
        Category category = new Category();
        if (month >= 6) {
            category = categoryMapper.selectOne(new Category().setName("下半年"));
        } else {
            category = categoryMapper.selectOne(new Category().setName("上半年"));
        }
        course.setThreeCategoryId(category.getId());
        courseDao.updateByPrimaryKey(course);
    }


    /**
     * 更新用户禁用状态
     *  @param id
     * @param status
     * @return
     */
    @Override
    public int updateStatus(Long id, CourseVO courseVO){
        Course course = courseDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(course)) {
            throw new CustomException("要更新状态的科目不存在");
        } else {
            course.setStatus(courseVO.getStatus())
                    .setModifiedTime(new Date());
            int n = courseDao.updateByPrimaryKey(course);
            return n;
        }
    }

    /**
     * 删除科目
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        Course course = courseDao.selectByPrimaryKey(id);
        if (course.getStatus() != 1 && course.getStatus() != 2) {
            throw new CustomException("要删除的科目不可删除");
        } else {
            courseDao.deleteByPrimaryKey(id);
        }
    }
}
