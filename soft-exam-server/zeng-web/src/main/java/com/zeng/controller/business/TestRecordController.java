package com.zeng.controller.business;

import com.zeng.business.service.TestRecordService;
import com.zeng.business.vo.*;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.enums.CommonStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/19 15:54
 **/
@Api(tags = "业务模块-考试记录管理接口")
@RequestMapping("business/testRecord")
@RestController
public class TestRecordController {


    @Resource
    private TestRecordService testRecordService;


    @ApiOperation(value = "获取记录列表")
    @GetMapping("/getRecordList")
    public ResponseBean getRecordList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize") Integer pageSize,
                                     TestRecordVO testRecordVO) {
        PageVO<TestRecordVO> orderList = testRecordService.getRecordList(pageNum, pageSize, testRecordVO);
        return ResponseBean.success(orderList);
    }


    @ApiOperation(value = "获取题目记录列表")
    @GetMapping("/getPracticeList")
    public ResponseBean getOrderList(@RequestParam(value = "userId") Long userId,
                                     @RequestParam(value = "testId") Long testId) {
        List<UserPracticeVO> userPracticeVOS = testRecordService.getPracticeList(userId, testId);
        return ResponseBean.success(userPracticeVOS);
    }


    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加考试记录")
    @PostMapping("/saveTestRecord")
    public ResponseBean saveTestRecord(@RequestBody @Validated TestRecordVO testRecordVO) {
        Long id =  testRecordService.saveRecord(CommonStatusEnum.AVAILABLE.getStatusCode(), testRecordVO);
        return ResponseBean.success(id);
    }


    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加练习记录")
    @PostMapping("/saveExerciseRecord")
    public ResponseBean saveExerciseRecord(@RequestBody @Validated TestRecordVO testRecordVO) {
        testRecordService.saveRecord(CommonStatusEnum.DISABLE.getStatusCode(), testRecordVO);
        return ResponseBean.success();
    }


    /**
     * 根据订单id订单信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取考试信息", notes = "获取考试信息")
    @GetMapping("/getTestInfo/{id}")
    public ResponseBean getTestInfo(@PathVariable Long id) {
        TestRecordVO testRecordVO = testRecordService.getTestInfo(id);
        return ResponseBean.success(testRecordVO);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除考生试卷", notes = "删除考生试卷")
    @DeleteMapping("/delete/{id}")
    public ResponseBean delete(@PathVariable Long id) {
        int msg = testRecordService.delete(id);
        return ResponseBean.success(msg);
    }


}
