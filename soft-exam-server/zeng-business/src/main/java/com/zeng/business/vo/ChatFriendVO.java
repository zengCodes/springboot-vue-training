package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/15 14:56
 **/
@Data
public class ChatFriendVO {


    /**
     * 好友列表id
     */
    @Id
    private Long fId;

    /**
     * 用户id
     */
    private Long fUserId;
    /**
     * 管理员id
     */
    private Long fAdminId;
    /**
     * 用户账号
     */
    private String userAccount;

    private String userAvatar;

    /**
     * 管理员账号
     */
    private String friendAccount;


    private String friendAvatar;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date fCreateTime;
    /**
     * 好友备注
     */
    private String fDesc;

    private Integer fStatus;

}
