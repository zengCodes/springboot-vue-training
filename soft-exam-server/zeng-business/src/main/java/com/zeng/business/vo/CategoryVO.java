package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/31 13:17
 **/
@Data
public class CategoryVO {

    @Id
    private Long id;

    private String name;

    private String remark;

    private Integer sort;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;

    private Integer status;

    /**
     * 分类类型
     */
    private String type;

    /**
     * 父级分类id
     */
    private Long parentId;
}
