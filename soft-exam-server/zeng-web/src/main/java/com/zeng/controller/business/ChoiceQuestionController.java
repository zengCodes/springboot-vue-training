package com.zeng.controller.business;

import com.zeng.business.entity.ChoiceQuestion;
import com.zeng.business.service.ChoiceQuestionService;
import com.zeng.business.vo.AnswerQuestionVO;
import com.zeng.business.vo.ChoiceQuestionVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.controller.BaseController;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * (ExamChoiceQuestion)表控制层
 *
 * @author zenghuiiqng
 * @since 2021-04-13 22:22:06
 */
@RestController
@RequestMapping("business/choiceQuestion")
@Api(tags = "业务模块-选择题管理接口")
@Slf4j
public class ChoiceQuestionController extends BaseController {

    @Resource
    private ChoiceQuestionService choiceQuestionService;


    /**
     * 列表
     *
     * @return
     */
    @ApiOperation(value = "选择题列表", notes = "选择题列表,根据条件查询")
    @GetMapping("/findQuestionList")
    public ResponseBean findChoiceQuestionList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize") Integer pageSize,
            ChoiceQuestionVO choiceQuestionVO) {
        PageVO<ChoiceQuestionVO> choiceQuestionList = choiceQuestionService.findChoiceQuestionList(pageNum, pageSize, choiceQuestionVO);
        return ResponseBean.success(choiceQuestionList);
    }

    /**
     * 查询所有类型
     *
     * @return
     */
    @ApiOperation(value = "所有选择题列表")
    @GetMapping("/findAll")
    public ResponseBean findAll() {
        List<ChoiceQuestionVO> choiceQuestionVOS = choiceQuestionService.findAll();
        return ResponseBean.success(choiceQuestionVOS);
    }

    /**
     * 添加试题类型
     *
     * @return
     */
    @ApiOperation(value = "添加选择题")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody @Validated ChoiceQuestionVO choiceQuestionVO) {
        ChoiceQuestion choiceQuestion = choiceQuestionService.add(choiceQuestionVO);
        return ResponseBean.success(choiceQuestion);
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
        ChoiceQuestionVO choiceQuestionVO = choiceQuestionService.edit(id);
        return ResponseBean.success(choiceQuestionVO);
    }

    /**
     * 更新试题类型
     *
     * @return
     */
    @ApiOperation(value = "更新试题")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody @Validated ChoiceQuestionVO choiceQuestionVO) {
        int n = choiceQuestionService.update(id, choiceQuestionVO);
        return ResponseBean.success(n);
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
        int n = choiceQuestionService.delete(id);
        return ResponseBean.success(n);
    }

}
