package com.zeng.business.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * (ExamResourceAlbum)实体类
 *
 * @author zenghuiqing
 * @since 2021-12-10 13:21:55
 */
@Data
@Table(name = "exam_resource_album")
public class ResourceAlbum implements Serializable {
    private static final long serialVersionUID = -92941044874072589L;
    /**
     * 相册编号id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
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
}
