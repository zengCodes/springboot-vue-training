package com.zeng.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/7/7 15:28
 **/
@Data
@ApiModel(value = "前台试卷查询条件")
public class PaperDTO {
    @ApiModelProperty(value = "试卷科目")
    private String name;

    @ApiModelProperty(value = "科目等级")
    private String level;

    @ApiModelProperty(value = "科目试卷年份")
    private String year;

    @ApiModelProperty(value = "科目试卷年度")
    private String annual;

    @ApiModelProperty("试卷开放状态")
    private Integer status;
}
