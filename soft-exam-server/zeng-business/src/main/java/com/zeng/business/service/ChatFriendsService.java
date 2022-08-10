package com.zeng.business.service;


import com.zeng.business.entity.ChatFriends;
import com.zeng.business.vo.ChatFriendVO;
import com.zeng.common.core.domain.entity.SysUser;

import java.util.List;

public interface ChatFriendsService {

    //查询用户好友
    List<ChatFriendVO> getFriends(Boolean isAdmin, Long id);

    // 查询所有管理员
    List<SysUser> selectAllAdminFriends();

    void appendFriend(ChatFriendVO chatFriendVO);

    void updateStatus(Long id, Boolean status);
}
