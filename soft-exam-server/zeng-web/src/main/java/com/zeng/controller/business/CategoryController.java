package com.zeng.controller.business;

import com.zeng.business.service.CategoryService;
import com.zeng.business.vo.CategoryTreeNodeVO;
import com.zeng.business.vo.CategoryVO;
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
 * @Date create in 2021/5/31 13:07
 **/
@Api(tags = "业务模块-项目类别管理接口")
@RequestMapping("business/category")
@RestController
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 课程分类列表
     *
     * @return
     */
    @ApiOperation(value = "分类列表", notes = "课程分类列表,根据课程分类名模糊查询")
    @GetMapping("/findCategoryList")
    public ResponseBean findCourseCategoryList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize") Integer pageSize,
            CategoryVO categoryVO) {
        PageVO<CategoryVO> courseCategoryList = categoryService.findCategoryList(pageNum, pageSize, categoryVO);
        return ResponseBean.success(courseCategoryList);
    }

    /**
     * 分类树形结构(分页)
     *
     * @return
     */
    @ApiOperation(value = "分类树形结构")
    @GetMapping("/categoryTree")
    public ResponseBean categoryTree(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                     CategoryVO categoryVO) {
        PageVO<CategoryTreeNodeVO> pageVO = categoryService.categoryTree(pageNum, pageSize, categoryVO);
        return ResponseBean.success(pageVO);
    }


    @ApiOperation(value = "分类树形结构")
    @GetMapping("/getCategoryTree")
    public ResponseBean getCategoryTree() {
        List<CategoryTreeNodeVO> categoryTreeNodeVOS = categoryService.getCategoryTree();
        return ResponseBean.success(categoryTreeNodeVOS);
    }

    /**
     * 获取父级分类树：2级树
     *
     * @return
     */
    @ApiOperation(value = "父级分类树")
    @GetMapping("/getParentCategoryTree")
    public ResponseBean getParentCategoryTree(@RequestParam(value = "type") String type) {
        List<CategoryTreeNodeVO> parentTree = categoryService.getParentCategoryTree(type);
        return ResponseBean.success(parentTree);
    }

    /**
     * 添加科目分类
     *
     * @return
     */
    @ApiOperation(value = "添加分类")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody CategoryVO categoryVO) {
        categoryService.add(categoryVO);
        return ResponseBean.success();
    }

    /**
     * 编辑科目分类
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑分类")
    @GetMapping("/edit/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean edit(@PathVariable Long id) {
        CategoryVO categoryVO = categoryService.edit(id);
        return ResponseBean.success(categoryVO);
    }

    /**
     * 更新科目分类
     *
     * @return
     */
    @ApiOperation(value = "更新分类")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody @Validated CategoryVO categoryVO) {
        categoryService.update(id, categoryVO);
        return ResponseBean.success();
    }

    /**
     * 删除科目分类
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除分类")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseBean.success();
    }
}
