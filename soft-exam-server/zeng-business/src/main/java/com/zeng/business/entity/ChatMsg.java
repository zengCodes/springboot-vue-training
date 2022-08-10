package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ExamChatMsg)实体类
 *
 * @author zenghuiqing
 * @since 2021-05-15 15:58:15
 */
@Data
@Table(name = "exam_chat_msg")
@Accessors(chain = true)
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = -56528138554118272L;
    /**
     * 消息id
     */
    @Id
    private Long mId;
    /**
     * 发送者
     */
    private String mFromUser;
    /**
     * 接收者
     */
    private String mToUser;
    /**
     * 发送者头像
     */
    private String mFromAvatar;
    /**
     * 接收者头像
     */
    private String mToAvatar;
    /**
     * 消息
     */
    private String mMsg;
    /**
     * 创建时间
     */
    private Date mCreateTime;
    /**
     * 消息类型
     */
    private Integer mType;

    /**
     * 窗口类型 0：单聊 1：群聊
     */
    private Integer mWindow;

}
