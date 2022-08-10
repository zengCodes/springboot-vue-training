package com.zeng.business.converter;

import com.zeng.business.entity.Course;
import com.zeng.business.mapper.CategoryMapper;
import com.zeng.business.vo.CourseVO;
import com.zeng.common.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/1 15:25
 **/
@Component
@Data
public class CourseConverter {

    private static CourseConverter courseConverter;


    public static CategoryMapper categoryMapper;

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        courseConverter.categoryMapper = categoryMapper;
    }


    /**
     * 转VOList
     *
     * @param courseList
     * @return
     */
    public static List<CourseVO> converterToVOList(List<Course> courseList) {
        List<CourseVO> courseVOs = new ArrayList<>();
        if (!StringUtils.isEmpty(courseList)) {
            for (Course course : courseList) {
                CourseVO courseVO = converterToCourseVO(course);
                courseVOs.add(courseVO);
            }
        }
        return courseVOs;
    }

    /**
     * 转VO
     *
     * @param course
     * @return
     */
    public static CourseVO converterToCourseVO(Course course) {
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        // 查询年度
        Date date = course.getTime();
        String year = formatter.format(date).split("-")[1];
        if (Integer.parseInt(year) >= 6) {
            courseVO.setAnnual(formatter.format(date).split("-")[0] + "-下半年");
        } else {
            courseVO.setAnnual(formatter.format(date).split("-")[0] + "-上半年");
        }
        return courseVO;
    }
}
