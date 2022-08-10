package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zeng.common.core.domain.entity.SysUser;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/4 10:53
 **/
@Data
public class DiscussVO {

    @Id
    private Long id;

    private Long parentId;

    private Long question;

    private String questionName;

    private Long type;

    private String title;

    private SysUser createBy;

    private String articleContent;

    private Integer likeNum;

    private Integer browseNum;

    private Integer discussNum;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;

}
