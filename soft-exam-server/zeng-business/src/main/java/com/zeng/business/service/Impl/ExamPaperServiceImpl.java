package com.zeng.business.service.Impl;

import com.zeng.business.dto.PracticeDTO;
import com.zeng.business.dto.TestDTO;
import com.zeng.business.entity.TestRecord;
import com.zeng.business.entity.UserPractice;
import com.zeng.business.mapper.TestRecordMapper;
import com.zeng.business.mapper.UserPracticeMapper;
import com.zeng.business.service.ExamPaperService;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/17 22:29
 **/
@Service("ExamPaperService")
public class ExamPaperServiceImpl implements ExamPaperService {

    @Resource
    private UserPracticeMapper userPracticeMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Override
    public int sendPaper(Long id, ArrayList<TestDTO> testDTO) {
        int sum = 0;
        Long paper = null;
        for (TestDTO test : testDTO) {
            if (StringUtils.isNotNull(test.getData())) {
                for (PracticeDTO practiceDTO : test.getData()) {
                    UserPractice userPractice = new UserPractice();
                    userPractice.setTestId(practiceDTO.getTestId())
                            .setQuestionId(practiceDTO.getId())
                            .setQuestionType(practiceDTO.getType())
                            .setUserResponse(practiceDTO.getUserResponse())
                            .setUserId(SecurityUtils.getUserId());
                    userPracticeMapper.insert(userPractice);
                    if (practiceDTO.getCorrectAnswer().equals(practiceDTO.getUserResponse())) {
                        sum += practiceDTO.getScore();
                    }
                }
            }
        }
        // 保存分数到记录表
        TestRecord testRecord = new TestRecord();
        testRecord.setId(id);
        TestRecord p = testRecordMapper.selectOne(testRecord);
        p.setTotal(sum);
        testRecordMapper.updateByPrimaryKey(p);
        return sum;
    }
}
