package com.zeng.business.service;

import com.zeng.business.vo.DiscussTreeNode;
import com.zeng.business.vo.DiscussVO;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.QuestionVO;

import java.util.List;

public interface DiscussService {

    PageVO<DiscussTreeNode> getDiscussTree(Integer pageNum, Integer pageSize, DiscussVO discussVO);

    PageVO<DiscussVO> getDiscussList(Integer pageNum, Integer pageSize, DiscussVO discussVO);

    DiscussVO getInfo(Long id);

    int add(DiscussVO discussVO);

    int update(Long id, DiscussVO discussVO);

    int delete(Long id);

    void updateStatus(Long id, Boolean status);

    List<QuestionVO> getQuestionData(String name);

    QuestionVO getQuestionData(Long id, Long type);
}
