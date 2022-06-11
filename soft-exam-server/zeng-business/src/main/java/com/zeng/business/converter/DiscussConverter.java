package com.zeng.business.converter;

import com.zeng.business.entity.Chapter;
import com.zeng.business.entity.ChoiceQuestion;
import com.zeng.business.entity.Discuss;
import com.zeng.business.mapper.AnswerQuestionMapper;
import com.zeng.business.mapper.ChoiceQuestionMapper;
import com.zeng.business.mapper.DiscussMapper;
import com.zeng.business.vo.DiscussTreeNode;
import com.zeng.business.vo.DiscussVO;
import com.zeng.business.vo.QuestionVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/4 11:17
 **/
@Component
public class DiscussConverter {


    public static DiscussConverter discussConverter;

    public static SysUserMapper sysUserMapper;

    public static DiscussMapper discussMapper;

    public static ChoiceQuestionMapper choiceQuestionMapper;

    public static AnswerQuestionMapper answerQuestionMapper;

    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        discussConverter.sysUserMapper = sysUserMapper;
    }

    @Autowired
    public void setDiscussMapper(DiscussMapper discussMapper) {
        discussConverter.discussMapper = discussMapper;
    }


    @Autowired
    public void setChoiceQuestionMapper(ChoiceQuestionMapper choiceQuestionMapper) {
        discussConverter.choiceQuestionMapper = choiceQuestionMapper;
    }

    @Autowired
    public void setAnswerQuestionMapper(AnswerQuestionMapper answerQuestionMapper) {
        discussConverter.answerQuestionMapper = answerQuestionMapper;
    }

    /**
     * 转voList
     *
     * @param discusses
     * @return
     */
    public static List<DiscussVO> converterToVOList(List<Discuss> discusses) {
        List<DiscussVO> discussVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(discusses)) {
            for (Discuss discuss : discusses) {
                DiscussVO discussVO = converterToDiscussVO(discuss);

                discussVOS.add(discussVO);
            }
        }
        return discussVOS;
    }


    /***
     * 转VO
     * @param discuss
     * @return
     */
    public static DiscussVO converterToDiscussVO(Discuss discuss) {
        DiscussVO discussVO = new DiscussVO();
        BeanUtils.copyProperties(discuss, discussVO);
        // 查询用户信息
        SysUser sysUser = sysUserMapper.selectUserById(discuss.getCreateBy());
        discussVO.setCreateBy(sysUser);
        Example o = new Example(Discuss.class);
        Example.Criteria criteria = o.createCriteria();
        criteria.andEqualTo("parentId", discuss.getId());
        discussVO.setDiscussNum(discussMapper.selectCountByExample(o));
        discussVO.setArticleContent(discuss.getContent());
        // 查询题目
        if (StringUtils.isNotNull(discuss.getType()) && discuss.getType().toString().equals("33")) {
            discussVO.setQuestionName(answerQuestionMapper.selectByPrimaryKey(discuss.getQuestion()).getName());
        }
        if (StringUtils.isNotNull(discuss.getType()) && discuss.getType().toString().equals("34")) {
            discussVO.setQuestionName(choiceQuestionMapper.selectByPrimaryKey(discuss.getQuestion()).getName());
        }
        return discussVO;
    }

    public static List<DiscussTreeNode> converterToVOTreeList(List<Discuss> discusses) {
        List<DiscussTreeNode> discussTreeNodes = new ArrayList<>();
        for (Discuss discuss : discusses) {
            DiscussTreeNode discussTreeNode = new DiscussTreeNode();
            BeanUtils.copyProperties(converterToDiscussVO(discuss), discussTreeNode);
            QuestionVO questionVO = new QuestionVO();
            // 问题
            if (StringUtils.isNotNull(discuss.getType()) && discuss.getType().toString().equals("33")) {
                questionVO.setName(answerQuestionMapper.selectByPrimaryKey(discuss.getQuestion()).getName());
                questionVO.setType(discuss.getType());
                questionVO.setQuestionId(discuss.getQuestion());
            }
            if (StringUtils.isNotNull(discuss.getType()) && discuss.getType().toString().equals("34")) {
                questionVO.setName(choiceQuestionMapper.selectByPrimaryKey(discuss.getQuestion()).getName());
                questionVO.setType(discuss.getType());
                questionVO.setQuestionId(discuss.getQuestion());
            }
            discussTreeNode.setQuestion(questionVO);
            // 查询该讨论下的所有回复
            Example o = new Example(Discuss.class);
            Example.Criteria criteria = o.createCriteria();
            criteria.andEqualTo("parentId", discuss.getId());
            List<Discuss> discussList = discussMapper.selectByExample(o);
            discussTreeNode.setChildren(converterToVOTreeList(discussList));
            discussTreeNodes.add(discussTreeNode);
        }
        return discussTreeNodes;
    }


}
