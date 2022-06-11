package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamChatFriends)实体类
 *
 * @author zenghuiqing
 * @since 2021-05-16 12:38:31
 */
@Data
@Table(name = "exam_chat_friends")
@Accessors(chain = true)
public class ChatFriends implements Serializable {
    private static final long serialVersionUID = -63413137292353724L;
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
     * 创建时间
     */
    private Date fCreateTime;
    /**
     * 好友备注
     */
    private String fDesc;

    //  状态，1：已添加；0：未添加 2:拒绝
    private Integer fStatus;
}
