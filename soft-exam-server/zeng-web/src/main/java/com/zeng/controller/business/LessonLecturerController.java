package com.zeng.controller.business;


import com.zeng.business.service.LessonLecturerService;
import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.business.vo.LessonVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.controller.BaseController;
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

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/25 23:00
 **/
@Api(tags = "业务模块-讲师列表管理接口")
@RestController
@RequestMapping("business/lessonLecturer")
public class LessonLecturerController extends BaseController {

    @Resource
    private LessonLecturerService lessonLecturerService;


    @ApiOperation(value = "讲师列表", notes = "讲师试题列表")
    @GetMapping("/getLessonLecturerList")
    public ResponseBean getLessonLecturerList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize") Integer pageSize,
                                              LessonLecturerVO lessonLecturerVO) {
        PageVO<LessonLecturerVO> lessonLecturerVOS = lessonLecturerService.findLessonLecturerList(pageNum, pageSize, lessonLecturerVO);
        return ResponseBean.success(lessonLecturerVOS);
    }

    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加讲师")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody @Validated LessonLecturerVO lessonLecturerVO) {
        int n = lessonLecturerService.add(lessonLecturerVO);
        return ResponseBean.success(n);
    }

    /**
     * 更新
     *
     * @return
     */
    @ApiOperation(value = "更新讲师", notes = "更新讲师信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody LessonLecturerVO lessonLecturerVO) {
        if (StringUtils.isEmpty(id)) {
            throw new CustomException("讲师编号不能为空");
        }
        int n = lessonLecturerService.update(id, lessonLecturerVO);
        return ResponseBean.success(n);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除讲师", notes = "删除讲师信息")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        lessonLecturerService.delete(id);
        return ResponseBean.success(lessonLecturerService.delete(id));
    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "用户状态", notes = "禁用和启用这两种状态")
    @PutMapping("/updateStatus/{id}/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataTypeClass = Boolean.class),
    })
    public ResponseBean updateStatus(@PathVariable Long id, @PathVariable Boolean status) {
        lessonLecturerService.updateStatus(id, status);
        return ResponseBean.success();
    }


    /**
     * 编辑课程
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑讲师", notes = "编辑讲师信息")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        LessonLecturerVO lessonVO = lessonLecturerService.edit(id);
        return ResponseBean.success(lessonVO);
    }

}
