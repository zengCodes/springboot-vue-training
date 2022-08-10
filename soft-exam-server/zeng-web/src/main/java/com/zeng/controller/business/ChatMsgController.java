package com.zeng.controller.business;


import com.zeng.business.dto.ChatMsgDTO;
import com.zeng.business.entity.ChatMsg;
import com.zeng.business.service.ChatMsgService;
import com.zeng.business.vo.ChatMsgVO;
import com.zeng.common.core.domain.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/17 22:34
 **/
@Api(tags = "业务模块-聊天消息管理接口")
@RestController
@RequestMapping("business/chat")
@Slf4j
public class ChatMsgController {

    @Resource
    private ChatMsgService chatMsgService;


    @ApiOperation(value = "获取与管理员聊天记录", notes = "根据当前登录用户查询与管理员的聊天记录")
    @PostMapping("/getChatMsg")
    public ResponseBean getAdminMsg(@RequestBody ChatMsgDTO chatMsgDTO) {
        List<ChatMsg> chatMsgs = chatMsgService.getAdminMsg(chatMsgDTO);
        return ResponseBean.success(chatMsgs);
    }

    @ApiOperation(value = "获取聊天记录", notes = "根据当前登录用户查询聊天记录")
    @PostMapping("/getAllMsgByAccount")
    public ResponseBean getAllMsgByAccount(@RequestBody ChatMsgDTO chatMsgDTO) {
        List<ChatMsg> chatMsgVOS = chatMsgService.getAllMsgByAccount(chatMsgDTO);
        return ResponseBean.success(chatMsgVOS);
    }

    @ApiOperation(value = "获取聊天记录", notes = "查询所有聊天记录")
    @GetMapping("/getAllMsg")
    public ResponseBean getAllMsg() {
        List<ChatMsg> chatMsgVOS = chatMsgService.getAllMsg();
        return ResponseBean.success(chatMsgVOS);
    }

}
