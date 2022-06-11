package com.zeng.business.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssUploadFileService {

    String upLoadFile(MultipartFile file);
}
