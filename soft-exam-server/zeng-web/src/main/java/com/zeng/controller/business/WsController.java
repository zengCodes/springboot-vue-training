package com.zeng.controller.business;

import com.zeng.business.service.ChatMsgService;
import com.zeng.business.vo.ChatMsgVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.system.service.ISysUserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/14 19:49
 **/
@Controller
public class WsController {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private ChatMsgService chatMsgService;

    /**
     * 单聊的消息的接受与转发
     *
     * @param chatMsg
     */
    @MessageMapping("/ws/chat")
    public void handleMessage(ChatMsgVO chatMsg) {
        // 查询当前用户信息
        SysUser sysUser = sysUserService.selectUserByUserName(chatMsg.getMFromUser());
        chatMsg.setMFromUser(sysUser.getUserName());
        chatMsg.setMFromAvatar(sysUser.getAvatar());
        chatMsgService.insertChatMsg(chatMsg);
        simpMessagingTemplate.convertAndSend("/queue/chat", chatMsg);
    }

    /**
     * 群聊的消息接受与转发
     *
     * @param chatMsg
     */
    @MessageMapping("/ws/groupChat")
    public void handleGroupMessage(ChatMsgVO chatMsg) {
        // 查询当前用户信息
        SysUser sysUser = sysUserService.selectUserByUserName(chatMsg.getMFromUser());
        chatMsg.setMFromUser(sysUser.getUserName());
        chatMsg.setMFromAvatar(sysUser.getAvatar());
        //保存该条群聊消息记录到数据库中
        chatMsgService.insertGroupChatMsg(chatMsg);
        //转发该条数据
        simpMessagingTemplate.convertAndSend("/topic/greetings", chatMsg);
    }

}
