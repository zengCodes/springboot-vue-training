package com.zeng.business.service.Impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.AnswerQuestionConverter;
import com.zeng.business.entity.AnswerQuestion;
import com.zeng.business.entity.ChoiceQuestion;
import com.zeng.business.entity.Paper;
import com.zeng.business.mapper.AnswerQuestionMapper;
import com.zeng.business.mapper.PaperMapper;
import com.zeng.business.service.AnswerQuestionService;
import com.zeng.business.vo.AnswerQuestionVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/24 0:36
 **/
@Service("AnswerQuestionService")
@Slf4j
public class AnswerQuestionServiceImpl implements AnswerQuestionService {
    @Resource
    private AnswerQuestionMapper answerQuestionDao;


    @Resource
    private PaperMapper paperDao;

    /**
     * 题目列表
     *
     * @param pageNum
     * @param pageSize
     * @param answerQuestionVO
     * @return
     */
    @Override
    public PageVO<AnswerQuestionVO> findAnswerQuestionList(Integer pageNum, Integer pageSize, AnswerQuestionVO answerQuestionVO) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(ChoiceQuestion.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotNull(answerQuestionVO.getName())) {
            criteria.andLike("name", "%" + answerQuestionVO.getName() + "%");
        }
        if (StringUtils.isNotNull(answerQuestionVO.getPaperId())) {
            criteria.andEqualTo("paperId", answerQuestionVO.getPaperId());
        }
        List<AnswerQuestion> answerQuestions = answerQuestionDao.selectByExample(o);
        List<AnswerQuestionVO> answerQuestionVOS = AnswerQuestionConverter.converterToVOList(answerQuestions);
        for (AnswerQuestionVO a : answerQuestionVOS) {
            Paper paper = new Paper();
            paper.setId(a.getPaperId());
            Paper p = paperDao.selectOne(paper);
            a.setPaperName(p.getTime() + p.getName());
        }
        PageInfo<AnswerQuestion> answerQuestionPageInfo = new PageInfo<>(answerQuestions);
        return new PageVO<>(answerQuestionPageInfo.getTotal(), answerQuestionVOS);
    }


    /**
     * 添加题目
     *
     * @param answerQuestionVO
     */
    @Override
    public void add(AnswerQuestionVO answerQuestionVO) {
        AnswerQuestion answerQuestion = new AnswerQuestion();
        BeanUtils.copyProperties(answerQuestionVO, answerQuestion);
        answerQuestion.setModifiedTime(new Date());
        answerQuestion.setCreateTime(new Date());
        answerQuestionDao.insert(answerQuestion);
    }

    /**
     * 编辑题目
     *
     * @param id
     * @return
     */
    @Override
    public AnswerQuestionVO edit(Long id) {
        AnswerQuestion answerQuestion = answerQuestionDao.selectByPrimaryKey(id);
        return AnswerQuestionConverter.converterToAnswerQuestionVO(answerQuestion);
    }

    /**
     * 更新题目
     *
     * @param id
     * @param answerQuestionVO
     */
    @Override
    public void update(Long id, AnswerQuestionVO answerQuestionVO) {
        AnswerQuestion answerQuestion = new AnswerQuestion();
        BeanUtils.copyProperties(answerQuestionVO, answerQuestion);
        answerQuestion.setModifiedTime(new Date());
        answerQuestionDao.updateByPrimaryKeySelective(answerQuestion);
    }

    /**
     * 删除题目
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        AnswerQuestion answerQuestion = answerQuestionDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(answerQuestion)) {
            throw new CustomException("该试题不存在");
        } else {
            answerQuestionDao.deleteByPrimaryKey(id);
        }
    }

    /**
     * 所有题目
     *
     * @return
     */
    @Override
    public List<AnswerQuestionVO> findAll() {
        List<AnswerQuestion> answerQuestions = answerQuestionDao.selectAll();
        return AnswerQuestionConverter.converterToVOList(answerQuestions);
    }
}
