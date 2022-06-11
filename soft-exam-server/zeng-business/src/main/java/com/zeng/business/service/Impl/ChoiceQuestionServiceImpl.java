package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.zeng.business.converter.ChoiceQuestionConverter;
import com.zeng.business.entity.ChoiceQuestion;
import com.zeng.business.entity.Paper;
import com.zeng.business.mapper.ChoiceQuestionMapper;
import com.zeng.business.mapper.PaperMapper;
import com.zeng.business.service.ChoiceQuestionService;
import com.zeng.business.vo.ChoiceQuestionVO;
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
 * @Date create in 2021/6/13 1:21
 **/
@Service("ChoiceQuestionService")
@Slf4j
public class ChoiceQuestionServiceImpl implements ChoiceQuestionService {


    @Resource
    private ChoiceQuestionMapper choiceQuestionDao;


    @Resource
    private PaperMapper paperDao;

    /**
     * 选择题列表
     *
     * @param pageNum
     * @param pageSize
     * @param choiceQuestionVO
     * @return
     */
    @Override
    public PageVO<ChoiceQuestionVO> findChoiceQuestionList(Integer pageNum, Integer pageSize, ChoiceQuestionVO choiceQuestionVO) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(ChoiceQuestion.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotNull(choiceQuestionVO.getName())) {
            criteria.andLike("name", "%" + choiceQuestionVO.getName() + "%");
        }
        if (StringUtils.isNotNull(choiceQuestionVO.getPaperId())) {
            criteria.andEqualTo("paperId", choiceQuestionVO.getPaperId());
        }
        List<ChoiceQuestion> choiceQuestions = choiceQuestionDao.selectByExample(o);
        List<ChoiceQuestionVO> choiceQuestionVOS = ChoiceQuestionConverter.converterToVOList(choiceQuestions);
        for (ChoiceQuestionVO c : choiceQuestionVOS) {
            Paper p = paperDao.selectByPrimaryKey(c.getPaperId());
            if (StringUtils.isNotNull(p)) {
                c.setPaperName(p.getTime() + p.getName());
            }
        }
        PageInfo<ChoiceQuestion> choiceQuestionPageInfo = new PageInfo<>(choiceQuestions);
        return new PageVO<>(choiceQuestionPageInfo.getTotal(), choiceQuestionVOS);
    }


    /**
     * 添加选择题
     *
     * @param choiceQuestionVO
     */
    @Override
    public ChoiceQuestion add(ChoiceQuestionVO choiceQuestionVO) {
        ChoiceQuestion choiceQuestion = new ChoiceQuestion();
        BeanUtils.copyProperties(choiceQuestionVO, choiceQuestion);
        choiceQuestion.setModifiedTime(new Date());
        choiceQuestion.setCreateTime(new Date());
        choiceQuestionDao.insert(choiceQuestion);
        return choiceQuestion;
    }

    /**
     * 编辑选择题
     *
     * @param id
     * @return
     */
    @Override
    public ChoiceQuestionVO edit(Long id) {
        ChoiceQuestion choiceQuestion = choiceQuestionDao.selectByPrimaryKey(id);
        return ChoiceQuestionConverter.converterToChoiceQuestionVO(choiceQuestion);
    }

    /**
     * 更新
     *
     * @param id
     * @param choiceQuestionVO
     */
    @Override
    public int update(Long id, ChoiceQuestionVO choiceQuestionVO) {
        ChoiceQuestion choiceQuestion = new ChoiceQuestion();
        BeanUtils.copyProperties(choiceQuestionVO, choiceQuestion);
        choiceQuestion.setModifiedTime(new Date());
        return choiceQuestionDao.updateByPrimaryKeySelective(choiceQuestion);
    }

    /**
     * 删除选择题
     *
     * @param id
     */
    @Override
    public int delete(Long id) {
        ChoiceQuestion choiceQuestion = choiceQuestionDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(choiceQuestion)) {
            throw new CustomException("该试题不存在");
        } else {
           return choiceQuestionDao.deleteByPrimaryKey(id);
        }
    }

    /**
     * 所有选择试题
     *
     * @return
     */
    @Override
    public List<ChoiceQuestionVO> findAll() {
        List<ChoiceQuestion> choiceQuestions = choiceQuestionDao.selectAll();
        return ChoiceQuestionConverter.converterToVOList(choiceQuestions);
    }

}
