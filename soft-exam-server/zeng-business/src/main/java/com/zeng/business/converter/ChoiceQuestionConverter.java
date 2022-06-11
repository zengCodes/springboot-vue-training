package com.zeng.business.converter;

import com.zeng.business.entity.ChoiceQuestion;
import com.zeng.business.vo.ChoiceQuestionVO;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/13 2:09
 **/
public class ChoiceQuestionConverter {
    /**
     * 转voList
     *
     * @param choiceQuestions
     * @return
     */
    public static List<ChoiceQuestionVO> converterToVOList(List<ChoiceQuestion> choiceQuestions) {
        List<ChoiceQuestionVO> choiceQuestionVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(choiceQuestions)) {
            for (ChoiceQuestion choiceQuestion : choiceQuestions) {
                ChoiceQuestionVO choiceQuestionVO = converterToChoiceQuestionVO(choiceQuestion);
                choiceQuestionVOS.add(choiceQuestionVO);
            }
        }
        return choiceQuestionVOS;
    }


    /***
     * 转VO
     * @param choiceQuestion
     * @return
     */
    public static ChoiceQuestionVO converterToChoiceQuestionVO(ChoiceQuestion choiceQuestion) {
        ChoiceQuestionVO choiceQuestionVO = new ChoiceQuestionVO();
        BeanUtils.copyProperties(choiceQuestion, choiceQuestionVO);
        return choiceQuestionVO;
    }
}
