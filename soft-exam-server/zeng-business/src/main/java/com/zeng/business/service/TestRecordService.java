package com.zeng.business.service;

import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.TestRecordVO;
import com.zeng.business.vo.UserPracticeVO;

import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/19 15:35
 **/
public interface TestRecordService {

    Long saveRecord(Integer type, TestRecordVO testRecordVO);

    PageVO<TestRecordVO> getRecordList(Integer pageNum, Integer pageSize, TestRecordVO testRecordVO);

    List<UserPracticeVO> getPracticeList(Long user, Long testId);

    public TestRecordVO getTestInfo(Long id);

    int delete(Long id);
}
