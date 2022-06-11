package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/1 15:08
 **/
@Data
public class CourseVO {

    @Id
    private Long id;

    @NotBlank
    private String name;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date time;

    private String annual;

    private String remark;

    private String img;

    private Integer sort;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    private Long[] categoryKeys;

    private Long oneCategoryId;

    private Long twoCategoryId;

    private Integer status;
}
