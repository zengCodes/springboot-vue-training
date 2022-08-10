package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/22 23:26
 **/
@Data
@Table(name = "exam_resource")
@Accessors(chain = true)
public class FileResource implements Serializable {

    private static final long serialVersionUID = -35096383057142841L;

    @Id
    private Long id;

    /**
     * 附件编码
     */
    private String serialNum;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * md5码标识
     */
    private String identifier;
    /**
     * 附件大小
     */
    private Integer totalSize;
    /**
     * 附件类型
     */
    private String type;
    /**
     * 存储地址
     */
    private String location;
    /**
     * 删除标志 0未删除 1已删除
     */
    private Integer delStatus;

    /**
     * 所属项目
     */
    private String refProject;
    /**
     * 上传人
     */
    private String uploadBy;
    /**
     * 上传时间
     */
    private Date uploadTime;
}
