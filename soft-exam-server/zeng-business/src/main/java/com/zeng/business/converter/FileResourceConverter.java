package com.zeng.business.converter;


import com.zeng.business.entity.FileResource;
import com.zeng.business.vo.FileResourceVO;
import com.zeng.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/23 0:49
 **/

@Component
public class FileResourceConverter {

    /**
     * 转VOList
     *
     * @param fileResources
     * @return
     */
    public static List<FileResourceVO> converterToVOList(List<FileResource> fileResources) {
        List<FileResourceVO> fileResourceVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(fileResources)) {
            for (FileResource fileResource : fileResources) {
                FileResourceVO fileResourceVO = converterToFileResourceVOVO(fileResource);
                fileResourceVOS.add(fileResourceVO);
            }
        }
        return fileResourceVOS;
    }

    /**
     * 转VO
     *
     * @param fileResource
     * @return
     */
    public static FileResourceVO converterToFileResourceVOVO(FileResource fileResource) {
        FileResourceVO fileResourceVO = new FileResourceVO();
        fileResourceVO.setSerialNum(fileResource.getSerialNum());
        fileResourceVO.setName(fileResource.getFilename());
        fileResourceVO.setRelativePath(fileResource.getLocation());
        fileResourceVO.setUniqueIdentifier(fileResource.getIdentifier());
        fileResourceVO.setSize(fileResource.getTotalSize());
        fileResourceVO.setUploadTime(fileResource.getUploadTime());
        fileResourceVO.setId(fileResource.getId());
        return fileResourceVO;
    }
}
