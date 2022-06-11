import { getToken } from '@/utils/auth'
import { selectAllAdminFriends } from '@/api/business/chat'

import { getChatMsg, addMailMsg } from '@/api/business/chat'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import Vue from 'vue'
import { Notification } from 'element-ui'

const chat = {
  state: {
    isShowChat: false,
    isShowAdd:false,
    isToBottom: false,
    chatMsgs: {},
    chatUsers: [],
    currentUser: null,
    currentSession: { userName: undefined, nickname: undefined }, //当前选中的用户
    stomp: null,
  },
  mutations: {
    // 滚动的提示阈值
    scrollBottom(state, data) {
      state.isToBottom = data
    },
    handleShowChat(state) {
      state.isShowChat = !state.isShowChat
    },
    handleAddMail(state) {
      state.isShowAdd = !state.isShowAdd
    },
    changeCurrentSession(state, currentSession) {
      //更新当前选中的用户
      state.currentSession = currentSession
    },
    //保存单聊数据
    addMessage(state, msg) {
      // console.log(state, msg)
      let message =
        state.chatMsgs[state.currentUser.userName + '#' + msg.mtoUser]
      if (!message) {
        //创建保存消息记录的数组
        Vue.set(
          state.chatMsgs,
          state.currentUser.userName + '#' + msg.mtoUser,
          []
        )
      }
      state.chatMsgs[state.currentUser.userName + '#' + msg.mtoUser].push({
        mmsg: msg.mmsg,
        mcreateTime: new Date(),
        mfromUser: msg.mfromUser,
        mtype: msg.mtype,
        mtoUser: msg.mtoUser,
        mtoAvatar: msg.mtoAvatar,
      })
    },
    /**
     *  获取本地聊天记录，同步数据库的记录保存到localStorage中。
     *  不刷新情况下都是读取保存再localStorage中的记录
     * @param state
     * @constructor
     */
    INIT_DATA: async (state, account) => {
      //同步数据库中的数据
      let resp = await getChatMsg({
        currentAccount: account.currentAccount,
        adminAccount: account.adminAccount,
        window: 0,
      })
      if (resp) {
        let msgList = resp.data
        if (state?.currentUser?.userName) {
          Vue.set(
            state.chatMsgs,
            state.currentUser.userName + '#' + state.currentSession.userName,
            msgList
          )
        }
      }
    },
    //保存系统所有用户
    INIT_USER(state, data) {
      state.chatUsers = data
    },
    GET_USERS: async (state, user) => {
      //获取好友列表
      let res = await selectAllAdminFriends()
      let { code, data } = res
      if (code === 200) {
        state.chatUsers = data
      }
    },
    // 保存当前登录用户
    SAVE_USER(state, data) {
      state.currentUser = data
    },

    ADD_FRIEND: async (state, data) => {
      let res = await addMailMsg(data)
      let { code,msg } = res
      if (code === 200) {
        Notification.success({
          title: '系统消息',
          message: msg,
          position: 'top-right',
        })
      } else {
        Notification.info({
          title: '系统消息',
          message: msg,
          position: 'top-right',
        })
      }
    },
  },
  actions: {
    /**
     * 作用：初始化数据
     * action函数接受一个与store实例具有相同方法和属性的context对象
     * @param context
     */
    initData(context, account) {
      //初始化聊天记录
      context.commit('INIT_DATA', account)
    },
    getUser(context, data) {
      //获取用户列表
      context.commit('GET_USERS', data)
    },
    saveUser(context, data) {
      context.commit('SAVE_USER', data)
    },
    handleAddSubmit(context, data) {
      context.commit("ADD_FRIEND",data)
    },
    /**
     * 实现连接服务端连接与消息订阅
     * @param context 与store实例具有相同方法和属性的context对象
     */
    connect(context) {
      //连接Stomp站点
      context.state.stomp = Stomp.over(new SockJS('/ws/chat'))
      context.state.stomp.connect(
        { Authorization: getToken() }, //传递token
        success => {
          /**
           * 订阅系统广播通知消息
           */
          context.state.stomp.subscribe('/topic/notification', msg => {
            //判断是否是系统广播通知
            Notification.info({
              title: '系统消息',
              message: msg.body.substr(5),
              position: 'top-right',
            })
            //更新用户列表（的登录状态）
            context.commit('GET_USERS')
          })

          /**
           * 订阅私人消息
           */
          context.state.stomp.subscribe('/queue/chat', msg => {
            //接收到的消息数据
            let receiveMsg = JSON.parse(msg.body)
            //没有选中用户或选中用户不是发来消息的那一方
            if (
              !context.state.currentSession ||
              receiveMsg.mtoUser != context.state.currentSession.userName
            ) {
              Notification.info({
                title: '【' + receiveMsg.mfromUser + '】发来一条消息',
                message:
                  receiveMsg.mmsg.length < 8
                    ? receiveMsg.mmsg
                    : receiveMsg.mmsg.substring(0, 8) + '...',
                position: 'bottom-right',
              })
            }
            //获取发送方
            receiveMsg.mtoUser = receiveMsg.mfromUser
            //提交消息记录
            context.commit('addMessage', receiveMsg)
          })
        },
        error => {
          Notification.error({
            title: '系统消息',
            message: '无法与服务端建立连接，请尝试重新登陆系统~',
            position: 'top-right',
          })
        }
      )
    },
    //与Websocket服务端断开连接
    disconnect(context) {
      if (context.state.stomp != null) {
        context.state.stomp.disconnect()
        console.log('关闭连接~')
      }
    },
  },
}

export default chat
