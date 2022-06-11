package com.zeng.controller.business;

import com.zeng.business.service.DiscussService;
import com.zeng.business.vo.*;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/4 10:27
 **/
@Api(tags = "业务模块-讨论管理管理接口")
@RestController
@RequestMapping("business/discuss")
public class DiscussController {

    @Resource
    private DiscussService discussService;


    @ApiOperation(value = "题目查询", notes = "对题目模糊查询")
    @GetMapping("/getQuestionData")
    public ResponseBean getQuestionData(@RequestParam(value = "name") String name) {
        List<QuestionVO> questionVOS = discussService.getQuestionData(name);
        return ResponseBean.success(questionVOS);
    }


    @ApiOperation(value = "讨论列表", notes = "讨论列表")
    @GetMapping("/getDiscussList")
    public ResponseBean getDiscussList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize") Integer pageSize,
                                       DiscussVO discussVO) {
        System.out.println(discussVO.getParentId());
        if (StringUtils.isNotNull(discussVO.getParentId())) {
            PageVO<DiscussVO> discussVOS = discussService.getDiscussList(pageNum, pageSize, discussVO);
            return ResponseBean.success(discussVOS);
        } else {
            PageVO<DiscussTreeNode> discussTree = discussService.getDiscussTree(pageNum, pageSize, discussVO);
            return ResponseBean.success(discussTree);
        }
    }


    /**
     * 编辑
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "讨论信息", notes = "获取讨论信息")
    @GetMapping("/info/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean getInfo(@PathVariable Long id) {
        DiscussVO discussVO = discussService.getInfo(id);
        return ResponseBean.success(discussVO);
    }

    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加讨论")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody DiscussVO discussVO) {
        int i = discussService.add(discussVO);
        return ResponseBean.success(i);
    }

    /**
     * 更新
     *
     * @return
     */
    @ApiOperation(value = "更新讨论", notes = "更新讨论信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody DiscussVO discussVO) {
        if (StringUtils.isNull(id)) {
            throw new CustomException("讨论编号不能为空");
        }
        int i = discussService.update(id, discussVO);
        return ResponseBean.success(i);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除讨论", notes = "删除讨论信息")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        int i = discussService.delete(id);
        return ResponseBean.success(i);
    }


    @ApiOperation(value = "讨论是否启用状态", notes = "禁用和启用这两种状态")
    @PutMapping("/updateStatus/{id}/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataTypeClass = Boolean.class),
    })
    public ResponseBean updateStatus(@PathVariable Long id, @PathVariable Boolean status) {
        discussService.updateStatus(id, status);
        return ResponseBean.success();
    }

    @ApiOperation(value = "根据类型id查询题目", notes = "根据类型id查询题目")
    @GetMapping("/getQuestionInfo")
    public ResponseBean getQuestionData(@RequestParam(value = "id") Long id, @RequestParam(value = "type") Long type) {
        return ResponseBean.success(discussService.getQuestionData(id, type));
    }
}
