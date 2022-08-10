package com.zeng.business.converter;


import com.zeng.business.entity.AnswerQuestion;
import com.zeng.business.vo.AnswerQuestionVO;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/24 0:40
 **/
public class AnswerQuestionConverter {
    /**
     * 转voList
     *
     * @param answerQuestions
     * @return
     */
    public static List<AnswerQuestionVO> converterToVOList(List<AnswerQuestion> answerQuestions) {
        List<AnswerQuestionVO> answerQuestionVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(answerQuestions)) {
            for (AnswerQuestion answerQuestion : answerQuestions) {
                AnswerQuestionVO answerQuestionVO = converterToAnswerQuestionVO(answerQuestion);
                answerQuestionVOS.add(answerQuestionVO);
            }
        }
        return answerQuestionVOS;
    }


    /***
     * 转VO
     * @param answerQuestion
     * @return
     */
    public static AnswerQuestionVO converterToAnswerQuestionVO(AnswerQuestion answerQuestion) {
        AnswerQuestionVO answerQuestionVO = new AnswerQuestionVO();
        BeanUtils.copyProperties(answerQuestion, answerQuestionVO);
        return answerQuestionVO;
    }
}
