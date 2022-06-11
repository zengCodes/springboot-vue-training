package com.zeng.controller.business;


import com.zeng.business.dto.PaperDTO;
import com.zeng.business.service.PaperService;
import com.zeng.business.vo.PaperQuestionVO;
import com.zeng.business.vo.PaperVO;
import com.zeng.common.core.domain.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/7/7 15:25
 **/
@Api(tags = "业务模块-前台试卷管理接口")
@RestController
@RequestMapping("/business/paper")
@Slf4j
public class ApiPaperController {

    @Resource
    private PaperService paperService;

    /**
     * 全部科目列表
     *
     * @return
     */
    @ApiOperation(value = "试卷列表", notes = "试卷列表,根据科目名称模糊查询")
    @GetMapping("/getAllPaperList")
    public ResponseBean getAllPaperList(PaperDTO paperDTO) {
        List<PaperVO> paperVOS = paperService.getAllPaperList(paperDTO);
        return ResponseBean.success(paperVOS);
    }


    @ApiOperation(value = "试卷列表", notes = "试卷试题列表")
    @GetMapping("/getPaperQuestionList")
    public ResponseBean getPaperQuestionList() {
        List<PaperQuestionVO> objectList = paperService.getPaperQuestionList();
        return ResponseBean.success(objectList);
    }

}
