package com.zeng.controller.business;

import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.entity.LessonApproval;
import com.zeng.business.service.LessonApprovalService;
import com.zeng.business.vo.LessonApprovalVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.domain.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/9 17:54
 **/

@Api(tags = "业务模块-课程审核接口")
@RestController
@RequestMapping("/business/lessonApproval")
@Slf4j
public class LessonApprovalController {

    @Resource
    private LessonApprovalService lessonApprovalService;

    /**
     * 全部科目列表
     *
     * @return
     */
    @ApiOperation(value = "课程审核列表", notes = "课程审核列表,根据课程名称模糊查询")
    @GetMapping("/getLessonApprovalList")
    public ResponseBean getLessonApprovalList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize") Integer pageSize,
                                              LessonApprovalVO lecturerApproval) {
        PageVO<LessonApprovalVO> pageVO = lessonApprovalService.getLessonApprovalList(pageNum, pageSize, lecturerApproval);
        return ResponseBean.success(pageVO);
    }


    /**
     * 审核
     *
     * @param id
     * @param approvalDTO
     * @return
     */
    @ApiOperation(value = "讲师审核", notes = "待审核、通过、不通过三种状态")
    @PutMapping("/updateApprovalStatus/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean updateStatus(@PathVariable Long id, @RequestBody ApprovalDTO approvalDTO) {
        int n = lessonApprovalService.updateApprovalStatus(id, approvalDTO);
        return ResponseBean.success(n);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除课程审核信息", notes = "删除课程审核信息")
    @DeleteMapping("/delete/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean delete(@PathVariable Long id) {
        int i = lessonApprovalService.delete(id);
        return ResponseBean.success(i);
    }

}
