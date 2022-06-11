package com.zeng.controller.business;

import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.service.LecturerApprovalService;
import com.zeng.business.vo.LecturerApprovalVO;
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
 * @Date create in 2021/12/6 18:24
 **/
@Api(tags = "业务模块-讲师审核接口")
@RestController
@RequestMapping("/business/lecturerApproval")
@Slf4j
public class LecturerApprovalController {

    @Resource
    private LecturerApprovalService lecturerApprovalService;

    /**
     * 全部科目列表
     *
     * @return
     */
    @ApiOperation(value = "讲师审核列表", notes = "讲师审核列表,根据讲师名称模糊查询")
    @GetMapping("/getLecturerApprovalList")
    public ResponseBean getLecturerApprovalList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize") Integer pageSize,
                                                LecturerApprovalVO lecturerApprovalVO) {
        PageVO<LecturerApprovalVO> pageVO = lecturerApprovalService.getLecturerApprovalList(pageNum, pageSize, lecturerApprovalVO);
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
    public ResponseBean updateStatus(@PathVariable Long id, ApprovalDTO approvalDTO) {
        int n = lecturerApprovalService.updateApprovalStatus(id, approvalDTO);
        return ResponseBean.success(n);
    }

}
