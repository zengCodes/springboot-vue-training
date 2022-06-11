package com.zeng.business.service;

import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.ResourceAlbumsVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface ResourceAlbumService {

    PageVO<ResourceAlbumsVO> getAlbumImage(Integer pageNum, Integer pageSize, ResourceAlbumsVO resourceAlbumVO);

    int add(MultipartFile[] files, ResourceAlbumsVO resourceAlbumsVO) throws IOException;

    int delete(Long id);

    int update(Long id, ResourceAlbumsVO resourceAlbumsVO);

    int updateStatus(Long id, ResourceAlbumsVO resourceAlbumsVO);

    int deletePhoto(Long id);
}
