package com.zeng.business.service;

import com.zeng.business.entity.ChoiceQuestion;
import com.zeng.business.vo.ChoiceQuestionVO;
import com.zeng.business.vo.PageVO;

import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/13 1:21
 **/
public interface ChoiceQuestionService {


    PageVO<ChoiceQuestionVO> findChoiceQuestionList(Integer pageNum, Integer pageSize, ChoiceQuestionVO choiceQuestionVO);

    List<ChoiceQuestionVO> findAll();

    ChoiceQuestion add(ChoiceQuestionVO choiceQuestionVO);

    ChoiceQuestionVO edit(Long id);

    int update(Long id, ChoiceQuestionVO choiceQuestionVO);

    int delete(Long id);
}
