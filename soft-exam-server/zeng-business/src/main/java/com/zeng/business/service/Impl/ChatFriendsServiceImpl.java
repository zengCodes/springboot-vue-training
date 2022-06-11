package com.zeng.business.service.Impl;


import com.zeng.business.converter.ChatFriendConverter;
import com.zeng.business.entity.ChatFriends;
import com.zeng.business.entity.Course;
import com.zeng.business.mapper.ChatFriendsMapper;
import com.zeng.business.service.ChatFriendsService;
import com.zeng.business.vo.ChatFriendVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.domain.SysUserRole;
import com.zeng.system.mapper.SysUserMapper;
import com.zeng.system.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/18 11:28
 **/
@Service("ChatFriendsService")
public class ChatFriendsServiceImpl implements ChatFriendsService {


    @Resource
    private SysUserMapper userDao;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private ChatFriendsMapper chatFriendsDao;


    /**
     * 查询好友列表
     *
     * @param id
     * @return
     */
    @Override
    public List<ChatFriendVO> getFriends(Boolean isAdmin, Long id) {
        //根据id用户名查询id
        SysUser user = userDao.selectUserById(id);
        if (StringUtils.isNull(user)) {
            throw new CustomException("该用户不存在");
        }
        Example e = new Example(ChatFriends.class);
        Example.Criteria c = e.createCriteria();
        // 判断是否是管理员用户
        if (isAdmin) {
            c.andEqualTo("fAdminId", user.getUserId());
        } else {
            c.andEqualTo("fUserId", user.getUserId());
        }
        List<ChatFriends> chatFriendsList = chatFriendsDao.selectByExample(e);
        List<ChatFriendVO> chatFriendVOS = ChatFriendConverter.converterToVOList(chatFriendsList);
        return chatFriendVOS;
    }

    @Override
    public List<SysUser> selectAllAdminFriends() {
        List<SysUser> sysUserList = new ArrayList<>();
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectAllAdminFriends();
        for (SysUserRole sysUserRole : sysUserRoles) {
            SysUser sysUser = userDao.selectUserById(sysUserRole.getUserId());
            if (StringUtils.isNotNull(sysUser)) {
                sysUserList.add(sysUser);
            }
        }
        return sysUserList;
    }

    /**
     * 状态，1：已添加；0：未添加 2:拒绝
     *
     * @param chatFriendVO
     */
    @Override
    public void appendFriend(ChatFriendVO chatFriendVO) {
        List<ChatFriends> chatFriends;
        Example o = new Example(ChatFriends.class);
        Example.Criteria criteria = o.createCriteria();
        Long friendAccount = userDao.selectUserByUserName(chatFriendVO.getFriendAccount()).getUserId();
        Long userAccount = userDao.selectUserByUserName(chatFriendVO.getUserAccount()).getUserId();
        criteria.andEqualTo("fUserId", userAccount);
        criteria.andEqualTo("fAdminId", friendAccount);
        chatFriends = chatFriendsDao.selectByExample(o);
        if (chatFriends.size() > 0) {
            return;
        } else {
            if (StringUtils.isNotNull(friendAccount) && StringUtils.isNotNull(userAccount)) {
                ChatFriends chatFriend = new ChatFriends();
                chatFriend.setFCreateTime(new Date())
                        .setFAdminId(friendAccount)
                        .setFUserId(userAccount);
                // 默认未添加
                chatFriend.setFStatus(CommonStatusEnum.DISABLE.getStatusCode());
                chatFriendsDao.insert(chatFriend);
            }
        }
    }

    @Override
    public void updateStatus(Long id, Boolean status) {
        ChatFriends chatFriend = chatFriendsDao.selectByPrimaryKey(id);
        if (StringUtils.isNotNull(chatFriend)) {
            chatFriend.setFId(id);
            // 同意 -- 拒绝
            chatFriend.setFStatus(status ? CommonStatusEnum.AVAILABLE.getStatusCode() : CommonStatusEnum.RECOVER.getStatusCode());
            chatFriendsDao.updateByPrimaryKeySelective(chatFriend);
        } else {
            throw new CustomException("该好友记录不存在！");
        }
    }
}
