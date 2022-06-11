package com.zeng.controller.business;

import com.zeng.business.service.ChapterService;
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
 * @Date create in 2021/12/25 16:46
 **/
@Api(tags = "业务模块-章节管理接口")
@RestController
@RequestMapping("business/chapter")
public class ChapterController {

    @Resource
    private ChapterService chapterService;

    @ApiOperation(value = "章节列表", notes = "章节列表")
    @GetMapping("/getLessonChapter")
    public ResponseBean getLessonChapter(@RequestParam("lesson") Integer lesson) {
        List<ChapterVO> chapterVOS = chapterService.getLessonChapter(lesson);
        return ResponseBean.success(chapterVOS);
    }


    @ApiOperation(value = "章节树列表", notes = "章节树列表")
    @GetMapping("/getChapterTreeList")
    public ResponseBean getChapterTreeList() {
        List<LessonTreeNode> lessonTreeNodes = chapterService.getChapterTreeList();
        return ResponseBean.success(lessonTreeNodes);
    }


    /**
     * 编辑章节
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑章节", notes = "编辑章节信息")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        ChapterVO chapterVO = chapterService.edit(id);
        return ResponseBean.success(chapterVO);
    }

    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加章节")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody @Validated ChapterVO chapterVO) {
        int i = chapterService.add(chapterVO);
        return ResponseBean.success(i);
    }

    /**
     * 更新
     *
     * @return
     */
    @ApiOperation(value = "更新章节", notes = "更新章节信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody ChapterVO chapterVO) {
        if (StringUtils.isEmpty(id)) {
            throw new CustomException("章节编号不能为空");
        }
        int i = chapterService.update(id, chapterVO);
        return ResponseBean.success(i);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除章节", notes = "删除章节信息")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        int i = chapterService.delete(id);
        return ResponseBean.success(i);
    }


    @ApiOperation(value = "课程是否启用状态", notes = "禁用和启用这两种状态")
    @PutMapping("/updateLessonStatus/{id}/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataTypeClass = Boolean.class),
    })
    public ResponseBean updateChapterStatus(@PathVariable Long id, @PathVariable Boolean status) {
        chapterService.updateChapterStatus(id, status);
        return ResponseBean.success();
    }


}
