package com.zeng.business.service;


import com.zeng.business.vo.AnswerQuestionVO;
import com.zeng.business.vo.PageVO;


import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/24 0:36
 **/
public interface AnswerQuestionService {

    PageVO<AnswerQuestionVO> findAnswerQuestionList(Integer pageNum, Integer pageSize, AnswerQuestionVO answerQuestionVO);


    List<AnswerQuestionVO> findAll();

    void add(AnswerQuestionVO answerQuestionVO);

    AnswerQuestionVO edit(Long id);

    void update(Long id, AnswerQuestionVO answerQuestionVO);

    void delete(Long id);
}
