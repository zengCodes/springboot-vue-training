package com.zeng.controller.business;

import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.service.CourseService;
import com.zeng.business.vo.CourseVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.controller.BaseController;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.page.TableDataInfo;
import com.zeng.common.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/1 15:21
 **/
@Api(tags = "业务模块-科目列表管理接口")
@RestController
@RequestMapping("business/course")
public class CourseController {
    @Resource
    private CourseService courseService;

    /**
     * 全部科目列表
     *
     * @return
     */
    @ApiOperation(value = "科目列表", notes = "科目列表,根据科目名模糊查询")
    @GetMapping("/getCourseList")
    public ResponseBean getCourseList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize") Integer pageSize) {
        PageVO<CourseVO> pageVO = courseService.getCourseList(pageNum, pageSize);
        return ResponseBean.success(pageVO);
    }

    /**
     * 全部科目列表
     *
     * @return
     */
    @ApiOperation(value = "科目列表", notes = "科目列表,根据科目名模糊查询")
    @GetMapping("/findCourseList")
    public ResponseBean findCourseList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize") Integer pageSize,
                                       @RequestParam(value = "categorys", required = false) String categorys,
                                       CourseVO courseVO) {
        buildCategorySearch(categorys, courseVO);
        PageVO<CourseVO> productVOPageVO = courseService.findCourseList(pageNum, pageSize, courseVO);
        return ResponseBean.success(productVOPageVO);
    }

    /**
     * 封装科目查询条件
     *
     * @param categorys
     * @param courseVO
     */
    private void buildCategorySearch(@RequestParam(value = "categorys", required = false) String categorys, CourseVO courseVO) {
        if (categorys != null && !"".equals(categorys)) {
            String[] split = categorys.split(",");
            switch (split.length) {
                case 1:
                    courseVO.setOneCategoryId(Long.parseLong(split[0]));
                    break;
                case 2:
                    courseVO.setOneCategoryId(Long.parseLong(split[0]));
                    courseVO.setTwoCategoryId(Long.parseLong(split[1]));
                    break;
            }
        }
    }


    /**
     * 添加科目
     *
     * @return
     */
    @ApiOperation(value = "添加科目")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody CourseVO courseVO) {
        if (courseVO.getCategoryKeys().length != 2) {
            throw new CustomException("科目需要2级分类");
        }
        int i = courseService.add(courseVO);
        return ResponseBean.success(i);
    }

    /**
     * 编辑科目
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑科目", notes = "编辑科目信息")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        CourseVO courseVO = courseService.edit(id);
        return ResponseBean.success(courseVO);
    }

    /**
     * 更新物资
     *
     * @return
     */
    @ApiOperation(value = "更新科目", notes = "更新科目信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody CourseVO courseVO) {
        if (courseVO.getCategoryKeys().length != 2) {
            throw new CustomException("科目需要2级分类");
        }
        courseService.update(id, courseVO);
        return ResponseBean.success();
    }

    /**
     * 更新状态
     *
     * @param id
     * @param courseVO
     * @return
     */
    @ApiOperation(value = "科目状态", notes = "禁用和启用这两种状态")
    @PutMapping("/updateStatus/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean updateStatus(@PathVariable Long id, @RequestBody CourseVO courseVO) {
        int i = courseService.updateStatus(id, courseVO);
        return ResponseBean.success(i);
    }

    /**
     * 删除科目
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除科目", notes = "删除科目信息")
    @DeleteMapping("/delete/{id}")
    public ResponseBean delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseBean.success();
    }
}
