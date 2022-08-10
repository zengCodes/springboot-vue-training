package com.zeng.controller.business;

import com.zeng.business.entity.Paper;
import com.zeng.business.service.PaperService;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.PaperVO;
import com.zeng.common.core.controller.BaseController;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.page.TableDataInfo;
import com.zeng.common.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/12 1:15
 **/
@Api(tags = "业务模块-试卷管理接口")
@RestController
@RequestMapping("business/paper")
@Slf4j
public class PaperController extends BaseController {

    @Resource
    private PaperService paperService;


    @ApiOperation(value = "下载word模板", notes = "下载模板")
    @RequestMapping("/downloadTemplate")
    public void downloadTemplate(@RequestParam("type") String type, HttpServletResponse response) {
        paperService.downloadTemplate(type, response);
    }


    @ApiOperation(value = "word文档导入上午题试卷", notes = "导入上午题试卷")
    @ResponseBody
    @RequestMapping("/importMorningPracticeWord")
    public ResponseBean importMorningPracticeWord(@RequestParam("wordTemplate") MultipartFile wordFile, HttpServletRequest request) throws IOException {
        Paper paper = paperService.importMorningPracticeWord(wordFile, request);
        return ResponseBean.success(paper);
    }


    @ApiOperation(value = "word文档导入下午题试卷", notes = "导入下午题试卷")
    @ResponseBody
    @RequestMapping("/importAfternoonPracticeWord")
    public ResponseBean importAfternoonPracticeWord(@RequestParam("wordTemplate") MultipartFile wordFile, HttpServletRequest request) throws IOException, TransformerException, ParserConfigurationException, ParseException {
        Paper paper = paperService.importAfternoonPracticeWord(wordFile, request);
        return ResponseBean.success(paper);
    }

    /**
     * 更新角色状态
     *
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "更新状态", notes = "开放和禁用两种状态")
    @PutMapping("/updateStatus/{id}/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataTypeClass = Boolean.class),
    })
    public ResponseBean updateStatus(@PathVariable Long id, @PathVariable Boolean status) {
        paperService.updateStatus(id, status);
        return ResponseBean.success();
    }

    /**
     * 导出试卷
     *
     * @param id
     * @param res
     * @throws IOException
     * @throws
     */
    @RequestMapping(value = "/exportPracticeWord", method = RequestMethod.GET)
    @ApiOperation(value = "导出试卷到word", notes = "导出试卷")
    public void exportPracticeWord(@RequestParam(value = "id") Long id, HttpServletResponse res) throws IOException {
        paperService.exportPracticeWord(id, res);
    }

    @ApiOperation(value = "试卷列表")
    @GetMapping("/getPaperList")
    public ResponseBean getPaperList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize") Integer pageSize) {

        PageVO<PaperVO> pageVO = paperService.getPaperList(pageNum, pageSize);
        return ResponseBean.success(pageVO);
    }


    /**
     * 前台练习 - 全部试卷列表
     *
     * @return
     */
    @ApiOperation(value = "试卷列表", notes = "试卷列表,根据科目名称模糊查询")
    @GetMapping("/findPaperList")
    public ResponseBean findPaperList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize") Integer pageSize,
                                      PaperVO paperVO
    ) {
        PageVO<PaperVO> paperVOPageVO = paperService.findPaperList(pageNum, pageSize, paperVO);
        return ResponseBean.success(paperVOPageVO);
    }


    /**
     * 添加科目
     *
     * @return
     */
    @ApiOperation(value = "添加试卷")
    @PostMapping("/add")
    public ResponseBean add(@RequestBody @Validated PaperVO paperVO) {
        if (paperVO.getCourseId() == null) {
            throw new CustomException("考试科目不能为空");
        }
        paperService.add(paperVO);
        return ResponseBean.success(paperService.add(paperVO));
    }

    /**
     * 编辑试卷
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑试卷", notes = "编辑试卷信息")
    @GetMapping("/edit/{id}")
    public ResponseBean edit(@PathVariable Long id) {
        PaperVO paperVO = paperService.edit(id);
        return ResponseBean.success(paperVO);
    }

    /**
     * 更新试卷
     *
     * @return
     */
    @ApiOperation(value = "更新试卷", notes = "更新试卷信息")
    @PutMapping("/update/{id}")
    public ResponseBean update(@PathVariable Long id, @RequestBody PaperVO paperVO) {
        if (paperVO.getCourseId() == null) {
            throw new CustomException("考试科目不能为空");
        }
        paperService.update(id, paperVO);
        return ResponseBean.success();
    }

    /**
     * 删除科目
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除科目", notes = "删除试卷信息")
    @DeleteMapping("/delete/{id}")
    public ResponseBean delete(@PathVariable Long id) {
        int msg = paperService.delete(id);
        return ResponseBean.success(msg);
    }

}
