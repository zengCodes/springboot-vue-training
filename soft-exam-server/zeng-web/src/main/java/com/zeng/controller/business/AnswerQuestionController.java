package com.zeng.controller.business;


import com.zeng.business.service.AnswerQuestionService;
import com.zeng.business.vo.AnswerQuestionVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.domain.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/13 1:04
 **/
@RestController
@RequestMapping("business/answerQuestion")
@Api(tags = "业务模块-简答题管理接口")
public class AnswerQuestionController{

    @Resource
    private AnswerQuestionService answerQuestionService;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation(value = "简答题题列表", notes = "简答题列表,根据条件查询")
    @GetMapping("/findQuestionList")
    public ResponseBean findAnswerQuestionServiceList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize") Integer pageSize,
            AnswerQuestionVO answerQuestionVO) {
        PageVO<AnswerQuestionVO> answerQuestionVOPageVO = answerQuestionService.findAnswerQuestionList(pageNum, pageSize, answerQuestionVO);
        return ResponseBean.success(answerQuestionVOPageVO);
    }

    /**
     * 查询所有类型
     *
     * @return
     */
    @ApiOperation(value = "所有简答题列表")
    @GetMapping("/findAll")
    public ResponseBean findAll() {
        List<AnswerQuestionVO> answerQuestionVOS = answerQuestionService.findAll();
        return ResponseBean.success(answerQuestionVOS);
    }

    /**
     * 添加试题类型
     *
     * @return
     */
    @ApiOperation(value = "添加简答题")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody @Validated AnswerQuestionVO answerQuestionVO) {
        answerQuestionService.add(answerQuestionVO);
        return ResponseBean.success();
    }

    /**
     * 编辑试题类型
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑试题类型")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        AnswerQuestionVO answerQuestionVO = answerQuestionService.edit(id);
        return ResponseBean.success(answerQuestionVO);
    }

    /**
     * 更新试题类型
     *
     * @return
     */
    @ApiOperation(value = "更新试题")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody @Validated AnswerQuestionVO answerQuestionVO) {
        answerQuestionService.update(id, answerQuestionVO);
        return ResponseBean.success();
    }

    /**
     * 删除试题类型
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除分类")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        answerQuestionService.delete(id);
        return ResponseBean.success();
    }


}

