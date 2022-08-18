package com.zeng.system.domain.vo;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/7/15 23:14
 **/
public class AvatarFileVo {

    private MultipartFile avatarFile;

    public MultipartFile getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(MultipartFile file){
        this.avatarFile = file;
    }
}
