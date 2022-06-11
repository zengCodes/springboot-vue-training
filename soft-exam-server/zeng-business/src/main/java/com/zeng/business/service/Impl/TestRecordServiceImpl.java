package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.DiscussConverter;
import com.zeng.business.converter.TestRecordConverter;
import com.zeng.business.converter.UserPracticeConverter;
import com.zeng.business.entity.Discuss;
import com.zeng.business.entity.TestRecord;
import com.zeng.business.entity.UserPractice;
import com.zeng.business.mapper.TestRecordMapper;
import com.zeng.business.mapper.UserPracticeMapper;
import com.zeng.business.service.TestRecordService;
import com.zeng.business.vo.DiscussVO;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.TestRecordVO;
import com.zeng.business.vo.UserPracticeVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
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
 * @Date create in 2021/12/19 15:35
 **/
@Service("TestRecordService")
public class TestRecordServiceImpl implements TestRecordService {


    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private UserPracticeMapper userPracticeMapper;


    @Override
    public Long saveRecord(Integer type, TestRecordVO testRecordVO) {
        TestRecord testRecord = new TestRecord();
        BeanUtils.copyProperties(testRecordVO, testRecord);
        testRecord.setCreateTime(new Date())
                .setUserId(SecurityUtils.getUserId())
                .setType(type);
        // 如果是练习
        if (type.equals(CommonStatusEnum.DISABLE.getStatusCode())) {
            // 查询同一条记录，新增num
            TestRecord t = new TestRecord();
            t.setPaperId(testRecord.getPaperId())
                    .setType(CommonStatusEnum.DISABLE.getStatusCode())
                    .setUserId(SecurityUtils.getUserId());
            TestRecord test = testRecordMapper.selectOne(t);
            if (StringUtils.isNotNull(test)) {
                Integer num;
                if(StringUtils.isNotNull(test.getNum())){
                    num = test.getNum();
                }else{
                    num =0;
                }
                test.setNum( num * 1 + 1);
                testRecordMapper.updateByPrimaryKeySelective(test);
            } else {
                testRecord.setNum(1);
                testRecordMapper.insert(testRecord);
            }
        } else {
            testRecordMapper.insert(testRecord);
        }
        return testRecord.getId();
    }


    @Override
    public TestRecordVO getTestInfo(Long id) {
        TestRecord testRecord = testRecordMapper.selectByPrimaryKey(id);
        return TestRecordConverter.converterToTestRecordVO(testRecord);
    }

    @Override
    public int delete(Long id) {
        return testRecordMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageVO<TestRecordVO> getRecordList(Integer pageNum, Integer pageSize, TestRecordVO testRecordVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<TestRecordVO> testRecordVOS = new ArrayList<>();
        Example o = new Example(TestRecord.class);
        if (StringUtils.isNotNull(testRecordVO.getUserId())) {
            o.createCriteria().andEqualTo("userId", testRecordVO.getUserId());
        }
        if (StringUtils.isNotNull(testRecordVO.getUserId())) {
            o.createCriteria().andEqualTo("paperId", testRecordVO.getPaperId());
        }
        if (StringUtils.isNotNull(testRecordVO.getNickName())) {
            o.createCriteria().andEqualTo("nickName", testRecordVO.getNickName());
        }
        List<TestRecord> testRecords = testRecordMapper.selectByExample(o);
        testRecordVOS = TestRecordConverter.converterToVOList(testRecords);
        PageInfo<TestRecordVO> pageInfo = new PageInfo<>(testRecordVOS);
        return new PageVO<>(pageInfo.getTotal(), testRecordVOS);
    }

    @Override
    public List<UserPracticeVO> getPracticeList(Long user, Long testId) {

        List<UserPracticeVO> userPracticeVOS = new ArrayList<>();
        Example o = new Example(UserPractice.class);
        if (StringUtils.isNotNull(user) && StringUtils.isNotNull(testId)) {
            o.createCriteria().andEqualTo("testId", testId)
                    .andEqualTo("userId", user);
        }
        List<UserPractice> userPractices = userPracticeMapper.selectByExample(o);
        userPracticeVOS = UserPracticeConverter.converterToVOList(userPractices);
        return userPracticeVOS;
    }


}
