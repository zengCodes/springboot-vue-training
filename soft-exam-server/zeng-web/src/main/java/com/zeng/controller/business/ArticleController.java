package com.zeng.controller.business;

import com.zeng.business.service.ArticleService;
import com.zeng.business.vo.*;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/12 22:52
 **/
@Api(tags = "业务模块-文章管理接口")
@RestController
@RequestMapping("/business/article")
@Slf4j
public class ArticleController {


    @Resource
    private ArticleService articleService;


    @ApiOperation(value = "获取文章列表")
    @GetMapping("/getListArticle")
    public ResponseBean getListArticle(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize") Integer pageSize,
                                       ArticleVO articleVO) throws IOException {
        PageVO<ArticleVO> articleVOPageVO = articleService.getArticleList(pageNum, pageSize, articleVO);
        return ResponseBean.success(articleVOPageVO);
    }


    /**
     * 添加或修改文章
     *
     * @param articleVO 文章信息
     * @return
     */
    @ApiOperation(value = "添加或修改文章")
    @PostMapping("/add")
    public ResponseBean saveOrUpdateArticle(@RequestBody ArticleVO articleVO) {
        int i = articleService.saveArticle(articleVO);
        return ResponseBean.success(i);
    }

    /**
     * 编辑
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑文章")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        ArticleVO articleVO = articleService.edit(id);
        return ResponseBean.success(articleVO);
    }


    /**
     * 更新
     *
     * @return
     */
    @ApiOperation(value = "更新文章", notes = "更新文章信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody ArticleVO articleVO) {
        if (StringUtils.isEmpty(id)) {
            throw new CustomException("文章编号不能为空");
        }
        int i = articleService.update(id, articleVO);
        return ResponseBean.success(i);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除文章")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        int i = articleService.delete(id);
        return ResponseBean.success(i);
    }
}
