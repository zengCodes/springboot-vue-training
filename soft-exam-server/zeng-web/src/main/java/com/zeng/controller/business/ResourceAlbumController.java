package com.zeng.controller.business;

import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.service.FileResourceService;
import com.zeng.business.service.ResourceAlbumService;
import com.zeng.business.vo.LessonVO;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.ResourceAlbumsVO;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.bean.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/10 17:01
 **/
@Api(tags = "业务模块-相册管理接口")
@RestController
@RequestMapping("business/resource")
public class ResourceAlbumController {

    @Resource
    private ResourceAlbumService resourceAlbumService;


    @ApiOperation(value = "获取相册列表")
    @GetMapping("/getAlbumImage")
    public ResponseBean getAlbumImage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize") Integer pageSize,
                                      ResourceAlbumsVO resourceAlbum) {
        PageVO<ResourceAlbumsVO> resourceAlbumList = resourceAlbumService.getAlbumImage(pageNum, pageSize, resourceAlbum);
        return ResponseBean.success(resourceAlbumList);
    }


    /**
     * 添加相册
     *
     * @return
     */
    @ApiOperation(value = "添加相册")
    @PostMapping("/add")
    public ResponseBean add(@RequestParam("files") MultipartFile[] files, ResourceAlbumsVO resourceAlbumsVO) throws IOException {
        if (files.length == 0) {
            throw new CustomException("相册文件未上传！");
        }
        int i = resourceAlbumService.add(files, resourceAlbumsVO);
        return ResponseBean.success(i);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id删除相册", notes = "删除课程信息")
    @DeleteMapping("/deleteAlbum/{id}")
    public ResponseBean deleteAlbum(@PathVariable Long id) {
        int i = resourceAlbumService.delete(id);
        return ResponseBean.success(i);
    }



    @ApiOperation(value = "更新相册", notes = "更新相册信息")
    @PutMapping("/update/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean update(@PathVariable Long id, @RequestBody ResourceAlbumsVO resourceAlbumsVO) {
        if (StringUtils.isEmpty(id)) {
            throw new CustomException("相册不能为空");
        }
        int i = resourceAlbumService.update(id, resourceAlbumsVO);
        return ResponseBean.success(i);
    }

    @ApiOperation(value = "恢复相册", notes = "恢复")
    @PutMapping("/updateStatus/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean updateStatus(@PathVariable Long id, @RequestBody ResourceAlbumsVO resourceAlbumsVO) {
        int n = resourceAlbumService.updateStatus(id, resourceAlbumsVO);
        return ResponseBean.success(n);
    }


    @ApiOperation(value = "根据id删除相册相片", notes = "删除相册相片")
    @PutMapping("/deletePhoto/{id}")
    public ResponseBean deletePhoto(@PathVariable Long id) {
        int i = resourceAlbumService.deletePhoto(id);
        return ResponseBean.success(i);
    }


}
