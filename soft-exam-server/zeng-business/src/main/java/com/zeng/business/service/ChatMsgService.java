package com.zeng.business.service;


import com.zeng.business.dto.ChatMsgDTO;
import com.zeng.business.entity.ChatMsg;
import com.zeng.business.vo.ChatMsgVO;
import org.omg.CORBA.SystemException;

import java.util.List;


public interface ChatMsgService {

    void insertChatMsg(ChatMsgVO chatMsgVO);

    List<ChatMsg> LookTwoUserMsg(ChatMsg chatMsg);

    List<ChatMsg> getFriendMsg(String username, String friend);

    List<ChatMsg> getAdminMsg(ChatMsgDTO chatMsgDTO);

    List<ChatMsg> getAllMsgByAccount(ChatMsgDTO chatMsgDTO);

    void insertGroupChatMsg(ChatMsgVO chatMsg);

    List<ChatMsg> getAllMsg();
}
