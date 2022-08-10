package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zeng.common.core.domain.entity.SysUser;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/7 13:13
 **/

@Data
public class DiscussTreeNode {

    @Id
    private Long id;

    private Long parentId;

    private QuestionVO question;

    private String articleContent;

    private SysUser createBy;

    private Integer likeNum;

    private Integer browseNum;

    private Integer discussNum;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;

    private List<DiscussTreeNode> children;

}
