package com.zeng.business.service;


import com.zeng.business.entity.FileResource;
import com.zeng.business.vo.FileResourceVO;
import com.zeng.business.vo.PageVO;
import org.omg.CORBA.SystemException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface FileResourceService {


    PageVO<FileResourceVO> findFileResourceList(Integer pageNum, Integer pageSize, FileResourceVO fileResourceVO);

    int add(FileResource fileResource);

    List<FileResourceVO> isHasFile(FileResourceVO fileResourceVO);

    /**
     * 删除
     *
     * @param fileResourceVO
     * @return
     */
    int deleteFile(FileResourceVO fileResourceVO) throws SystemException;

    String uploadFile(Long id, String type, MultipartFile file) throws IOException;

    void download(String location, HttpServletResponse resp) throws Exception;
}
