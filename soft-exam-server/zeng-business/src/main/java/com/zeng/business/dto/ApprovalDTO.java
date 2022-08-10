package com.zeng.business.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/26 16:51
 **/
@Data
@ApiModel(value = "讲师审核内容")
public class ApprovalDTO {

    private Integer approvalStatus;

    private String comment;
}
