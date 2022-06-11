package com.zeng.business.converter;

import com.zeng.business.entity.Course;
import com.zeng.business.entity.Order;
import com.zeng.business.entity.Paper;
import com.zeng.business.entity.TestRecord;
import com.zeng.business.mapper.CourseMapper;
import com.zeng.business.mapper.PaperMapper;
import com.zeng.business.vo.OrderVO;
import com.zeng.business.vo.TestRecordVO;
import com.zeng.common.utils.StringUtils;
import lombok.Data;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/14 12:13
 **/
@Component
@Data
public class TestRecordConverter {

    private static TestRecordConverter testRecordConverter;


    private static PaperMapper paperMapper;

    private static CourseMapper courseMapper;


    @Autowired
    public void setPaperMapper(PaperMapper paperMapper) {
        testRecordConverter.paperMapper = paperMapper;
    }


    @Autowired
    public void setCourseMapper(CourseMapper courseMapper) {
        testRecordConverter.courseMapper = courseMapper;
    }

    /**
     * 转voList
     *
     * @param testRecords
     * @return
     */
    public static List<TestRecordVO> converterToVOList(List<TestRecord> testRecords) {
        List<TestRecordVO> testRecordVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(testRecords)) {
            for (TestRecord testRecord : testRecords) {
                if (testRecord.getType().toString().equals("1")) {
                    TestRecordVO testRecordVO = converterToTestRecordVO(testRecord);
                    testRecordVOS.add(testRecordVO);
                }
            }
        }
        return testRecordVOS;
    }


    /***
     * 转VO
     * @param testRecord
     * @return
     */
    public static TestRecordVO converterToTestRecordVO(TestRecord testRecord) {
        TestRecordVO testRecordVO = new TestRecordVO();
        BeanUtils.copyProperties(testRecord, testRecordVO);
        // 查询试卷
        Paper paper = paperMapper.selectByPrimaryKey(testRecord.getPaperId());
        testRecordVO.setQuestionTotal(paper.getScore());
        testRecordVO.setPaperName(paper.getTime() + paper.getName());
        Course course = courseMapper.selectByPrimaryKey(paper.getCourseId());
        testRecordVO.setCourseName(course.getName());
        return testRecordVO;
    }

}
