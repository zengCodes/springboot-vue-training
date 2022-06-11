package com.zeng.controller.business;

import com.zeng.business.utils.FileUtil;
import com.zeng.business.entity.FileChunk;
import com.zeng.business.entity.FileResource;
import com.zeng.business.service.FileChunkService;
import com.zeng.business.service.FileResourceService;
import com.zeng.business.vo.FileChunkVO;
import com.zeng.business.vo.FileResourceVO;
import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.controller.BaseController;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.domain.UploadResponse;
import com.zeng.common.core.page.TableDataInfo;
import com.zeng.common.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/22 22:40
 **/
@Api(tags = "业务模块-资源管理接口")
@RestController
@RequestMapping("/business/resource")
@Slf4j
public class FileResourceController extends BaseController {

    @Value("${zeng.fileFolder}")
    private String uploadFolder;

    @Resource
    private FileResourceService fileResourceService;

    @Resource
    private FileChunkService chunkService;

    /**
     * 普通文件
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传文件")
    @PostMapping("/file")
    public ResponseBean uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) throws IOException {
        String realPath = fileResourceService.uploadFile(null, type, file);
        return ResponseBean.success(realPath);
    }


    /**
     * 查询列表
     *
     * @return ApiResult
     */
    @ApiOperation(value = "文件列表", notes = "文件列表,模糊查询")
    @GetMapping("/findFileResourceList")
    public ResponseBean findFileResourceList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize") Integer pageSize,
                                             FileResourceVO fileResourceVO) {
        PageVO<FileResourceVO> fileResourceVOPageVO = fileResourceService.findFileResourceList(pageNum, pageSize, fileResourceVO);
        return ResponseBean.success(fileResourceVOPageVO);
    }

    /**
     * 上传文件块
     *
     * @param fileChunkVO
     * @return
     */
    @PostMapping("/upload/chunk")
    public ResponseBean uploadChunk(FileChunkVO fileChunkVO) {
        FileChunk fileChunk = new FileChunk();
        String apiRlt = "200";
        Path path = null;
        MultipartFile file = fileChunkVO.getUploadFile();
        try {
            byte[] bytes = file.getBytes();
            BeanUtils.copyProperties(fileChunkVO, fileChunk);
            path = Paths.get(FileUtil.generatePath(uploadFolder, fileChunk));
            //文件写入指定路径
            Files.write(path, bytes);
            if (chunkService.saveChunk(fileChunkVO) < 0) apiRlt = "415";

        } catch (IOException e) {
            e.printStackTrace();
            apiRlt = "415";
        }
        return ResponseBean.success(apiRlt);
    }

    @GetMapping("/upload/chunk")
    public UploadResponse checkChunk(FileChunkVO chunk, HttpServletResponse response) {
        UploadResponse ur = new UploadResponse();
        //默认返回其他状态码，前端不进去checkChunkUploadedByResponse函数，正常走标准上传
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

        String file = uploadFolder + "/upload/file/" + chunk.getIdentifier() + "/" + chunk.getFilename();

        //先判断整个文件是否已经上传过了，如果是，则告诉前端跳过上传，实现秒传
        if (FileUtil.fileExists(file)) {
            ur.setSkipUpload(true);
            ur.setLocation(file);
            response.setStatus(HttpServletResponse.SC_OK);
            ur.setMessage("完整文件已存在，直接跳过上传，实现秒传");
            return ur;
        }
        //如果完整文件不存在，则去数据库判断当前哪些文件块已经上传过了，把结果告诉前端，跳过这些文件块的上传，实现断点续传
        ArrayList<Integer> list = chunkService.checkChunk(chunk);
        if (list != null && list.size() > 0) {
            ur.setSkipUpload(false);
            ur.setUploadedChunks(list);
            response.setStatus(HttpServletResponse.SC_OK);
            ur.setMessage("部分文件块已存在，继续上传剩余文件块，实现断点续传");
            return ur;
        }
        return ur;
    }

    @PostMapping("/upload/mergeFile")
    public ResponseBean mergeFile(@RequestBody FileResourceVO fileResourceVO) {
        String rlt = "FALSE";
        //前端组件参数转换为model对象
        FileResource fileResource = new FileResource();
        fileResource.setFilename(fileResourceVO.getName());
        fileResource.setIdentifier(fileResourceVO.getUniqueIdentifier());
        fileResource.setSerialNum(fileResourceVO.getSerialNum());
        fileResource.setTotalSize(fileResourceVO.getSize());
        fileResource.setRefProject(fileResourceVO.getRefProject());
        fileResource.setUploadTime(new Date());
        //进行文件的合并操作
        String filename = fileResource.getFilename();
        String file = uploadFolder + "/upload/file/" + fileResource.getIdentifier() + "/" + filename;
        String folder = uploadFolder + "/upload/file/" + fileResource.getIdentifier();
        String fileSuccess = FileUtil.merge(file, folder, filename);
        String path = "/upload/file/" + fileResource.getIdentifier() + "/" + filename;
        fileResource.setLocation(path);

        //文件合并成功后，保存记录至数据库
        if ("200".equals(fileSuccess)) {
            if (fileResourceService.add(fileResource) > 0) rlt = "SUCCESS";
        }
        //如果已经存在，则判断是否同一个项目，同一个项目的不用新增记录，否则新增
        if ("300".equals(fileSuccess)) {
            List<FileResourceVO> fileResourceVOPageVO = fileResourceService.isHasFile(fileResourceVO);
            if (fileResourceVOPageVO != null) {
                if (fileResourceVOPageVO.size() == 0 || (fileResourceVOPageVO.size() > 0 && !fileResourceVO.getRefProject().equals(fileResourceVOPageVO.get(0).getRefProject()))) {
                    if (fileResourceService.add(fileResource) > 0) rlt = "SUCCESS";
                }
            }
        }
        return ResponseBean.success(rlt, path);
    }

    /**
     * 删除
     */
    @PostMapping("/deleteFile")
    @ApiOperation(value = "删除文件", notes = "更改状态")
    public ResponseBean deleteFile(@RequestBody FileResourceVO fileResourceVO) throws SystemException {
        int result = fileResourceService.deleteFile(fileResourceVO);
        return ResponseBean.success(result);
    }
}
