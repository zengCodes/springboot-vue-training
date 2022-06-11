package com.zeng.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/30 15:51
 **/
@Data
@ApiModel(value = "用户查询与管理员的聊天记录")
public class ChatMsgDTO {
    @NotBlank(message = "当前用户名不能为空")
    @ApiModelProperty(value = "用户名")
    private String currentAccount;

    @NotBlank(message = "管理员不能为空")
    @ApiModelProperty(value = "管理员")
    private String adminAccount;


    private Integer window;


}
