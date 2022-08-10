package com.zeng.business.converter;

import com.zeng.business.entity.Article;
import com.zeng.business.entity.Category;
import com.zeng.business.entity.Course;
import com.zeng.business.mapper.CategoryMapper;
import com.zeng.business.vo.ArticleVO;
import com.zeng.business.vo.CourseVO;
import com.zeng.common.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/12 23:42
 **/
@Component
@Data
public class ArticleConverter {

    public static ArticleConverter articleConverter;

    public static CategoryMapper categoryMapper;


    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        articleConverter.categoryMapper = categoryMapper;
    }

    /**
     * 转VOList
     *
     * @param articleList
     * @return
     */
    public static List<ArticleVO> converterToVOList(List<Article> articleList) {
        List<ArticleVO> articleVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(articleList)) {
            for (Article article : articleList) {
                if (article.getIsDelete() != 1) {
                    ArticleVO articleVO = converterToArticleVO(article);
                    articleVOS.add(articleVO);
                }
            }
        }
        return articleVOS;
    }

    /**
     * 转VO
     *
     * @param article
     * @return
     */
    public static ArticleVO converterToArticleVO(Article article) {
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        // 查询分类名称
        Category category = categoryMapper.selectByPrimaryKey(article.getCategoryId());
        articleVO.setCategoryName(category.getName());
        return articleVO;
    }

}
