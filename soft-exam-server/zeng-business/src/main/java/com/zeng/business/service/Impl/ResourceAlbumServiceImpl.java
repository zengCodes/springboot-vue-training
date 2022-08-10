package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.ResourceAlbumConverter;
import com.zeng.business.entity.FileResource;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.entity.ResourceAlbum;
import com.zeng.business.mapper.FileResourceMapper;
import com.zeng.business.mapper.ResourceAlbumMapper;
import com.zeng.business.service.FileResourceService;
import com.zeng.business.service.ResourceAlbumService;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.ResourceAlbumsVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/10 13:25
 **/
@Service("ResourceAlbumService")
public class ResourceAlbumServiceImpl implements ResourceAlbumService {

    @Resource
    private ResourceAlbumMapper resourceAlbumMapper;


    @Resource
    private FileResourceMapper fileResourceMapper;

    @Resource
    private FileResourceService fileResourceService;


    @Override
    public PageVO<ResourceAlbumsVO> getAlbumImage(Integer pageNum, Integer pageSize, ResourceAlbumsVO resourceAlbumVO) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(ResourceAlbum.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotNull(resourceAlbumVO.getAlbumType())) {
            criteria.andEqualTo("albumType", resourceAlbumVO.getAlbumType());
        }
        // 状态查询
        criteria.andEqualTo("flag",resourceAlbumVO.getFlag());
        List<ResourceAlbum> resourceAlbumList = resourceAlbumMapper.selectByExample(o);
        // 查询每个相册对应的相片
        List<ResourceAlbumsVO> resourceAlbumsVOList = ResourceAlbumConverter.converterToVOList(resourceAlbumList);
        PageInfo<ResourceAlbumsVO> resourceAlbumsVOPageInfo = new PageInfo<>(resourceAlbumsVOList);
        return new PageVO<>(resourceAlbumsVOPageInfo.getTotal(), resourceAlbumsVOList);
    }

    @Override
    public int update(Long id, ResourceAlbumsVO resourceAlbumsVO) {
        ResourceAlbum resourceAlbum = new ResourceAlbum();
        BeanUtils.copyProperties(resourceAlbumsVO, resourceAlbum);
        List<FileResource> imgList = resourceAlbumsVO.getImgList();
        if (resourceAlbumsVO.getImgList().size() > 0) {
            // 查询每个img，并更新
            for(FileResource fileResource:imgList){
                FileResource file = new FileResource();
                BeanUtils.copyProperties(fileResource, file);
                file.setId(fileResource.getId());
                file.setLocation(fileResource.getLocation().replaceAll("/dev-api", ""));
                fileResourceMapper.updateByPrimaryKey(file);
            }
        }
        resourceAlbum.setId(id);
        int n = resourceAlbumMapper.updateByPrimaryKey(resourceAlbum);
        return n;
    }

    @Override
    public int updateStatus(Long id, ResourceAlbumsVO resourceAlbumsVO) {
        ResourceAlbum resourceAlbum = resourceAlbumMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(resourceAlbum)) {
            throw new CustomException("要更新状态的相册不存在");
        } else {
            // 更新相册
            resourceAlbum.setFlag(resourceAlbumsVO.getFlag());
            int n = resourceAlbumMapper.updateByPrimaryKey(resourceAlbum);
            // 更新相册中的照片
            if(resourceAlbumsVO.getImgList().size()>0){
                for (FileResource fileResource : resourceAlbumsVO.getImgList()) {
                    fileResource.setDelStatus(resourceAlbumsVO.getFlag());
                    fileResource.setLocation(fileResource.getLocation().replaceAll("/dev-api", ""));
                    fileResourceMapper.updateByPrimaryKey(fileResource);
                }
            }
            return n;
        }
    }


    @Override
    public int add(MultipartFile[] files, ResourceAlbumsVO resourceAlbumsVO) throws IOException {
        ResourceAlbum resourceAlbum = new ResourceAlbum();
        BeanUtils.copyProperties(resourceAlbumsVO, resourceAlbum);
        resourceAlbum.setAlbumType(resourceAlbum.getAlbumType());
        // 删除标志默认为0
        resourceAlbum.setFlag(CommonStatusEnum.DISABLE.getStatusCode());
        // 插入相册
        int i = resourceAlbumMapper.insert(resourceAlbum);
        // 插入照片
        for (MultipartFile file : files) {
            fileResourceService.uploadFile(resourceAlbum.getId(), "Albums", file);
        }
        return i;
    }

    @Override
    public int delete(Long id) {
        int num;
        ResourceAlbum resourceAlbum = resourceAlbumMapper.selectByPrimaryKey(id);
        Example o = new Example(FileResource.class);
        Example.Criteria criteria = o.createCriteria();
        // 查询相册下的照片
        if (StringUtils.isNotNull(resourceAlbum.getId())) {
            criteria.andEqualTo("refProject", String.valueOf("Albums-" + resourceAlbum.getId()));
        }
        List<FileResource> fileResources = fileResourceMapper.selectByExample(o);
        // 若相册下存在照片则逻辑删除相册和照片
        for (FileResource fileResource : fileResources) {
            fileResource.setDelStatus(CommonStatusEnum.AVAILABLE.getStatusCode());
            fileResourceMapper.updateByPrimaryKey(fileResource);
        }
        if (fileResources.size() > 0) {
            resourceAlbum.setId(id);
            resourceAlbum.setFlag(CommonStatusEnum.AVAILABLE.getStatusCode());
            num = resourceAlbumMapper.updateByPrimaryKey(resourceAlbum);
        } else {
            // 若相册下不存在照片则直接删除
            num = resourceAlbumMapper.delete(resourceAlbum);
        }
        return num;
    }

    @Override
    public int deletePhoto(Long id) {
        FileResource fileResource = fileResourceMapper.selectByPrimaryKey(id);
        fileResource.setRefProject(null);
        int num = fileResourceMapper.updateByPrimaryKey(fileResource);
        return num;
    }

}
