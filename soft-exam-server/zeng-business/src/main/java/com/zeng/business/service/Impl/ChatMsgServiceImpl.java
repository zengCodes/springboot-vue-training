package com.zeng.business.service.Impl;


import com.zeng.business.dto.ChatMsgDTO;
import com.zeng.business.entity.ChatMsg;
import com.zeng.business.mapper.ChatMsgMapper;
import com.zeng.business.service.ChatMsgService;
import com.zeng.business.vo.ChatMsgVO;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/15 16:49
 **/
@Service("ChatMsgService")
public class ChatMsgServiceImpl implements ChatMsgService {

    @Resource
    private ChatMsgMapper chatMsgDao;


    @Async
    @Transactional
    @Override
    public void insertChatMsg(ChatMsgVO chatMsgVO) {
        @NotBlank(message = "发送者不能为空") String fromUser = chatMsgVO.getMFromUser();
        @NotNull(message = "接收者不能为空") String toUser = chatMsgVO.getMToUser();
        Example o = new Example(ChatMsg.class);
        o.createCriteria().andEqualTo("mFromUser", fromUser);
        int i = chatMsgDao.selectCountByExample(o);
        if (i < 0) {
            throw new CustomException("发送者不存在");
        }
        if (StringUtils.isEmpty(toUser)) {
            throw new CustomException("接收者不存在");
        }
        ChatMsg chatMsg = new ChatMsg();
        BeanUtils.copyProperties(chatMsgVO, chatMsg);
        chatMsg.setMFromUser(chatMsg.getMFromUser())
                .setMFromAvatar(chatMsg.getMFromAvatar())
                .setMToUser(chatMsg.getMToUser())
                .setMToAvatar(chatMsg.getMToAvatar())
                .setMMsg(chatMsg.getMMsg())
                .setMCreateTime(new Date())
                .setMType(1);
        chatMsgDao.insert(chatMsg);
    }


    public List<ChatMsg> LookTwoUserMsg(ChatMsg chatMsg) {
        List<ChatMsg> chatMsgVOS = chatMsgDao.select(chatMsg);
        if (!chatMsgVOS.isEmpty()) {
            return chatMsgVOS;
        }
        return null;
    }


    /**
     * @param username
     * @param friend
     * @return
     */
    @Override
    public List<ChatMsg> getFriendMsg(String username, String friend) {
        //查询用户是否存在
        Example o = new Example(ChatMsg.class);
        o.createCriteria().andEqualTo("mToUser", username);
        int i = chatMsgDao.selectCountByExample(o);
        if (i < 0) {
            throw new CustomException("该用户不存在");
        }
        Example e = new Example(ChatMsg.class);
        //查询a->b
        Example.Criteria c = e.createCriteria();
        c.andEqualTo("mToUser", username);
        c.andEqualTo("mFromUser", friend);
        //查询b->a
        Example.Criteria ec = e.createCriteria();
        ec.andEqualTo("mToUser", friend);
        ec.andEqualTo("mFromUser", username);
        e.or(ec);
        List<ChatMsg> resultList = chatMsgDao.selectByExample(e);

        return resultList;
    }

    @Override
    public List<ChatMsg> getAdminMsg(ChatMsgDTO chatMsgDTO) {
        String username = chatMsgDTO.getCurrentAccount();
        String admin = chatMsgDTO.getAdminAccount();
        //查询用户是否存在
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(admin)) {
            throw new CustomException("当前用户或者管理员不存在");
        }
        Example e = new Example(ChatMsg.class);
        //查询a->b
        Example.Criteria c = e.createCriteria();
        c.andEqualTo("mToUser", username);
        c.andEqualTo("mFromUser", admin);
        //查询b->a
        Example.Criteria ec = e.createCriteria();
        ec.andEqualTo("mToUser", admin);
        ec.andEqualTo("mFromUser", username);
        e.or(ec);
        if (StringUtils.isNotNull(chatMsgDTO.getWindow())) {
            e.createCriteria().andEqualTo("mWindow", chatMsgDTO.getWindow());
        }
        List<ChatMsg> resultList = chatMsgDao.selectByExample(e);
        return resultList;
    }

    @Override
    public List<ChatMsg> getAllMsgByAccount(ChatMsgDTO chatMsgDTO) {
        Example e = new Example(ChatMsg.class);
        //查询a->b
        Example.Criteria c = e.createCriteria();
        c.andEqualTo("mToUser", chatMsgDTO.getCurrentAccount());
        //查询b->a
        Example.Criteria ec = e.createCriteria();
        ec.andEqualTo("mFromUser", chatMsgDTO.getCurrentAccount());
        e.or(ec);
        if (StringUtils.isNotNull(chatMsgDTO.getWindow())) {
            e.createCriteria().andEqualTo("mWindow", chatMsgDTO.getWindow());
        }
        List<ChatMsg> resultList = chatMsgDao.selectByExample(e);
        return resultList;
    }

    @Override
    public void insertGroupChatMsg(ChatMsgVO chatMsgVO) {
        @NotBlank(message = "发送者不能为空") String fromUser = chatMsgVO.getMFromUser();
        Example o = new Example(ChatMsg.class);
        o.createCriteria().andEqualTo("mFromUser", fromUser);
        int i = chatMsgDao.selectCountByExample(o);
        if (i < 0) {
            throw new CustomException("发送者不存在");
        }
        ChatMsg chatMsg = new ChatMsg();
        BeanUtils.copyProperties(chatMsgVO, chatMsg);
        chatMsg.setMFromUser(chatMsg.getMFromUser())
                .setMFromAvatar(chatMsg.getMFromAvatar())
                .setMMsg(chatMsg.getMMsg())
                .setMCreateTime(new Date())
                .setMType(1);
        chatMsgDao.insert(chatMsg);
    }

    @Override
    public List<ChatMsg> getAllMsg() {
        List<ChatMsg> resultList = chatMsgDao.selectAll();
        return resultList;
    }
}
