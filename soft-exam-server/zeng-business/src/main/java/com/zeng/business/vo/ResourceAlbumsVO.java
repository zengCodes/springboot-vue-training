package com.zeng.business.vo;

import com.zeng.business.entity.FileResource;
import lombok.Data;

import javax.persistence.Id;
import java.util.List;


/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/10 16:13
 **/
@Data
public class ResourceAlbumsVO {


    @Id
    private Long id;

    /**
     * 相册名称
     */
    private String albumName;

    /**
     * 相册描述
     */
    private String albumDesc;

    /**
     * 相册类型
     */
    private Long albumType;

    private Integer flag;

    private List<FileResource> imgList;
}
