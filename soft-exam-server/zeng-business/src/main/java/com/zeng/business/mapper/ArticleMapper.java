package com.zeng.business.mapper;

import com.zeng.business.entity.Article;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/12 21:10
 **/
@Repository
public interface ArticleMapper extends Mapper<Article> {
}
