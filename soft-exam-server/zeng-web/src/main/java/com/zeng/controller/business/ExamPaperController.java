package com.zeng.controller.business;

import com.zeng.business.dto.PracticeDTO;
import com.zeng.business.dto.SendTestDTO;
import com.zeng.business.dto.TestDTO;
import com.zeng.business.service.ExamPaperService;
import com.zeng.business.vo.CourseVO;
import com.zeng.business.vo.UserPracticeVO;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/17 22:23
 **/
@Api(tags = "业务模块-试卷统计分数接口")
@RestController
@RequestMapping("business/exam")
public class ExamPaperController {

    @Resource
    private ExamPaperService examPaperService;


    @ApiOperation(value = "试卷提交")
    @PostMapping("/sendPractice")
    public ResponseBean sendPaper(@RequestBody @Validated SendTestDTO sendTestDTO) {
        int num = examPaperService.sendPaper(sendTestDTO.getId(),sendTestDTO.getTestDTO());
        return ResponseBean.success(num);
    }

}
