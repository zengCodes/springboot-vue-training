package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.FileResourceConverter;
import com.zeng.business.entity.FileResource;
import com.zeng.business.entity.SnowflakeIdWorker;
import com.zeng.business.mapper.FileResourceMapper;
import com.zeng.business.service.FileResourceService;
import com.zeng.business.service.OssUploadFileService;
import com.zeng.business.vo.FileResourceVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/22 23:08
 **/

@Service("FileResourceService")
@Slf4j
public class FileResourceServiceImpl implements FileResourceService {

    @Resource
    private FileResourceMapper fileResourceDao;

    @Resource
    private OssUploadFileService ossUploadFileService;


//    @Override
//    public String uploadFile(Long id, String type, MultipartFile file) throws IOException {
//        if (file.isEmpty()) {
//            throw new CustomException("上传的文件不能为空");
//        }
//        String img = FileUploadUtils.upload(ZengConfig.getUploadPath(), file);
//        if (StringUtils.isNotNull(img)) {
//            // 保存到数据库
//            FileResource fileResource = new FileResource();
//            fileResource.setFilename(file.getOriginalFilename())
//                    .setTotalSize((int) file.getSize())
//                    .setUploadTime(new Date())
//                    .setRefProject(type + "-" + id)
//                    .setLocation(img);
//            if (add(fileResource) > 0) {
//                return img;
//            } else {
//                throw new CustomException("上传的图片保存失败");
//            }
//        }
//        return null;
//    }
    @Override
    public String uploadFile(Long id, String type, MultipartFile file){
        if (file.isEmpty()) {
            throw new CustomException("上传的文件不能为空");
        }
        String img = ossUploadFileService.upLoadFile(file);
        if (StringUtils.isNotNull(img)) {
            // 保存到数据库
            FileResource fileResource = new FileResource();
            fileResource.setFilename(file.getOriginalFilename())
                .setTotalSize((int) file.getSize())
                .setUploadTime(new Date())
                .setRefProject(type + "-" + id)
                .setLocation(img);
            if (add(fileResource) > 0) {
                return img;
            } else {
                throw new CustomException("上传的文件保存失败");
            }
        }
        return null;
    }

    @Override
    public PageVO<FileResourceVO> findFileResourceList(Integer pageNum, Integer pageSize, FileResourceVO fileResourceVO) {
        PageHelper.startPage(pageNum, pageSize);
        String fileName = fileResourceVO.getName();
        String fileType = fileResourceVO.getFileType();
        List<FileResource> fileResources;
        Example o = new Example(FileResource.class);
        Example.Criteria criteria = o.createCriteria();
        if (!StringUtils.isEmpty(fileName)) {
            criteria.andLike("filename", "%" + fileName + "%");
        }
        if (!StringUtils.isEmpty(fileType)) {
            criteria.andEqualTo("type", fileType.substring(fileType.indexOf(".") + 1));
        }
        criteria.andEqualTo("delStatus", CommonStatusEnum.DISABLE.getStatusCode());
        fileResources = fileResourceDao.selectByExample(o);
        List<FileResourceVO> fileResourceVOS = FileResourceConverter.converterToVOList(fileResources);
        PageInfo<FileResource> info = new PageInfo<>(fileResources);
        return new PageVO<>(info.getTotal(), fileResourceVOS);
    }


    @Override
    public int add(FileResource fileResource) {
        String name = fileResource.getFilename();
        fileResource.setSerialNum(SnowflakeIdWorker.getUUID() + SnowflakeIdWorker.getUUID())
                .setType(name.substring(name.indexOf(".") + 1))
                .setUploadBy(SecurityUtils.getUsername())
                .setDelStatus(CommonStatusEnum.DISABLE.getStatusCode());
        return fileResourceDao.insert(fileResource);
    }


    @Override
    public List<FileResourceVO> isHasFile(FileResourceVO fileResourceVO) {
        @NotNull(message = "文件名称不能为空") String fileName = fileResourceVO.getName();
        @NotNull(message = "文件md5标识不能为空") String identifier = fileResourceVO.getUniqueIdentifier();
        List<FileResource> fileResources;
        Example o = new Example(FileResource.class);
        Example.Criteria criteria = o.createCriteria();
        if (!StringUtils.isEmpty(fileName)) {
            criteria.andEqualTo("filename", fileName);
        }
        if (!StringUtils.isEmpty(identifier)) {
            criteria.andEqualTo("identifier", identifier);
        }
        fileResources = fileResourceDao.selectByExample(o);
        List<FileResourceVO> fileResourceVOS = FileResourceConverter.converterToVOList(fileResources);
        return fileResourceVOS;
    }


    @Override
    public int deleteFile(FileResourceVO fileResourceVO) {
        Long id = fileResourceVO.getId();
        FileResource fileResource = fileResourceDao.selectByPrimaryKey(id);
        if (fileResource == null) {
            throw new CustomException("文件不存在");
        }
        FileResource f = new FileResource();
        f.setId(fileResourceVO.getId())
                .setDelStatus(1);
        int i = fileResourceDao.updateByPrimaryKeySelective(f);
        return i;
    }


    @Override
    public void download(String id, HttpServletResponse response) throws Exception {
        FileResource fileResource = fileResourceDao.selectByPrimaryKey(Integer.parseInt(id));
        URL url;
        URLConnection conn;
        InputStream inputStream = null;
        try {
            //下载时文件名称
            String[] footerArray = fileResource.getLocation().split("\\.");
            String fileName = fileResource.getFilename() + "." + footerArray[footerArray.length - 1];
            // 这里填文件的url地址
            url = new URL(fileResource.getLocation());
            conn = url.openConnection();
            inputStream = conn.getInputStream();
            response.setContentType(conn.getContentType());
            // 使用URLEncoder.encode(fileName, "UTF-8")将文件名设置为UTF-8的编码格式
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
            byte[] buffer = new byte[1024];
            int len;
            OutputStream outputStream = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            // 如果发生异常，返回自定义的下载失败的提示信息
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
