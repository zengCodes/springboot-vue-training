package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.ArticleConverter;
import com.zeng.business.entity.Article;
import com.zeng.business.entity.Category;
import com.zeng.business.entity.Chapter;
import com.zeng.business.entity.Lesson;
import com.zeng.business.mapper.ArticleMapper;
import com.zeng.business.mapper.CategoryMapper;
import com.zeng.business.service.ArticleService;
import com.zeng.business.vo.ArticleVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/12 21:11
 **/
@Service("ArticleService")
public class ArticleServiceImpl implements ArticleService {


    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CategoryMapper categoryMapper;


    @Override
    public PageVO<ArticleVO> getArticleList(Integer pageNum, Integer pageSize, ArticleVO articleVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.selectAll();
        List<ArticleVO> articleVOList = ArticleConverter.converterToVOList(articles);
        PageInfo<ArticleVO> articlePageInfo = new PageInfo<>(articleVOList);
        return new PageVO<>(articlePageInfo.getTotal(), articleVOList);
    }

    @Override
    public int delete(Long id) {
        Article article = articleMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(article)) {
            throw new CustomException("文章不存在！");
        } else {
            Article a = new Article();
            a.setId(id);
            a.setIsDelete(CommonStatusEnum.AVAILABLE.getStatusCode());
            return articleMapper.updateByPrimaryKeySelective(a);
        }
    }

    @Override
    public int update(Long id, ArticleVO articleVO) {
        Article article = articleMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(articleVO, article);
        if (articleVO.getArticleCover().indexOf("dev-api") > 0) {
            article.setArticleCover(articleVO.getArticleCover().replaceAll("/dev-api", ""));
        }
        article.setId(id)
                .setUpdateTime(new Date());
        int n = articleMapper.updateByPrimaryKey(article);
        return n;
    }

    /**
     * 新增文章
     *
     * @param
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveArticle(ArticleVO articleVO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        // 查询分类名称
        Category category = categoryMapper.selectOne(new Category().setName(articleVO.getCategoryName()));
        if (StringUtils.isNotNull(category)) {
            article.setCategoryId(category.getId());
        }
        article.setCreateBy(SecurityUtils.getUserId());
        article.setIsDelete(CommonStatusEnum.DISABLE.getStatusCode());
        article.setCreateTime(new Date());
        return articleMapper.insert(article);
    }


    /**
     * 编辑
     *
     * @param id
     * @return
     */
    @Override
    public ArticleVO edit(Long id) {
        Article article = articleMapper.selectByPrimaryKey(id);
        return ArticleConverter.converterToArticleVO(article);
    }

}
