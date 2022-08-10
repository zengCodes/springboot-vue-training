package com.zeng.controller.business;

import com.zeng.business.service.LessonService;
import com.zeng.business.vo.*;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/7 22:43
 **/
@Api(tags = "业务模块-课程列表管理接口")
@RestController
@RequestMapping("business/lesson")
public class LessonController {

    @Resource
    private LessonService lessonService;


    @ApiOperation(value = "课程列表", notes = "课程列表")
    @GetMapping("/getLessonList")
    public ResponseBean getLessonList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize") Integer pageSize,
                                      LessonVO lessonVO) {
        PageVO<LessonVO> lessonList = lessonService.getLessonList(pageNum, pageSize, lessonVO);
        return ResponseBean.success(lessonList);
    }


    /**
     * 前台课程---分类树结构
     *
     * @return
     */
    @ApiOperation(value = "课程分类树形结构")
    @GetMapping("/lessonTree")
    public ResponseBean lessonTree(@RequestParam(value = "status") Boolean status) {
        List<LessonTreeNodeVO> lessonTreeNodeVOS = lessonService.lessonTree(status);
        return ResponseBean.success(lessonTreeNodeVOS);
    }


    /**
     * 编辑课程
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑课程", notes = "编辑课程信息")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        LessonVO lessonVO = lessonService.edit(id);
        return ResponseBean.success(lessonVO);
    }

    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加课程")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody @Validated LessonVO lessonVO) {
        int i = lessonService.add(lessonVO);
        return ResponseBean.success(i);
    }

    /**
     * 更新
     *
     * @return
     */
    @ApiOperation(value = "更新课程", notes = "更新课程信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody LessonVO lessonVO) {
        if (StringUtils.isEmpty(id)) {
            throw new CustomException("课程编号不能为空");
        }
        int i = lessonService.update(id, lessonVO);
        return ResponseBean.success(i);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除课程", notes = "删除课程信息")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        int i = lessonService.delete(id);
        return ResponseBean.success(i);
    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "课程上下架状态", notes = "上架、下架这两种状态")
    @PutMapping("/updateStatus/{id}/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataTypeClass = Boolean.class),
    })
    public ResponseBean updateStatus(@PathVariable Long id, @PathVariable Boolean status) {
        lessonService.updateStatus(id, status);
        return ResponseBean.success();
    }

    @ApiOperation(value = "课程是否启用状态", notes = "禁用和启用这两种状态")
    @PutMapping("/updateLessonStatus/{id}/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataTypeClass = Integer.class),
    })
    public ResponseBean updateLessonStatus(@PathVariable Long id, @PathVariable Integer status) {
        lessonService.updateLessonStatus(id, status);
        return ResponseBean.success();
    }


    @ApiOperation(value = "保存学习记录", notes = "保存学习记录")
    @PutMapping("/saveLessonStudy/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean saveLessonStudy(@PathVariable Long id) {
        int n = lessonService.saveLessonStudy(id);
        return ResponseBean.success(n);
    }


    /**
     * 根据课程id查询课程信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取课程信息", notes = "获取课程信息")
    @GetMapping("/getLessonInfo/{id}")
    public ResponseBean getOrderInfo(@PathVariable Long id) {
        LessonVO lessonVO = lessonService.getLessonInfo(id);
        return ResponseBean.success(lessonVO);
    }
}
