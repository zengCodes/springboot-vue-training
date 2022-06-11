package com.zeng.business.converter;

import com.zeng.business.entity.ChatFriends;
import com.zeng.business.vo.ChatFriendVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/15 17:31
 **/
@Component
@Data
public class ChatFriendConverter {


    public ChatFriendConverter chatFriendConverter;

    private static SysUserMapper sysUserMapper;

    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        chatFriendConverter.sysUserMapper = sysUserMapper;
    }

    /**
     * 转VOList
     *
     * @param chatFriends
     * @return
     */
    public static List<ChatFriendVO> converterToVOList(List<ChatFriends> chatFriends) {
        List<ChatFriendVO> chatFriendVOArrayList = new ArrayList<>();
        if (!StringUtils.isEmpty(chatFriends)) {
            for (ChatFriends c : chatFriends) {
                ChatFriendVO chatFriendVO = converterToChatFriendVO(c);
                chatFriendVOArrayList.add(chatFriendVO);
            }
        }
        return chatFriendVOArrayList;
    }

    /**
     * 转VO
     *
     * @param chatFriends
     * @return
     */
    public static ChatFriendVO converterToChatFriendVO(ChatFriends chatFriends) {
        ChatFriendVO chatFriendVO = new ChatFriendVO();
        BeanUtils.copyProperties(chatFriends, chatFriendVO);
        // 查询用户名称
        SysUser user = sysUserMapper.selectUserById(chatFriends.getFUserId());
        chatFriendVO.setUserAccount(user.getUserName());
        chatFriendVO.setUserAvatar(user.getAvatar());
        SysUser admin = sysUserMapper.selectUserById(chatFriends.getFAdminId());

        chatFriendVO.setFriendAccount(admin.getUserName());
        chatFriendVO.setFriendAvatar(admin.getAvatar());
        return chatFriendVO;
    }
}
