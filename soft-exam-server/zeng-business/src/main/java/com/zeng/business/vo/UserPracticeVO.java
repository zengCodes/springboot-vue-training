package com.zeng.business.vo;

import lombok.Data;
import javax.persistence.Id;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/16 17:43
 **/
@Data
public class UserPracticeVO {

    @Id
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 试卷对象
     */
    private PaperVO paper;


    private Long questionId;

    private Long questionType;
    /**
     * 回答情况
     */
    private String userResponse;

    /**
     * 回答问题对象
     */
    private ChoiceQuestionVO choiceQuestionVO;

    private AnswerQuestionVO answerQuestionVO;
}
