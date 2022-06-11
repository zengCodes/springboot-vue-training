package com.zeng.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/15 15:07
 **/
@Data
@ApiModel(description = "websocket聊天消息内容")
public class ChatMsgVO {


    @ApiModelProperty(value = "发送者")
    private String mFromUser;

    @ApiModelProperty(value = "接收者")
    private String mToUser;

    @ApiModelProperty(value = "发送者头像")
    private String mFromAvatar;

    @ApiModelProperty(value = "接收者头像")
    private String mToAvatar;

    @ApiModelProperty(value = "消息")
    private String mMsg;

    @ApiModelProperty(value = "消息类型")
    private Integer mType;

    @ApiModelProperty(value = "窗口类型")
    private Integer mWindow;

}
