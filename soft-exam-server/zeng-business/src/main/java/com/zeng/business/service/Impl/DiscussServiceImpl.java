package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.DiscussConverter;
import com.zeng.business.entity.*;
import com.zeng.business.mapper.AnswerQuestionMapper;
import com.zeng.business.mapper.ChoiceQuestionMapper;
import com.zeng.business.mapper.DiscussMapper;
import com.zeng.business.service.DiscussService;
import com.zeng.business.vo.DiscussTreeNode;
import com.zeng.business.vo.DiscussVO;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.QuestionVO;
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
 * @Date create in 2022/1/4 10:23
 **/
@Service("DiscussService")
public class DiscussServiceImpl implements DiscussService {


    @Resource
    private DiscussMapper discussMapper;

    @Resource
    private ChoiceQuestionMapper choiceQuestionMapper;

    @Resource
    private AnswerQuestionMapper answerQuestionMapper;

    @Override
    public PageVO<DiscussVO> getDiscussList(Integer pageNum, Integer pageSize, DiscussVO discussVO) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(Discuss.class);
        List<Discuss> discusses = new ArrayList<>();
        List<DiscussVO> discussVOS = new ArrayList<>();
        if (StringUtils.isNotNull(discussVO.getParentId())) {
            o.createCriteria().andEqualTo("parentId", discussVO.getParentId());
        }
        discusses = discussMapper.selectByExample(o);
        discussVOS = DiscussConverter.converterToVOList(discusses);
        PageInfo<Discuss> pageInfo = new PageInfo<>(discusses);
        return new PageVO<>(pageInfo.getTotal(), discussVOS);
    }


    @Override
    public PageVO<DiscussTreeNode> getDiscussTree(Integer pageNum, Integer pageSize, DiscussVO discussVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<Discuss> discusses = new ArrayList<>();
        List<DiscussTreeNode> discussTreeNodes = new ArrayList<>();
        Example o = new Example(Discuss.class);
        o.createCriteria().andEqualTo("parentId", CommonStatusEnum.DISABLE.getStatusCode());
        discusses = discussMapper.selectByExample(o);
        discussTreeNodes = DiscussConverter.converterToVOTreeList(discusses);
        PageInfo<Discuss> pageInfo = new PageInfo<>(discusses);
        return new PageVO<>(pageInfo.getTotal(), discussTreeNodes);
    }

    @Override
    public DiscussVO getInfo(Long id) {
        Discuss discuss = discussMapper.selectByPrimaryKey(id);
        return DiscussConverter.converterToDiscussVO(discuss);
    }

    @Override
    public int add(DiscussVO discussVO) {
        Discuss discuss = new Discuss();
        BeanUtils.copyProperties(discussVO, discuss);
        discuss.setCreateTime(new Date());
        discuss.setParentId(discussVO.getParentId());
        discuss.setContent(discussVO.getArticleContent());
        discuss.setCreateBy(SecurityUtils.getUserId());
        discuss.setLikeNum(CommonStatusEnum.DISABLE.getStatusCode());
        discuss.setBrowseNum(CommonStatusEnum.DISABLE.getStatusCode());
        int n = discussMapper.insertSelective(discuss);
        return n;
    }

    @Override
    public int update(Long id, DiscussVO discussVO) {
        Discuss discuss = new Discuss();
        BeanUtils.copyProperties(discussVO, discuss);
        discuss.setContent(discussVO.getArticleContent());
        discuss.setCreateBy(discussVO.getCreateBy().getUserId());
        int n = discussMapper.updateByPrimaryKey(discuss);
        return n;
    }

    @Override
    public int delete(Long id) {
        return discussMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateStatus(Long id, Boolean status) {

    }

    @Override
    public List<QuestionVO> getQuestionData(String name) {
        List<QuestionVO> questionVOS = new ArrayList<>();
        // 模糊查询所有选择题
        Example o1 = new Example(ChoiceQuestion.class);
        o1.createCriteria().andLike("name", "%" + name + "%");
        List<ChoiceQuestion> choiceQuestions = choiceQuestionMapper.selectByExample(o1);
        // 模糊查询所有简答题题目
        Example o2 = new Example(AnswerQuestion.class);
        o2.createCriteria().andLike("name", "%" + name + "%");
        List<AnswerQuestion> answerQuestions = answerQuestionMapper.selectByExample(o2);
        for (ChoiceQuestion choiceQuestion : choiceQuestions) {
            QuestionVO questionVO = new QuestionVO();
            questionVO.setName(choiceQuestion.getName());
            questionVO.setQuestionId(choiceQuestion.getId());
            questionVO.setType(choiceQuestion.getTypeId());
            questionVOS.add(questionVO);
        }
        for (AnswerQuestion answerQuestion : answerQuestions) {
            QuestionVO questionVO = new QuestionVO();
            questionVO.setName(answerQuestion.getName());
            questionVO.setQuestionId(answerQuestion.getId());
            questionVO.setType(answerQuestion.getTypeId());
            questionVOS.add(questionVO);
        }
        return questionVOS;
    }

    @Override
    public QuestionVO getQuestionData(Long id, Long type) {
        QuestionVO questionVO = new QuestionVO();
        if (type.toString().equals("33")) {
            AnswerQuestion answerQuestion = answerQuestionMapper.selectByPrimaryKey(id);
            questionVO.setName(answerQuestion.getName());
        }
        // 先写死 后期优化 34选择题
        if (type.toString().equals("34")) {
            ChoiceQuestion choiceQuestion = choiceQuestionMapper.selectByPrimaryKey(id);
            questionVO.setName(choiceQuestion.getName());
        }
        questionVO.setQuestionId(id);
        return questionVO;
    }


}
