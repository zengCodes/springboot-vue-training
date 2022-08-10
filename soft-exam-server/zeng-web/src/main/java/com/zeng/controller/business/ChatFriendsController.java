package com.zeng.controller.business;


import com.zeng.business.service.ChatFriendsService;
import com.zeng.business.vo.ChatFriendVO;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.domain.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/18 11:26
 **/
@Api(tags = "业务模块-聊天好友管理接口")
@RestController
@RequestMapping("business/chat")
@Slf4j
public class ChatFriendsController {

    @Resource
    private ChatFriendsService chatFriendsService;


    @ApiOperation(value = "获取用户好友", notes = "根据用户id查询用户好友")
    @GetMapping("/getFriends")
    public ResponseBean getFriends(@RequestParam(value = "id") Long id, @RequestParam(value = "isAdmin") Boolean isAdmin) {
        List<ChatFriendVO> chatFriendsList = chatFriendsService.getFriends(isAdmin, id);
        return ResponseBean.success(chatFriendsList);
    }


    @ApiOperation(value = "获取管理员列表", notes = "前台显示管理员列表")
    @GetMapping("/selectAllAdminFriends")
    public ResponseBean getAdminFriends() {
        List<SysUser> sysUsers = chatFriendsService.selectAllAdminFriends();
        return ResponseBean.success(sysUsers);
    }


    /**
     * 添加好友
     *
     * @return
     */
    @ApiOperation(value = "添加好友")
    @PostMapping("/appendFriend")
    public ResponseBean add(@RequestBody ChatFriendVO chatFriendVO) {
        chatFriendsService.appendFriend(chatFriendVO);
        return ResponseBean.success();
    }


    /**
     * 修改好友状态
     *
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "修改好友状态", notes = "同意或者拒绝这两种状态")
    @PutMapping("/updateStatus/{id}")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = Long.class)
    public ResponseBean updateStatus(@PathVariable Long id, @RequestParam(value = "status") Boolean status) {
        chatFriendsService.updateStatus(id, status);
        return ResponseBean.success();
    }

}
