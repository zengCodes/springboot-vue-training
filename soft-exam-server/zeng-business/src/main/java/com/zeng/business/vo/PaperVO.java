package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/17 14:01
 **/
@Data
@Accessors(chain = true)
public class PaperVO {

    @Id
    private Long id;

    private String name;

    private Long courseId;

    private String course;

    private Integer questionCount;

    private Integer exerciseNum;

    private Integer score;

    private Integer suggestTime;

    private String time;

    private String user;

    private Boolean status;

    private String level;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;
}
