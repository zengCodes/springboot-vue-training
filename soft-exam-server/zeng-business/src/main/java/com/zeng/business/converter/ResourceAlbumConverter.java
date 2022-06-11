package com.zeng.business.converter;

import com.zeng.business.entity.*;
import com.zeng.business.mapper.FileResourceMapper;
import com.zeng.business.mapper.ResourceAlbumMapper;
import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.business.vo.ResourceAlbumsVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/10 18:24
 **/
@Data
@Component
public class ResourceAlbumConverter {


    public static ResourceAlbumConverter resourceAlbumConverter;


    private static ResourceAlbumMapper resourceAlbumMapper;

    private static FileResourceMapper fileResourceMapper;


    @Autowired
    public void setResourceAlbumConverter(ResourceAlbumMapper resourceAlbumMapper) {
        resourceAlbumConverter.resourceAlbumMapper = resourceAlbumMapper;
    }


    @Autowired
    public void setFileResourceMapper(FileResourceMapper fileResourceMapper) {
        resourceAlbumConverter.fileResourceMapper = fileResourceMapper;
    }

    /**
     * 转VOList
     *
     * @param resourceAlbumList
     * @return
     */
    public static List<ResourceAlbumsVO> converterToVOList(List<ResourceAlbum> resourceAlbumList) {
        List<ResourceAlbumsVO> resourceAlbumsVOList = new ArrayList<>();
        if (StringUtils.isNotNull(resourceAlbumList)) {
            for (ResourceAlbum resourceAlbum : resourceAlbumList) {
                ResourceAlbumsVO resourceAlbumsVO = converterToResourceAlbumsVO(resourceAlbum);
                resourceAlbumsVOList.add(resourceAlbumsVO);
            }
        }
        return resourceAlbumsVOList;
    }

    /**
     * 转VO
     *
     * @param resourceAlbum
     * @return
     */
    public static ResourceAlbumsVO converterToResourceAlbumsVO(ResourceAlbum resourceAlbum) {
        ResourceAlbumsVO resourceAlbumsVO = new ResourceAlbumsVO();
        BeanUtils.copyProperties(resourceAlbum, resourceAlbumsVO);
        // 根据相册id 查询所有所属相片
        Example o = new Example(FileResource.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotNull(resourceAlbum.getId())) {
            criteria.andEqualTo("refProject", String.valueOf("Albums-" + resourceAlbum.getId()));
        }
        List<FileResource> fileResources = fileResourceMapper.selectByExample(o);
        resourceAlbumsVO.setImgList(fileResources);
        return resourceAlbumsVO;
    }

}

