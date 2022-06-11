package com.zeng.business.mapper;

import com.zeng.business.entity.Course;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface CourseMapper extends Mapper<Course> {
}
