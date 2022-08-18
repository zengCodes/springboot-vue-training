package com.zeng.business.service.Impl;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zeng.business.service.OssUploadFileService;
import com.zeng.common.config.ZengConfig;
import com.zeng.common.utils.file.FileUploadUtils;
import com.zeng.common.utils.file.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/4/29 15:02
 **/
@Service("OssUploadImageService")
public class OssUploadFileServiceImpl implements OssUploadFileService {

    private Auth auth;

    /**七牛文件上传管理器*/
    private UploadManager uploadManager;
    /**上传的token*/
    private String token;

    private BucketManager bucketManager;

    public OssUploadFileServiceImpl() {
        init();
    }

    private void init() {
        // 我是华南地区的所以是zone2，如果是其他地区的需要修改
        uploadManager = new UploadManager(new Configuration(Region.region2()));
        auth = Auth.create(ZengConfig.getAccessKey(), ZengConfig.getSecretKey());
        // 根据命名空间生成的上传token
        bucketManager = new BucketManager(auth, new Configuration(Region.region2()));
        token = auth.uploadToken(ZengConfig.getBucket());
    }

    @Override
    public String upLoadFile(MultipartFile file){
        String resultImage = "";
        try {
            String name = FileUploadUtils.extractFilename(file);
            System.out.println("----"+name);
            //判断是否为恶意程序
            // 上传文件
            Response res = uploadManager.put(file.getInputStream(), name, token, null, null);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
            // 解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(res.bodyString(), DefaultPutRet.class);
            // 直接返回外链地址
            resultImage = getPrivateFile(name);
        } catch (QiniuException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultImage;
    }

    /**
     * 获取私有空间文件
     *
     * @param fileKey
     * @return
     */
    public String getPrivateFile(String fileKey) {
        String encodedFileName = null;
        String finalUrl = null;
        try {
            encodedFileName = URLEncoder.encode(fileKey, "utf-8").replace("+", "%20");
            String publicUrl = String.format("%s/%s", ZengConfig.getDomain(), encodedFileName);
            //1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return finalUrl;
    }
}
