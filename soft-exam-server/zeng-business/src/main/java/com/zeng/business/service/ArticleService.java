package com.zeng.business.service;

import com.zeng.business.vo.ArticleVO;
import com.zeng.business.vo.PageVO;

public interface ArticleService {
    ArticleVO edit(Long id);

    int saveArticle(ArticleVO articleVO);

    PageVO<ArticleVO> getArticleList(Integer pageNum, Integer pageSize, ArticleVO articleVO);

    int delete(Long id);

    int update(Long id, ArticleVO articleVO);
}
