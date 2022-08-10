package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/23 0:41
 **/
@Data
public class FileResourceVO implements Serializable {

    private static final long serialVersionUID = -668666237985927833L;


    @Id
    private Long id;

    //附件编号
    private String serialNum;

    //附件类型
    private String fileType;

    //附件名称
    private String name;

    //附件总大小
    private Integer size;

    //附件地址
    private String relativePath;

    //附件MD5标识
    private String uniqueIdentifier;

    //附件所属项目 项目-id
    private String refProject;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date uploadTime;

}
