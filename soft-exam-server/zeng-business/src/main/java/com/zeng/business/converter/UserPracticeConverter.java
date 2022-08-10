package com.zeng.business.converter;

import com.zeng.business.entity.*;
import com.zeng.business.mapper.*;
import com.zeng.business.vo.UserPracticeVO;
import com.zeng.common.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/16 17:48
 **/

@Data
@Component
public class UserPracticeConverter {


    public static UserPracticeConverter userPracticeConverter;

    private static PaperMapper paperMapper;

    private static TestRecordMapper testRecordMapper;

    private static CategoryMapper categoryMapper;

    private static ChoiceQuestionMapper choiceQuestionMapper;

    private static AnswerQuestionMapper answerQuestionMapper;

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        userPracticeConverter.categoryMapper = categoryMapper;
    }

    @Autowired
    public void setPaperMapper(PaperMapper paperMapper) {
        userPracticeConverter.paperMapper = paperMapper;
    }

    @Autowired
    public void setTestRecordMapper(TestRecordMapper testRecordMapper) {
        userPracticeConverter.testRecordMapper = testRecordMapper;
    }


    @Autowired
    public void setChoiceQuestionMapper(ChoiceQuestionMapper choiceQuestionMapper) {
        userPracticeConverter.choiceQuestionMapper = choiceQuestionMapper;
    }

    @Autowired
    public void setAnswerQuestionMapper(AnswerQuestionMapper answerQuestionMapper) {
        userPracticeConverter.answerQuestionMapper = answerQuestionMapper;
    }

    /**
     * 转voList
     *
     * @param userPractices
     * @return
     */
    public static List<UserPracticeVO> converterToVOList(List<UserPractice> userPractices) {
        List<UserPracticeVO> userPracticeVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(userPractices)) {
            for (UserPractice userPractice : userPractices) {
                UserPracticeVO userPracticeVO = converterToUserPracticeVO(userPractice);
                userPracticeVOS.add(userPracticeVO);
            }
        }
        return userPracticeVOS;
    }


    /***
     * 转VO
     * @param userPractice
     * @return
     */
    public static UserPracticeVO converterToUserPracticeVO(UserPractice userPractice) {
        UserPracticeVO userPracticeVO = new UserPracticeVO();
        BeanUtils.copyProperties(userPractice, userPracticeVO);
        // 查询试卷
        TestRecord testRecord = new TestRecord();
        testRecord.setId(userPractice.getTestId());
        if (StringUtils.isNotNull(testRecordMapper.selectByPrimaryKey(testRecord))) {
            TestRecord testRecord1 = testRecordMapper.selectByPrimaryKey(testRecord);
            userPracticeVO.setPaper(PaperConverter.converterToPaperVO(paperMapper.selectByPrimaryKey(testRecord1.getPaperId())));
        }
        // 查询试题
        Long questionType = userPractice.getQuestionType();
        if (questionType.toString().equals("34")) {
            // 查询试题
            ChoiceQuestion choiceQuestion = new ChoiceQuestion();
            choiceQuestion.setId(userPractice.getQuestionId());
            if (StringUtils.isNotNull(choiceQuestionMapper.selectOne(choiceQuestion))) {
                userPracticeVO.setChoiceQuestionVO(ChoiceQuestionConverter.converterToChoiceQuestionVO(choiceQuestionMapper.selectOne(choiceQuestion)));
            }
        } else {
            AnswerQuestion answerQuestion = new AnswerQuestion();
            answerQuestion.setId(userPractice.getQuestionId());
            if (StringUtils.isNotNull(answerQuestionMapper.selectOne(answerQuestion))) {
                userPracticeVO.setAnswerQuestionVO(AnswerQuestionConverter.converterToAnswerQuestionVO(answerQuestionMapper.selectOne(answerQuestion)));
            }
        }
        return userPracticeVO;
    }

}
