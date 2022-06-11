import { getToken } from "@/utils/auth";
import { getFriends } from "@/api/business/chat";
import { getAllMsg, appendFriend } from "@/api/business/chat";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import Vue from "vue";
import { Notification } from "element-ui";
const chat = {
  state: {
    currentMsgList: [],
    chatMsgs: {},
    chatUsers: [],
    currentUser: null,
    currentSession: { friendAccount: "群聊", nickname: "群聊" }, //当前选中的用户，默认为群聊
    currentList: "群聊", //当前聊天窗口列表
    filterKey: "",
    stomp: null,
    isDot: {}, //两用户之间是否有未读信息
  },
  mutations: {
    CHANGE_SESSION(state, currentSession) {
      console.log(state, currentSession);
      let account = state.currentUser.admin
        ? currentSession.userAccount
        : currentSession.friendAccount;
      //切换到当前用户就标识消息已读
      Vue.set(state.isDot, state.currentUser.userName + "#" + account, false);
      //更新当前选中的用户
      state.currentSession = currentSession;
    },
    //修改当前聊天窗口列表
    changeCurrentList(state, currentList) {
      state.currentList = currentList;
    },
    //保存群聊消息记录
    addGroupMessage(state, msg) {
      let message = state.chatMsgs["群聊"];
      if (!message) {
        Vue.set(state.chatMsgs, "群聊", []);
      }
      state.chatMsgs["群聊"].push({
        mmsg: msg.mmsg,
        mcreateTime: new Date(),
        mfromUser: msg.mfromUser,
        mtype: msg.mtype,
        mfromAvatar: msg.mfromAvatar,
      });
    },
    //保存单聊数据
    addMessage(state, msg) {
      // console.log(state, msg);
      let message =
        state.chatMsgs[state.currentUser.userName + "#" + msg.mtoUser];
      if (!message) {
        //创建保存消息记录的数组
        Vue.set(
          state.chatMsgs,
          state.currentUser.userName + "#" + msg.mtoUser,
          []
        );
      }
      state.chatMsgs[state.currentUser.userName + "#" + msg.mtoUser].push({
        mmsg: msg.mmsg,
        mcreateTime: new Date(),
        mfromUser: msg.mfromUser,
        mfromAvatar: msg.mfromAvatar,
        mtype: msg.mtype,
        mtoUser: msg.mtoUser,
        mtoAvatar: msg.mtoAvatar,
      });
    },
    /**
     *  获取本地聊天记录，同步数据库的记录保存到localStorage中。
     *  不刷新情况下都是读取保存再localStorage中的记录
     */
    INIT_DATA: async (state) => {
      //同步数据库中的群聊数据
      let resp = await getAllMsg({});
      if (resp) {
        let msgList = resp.data;
        state.currentMsgList = resp.data;
        let msg_one = msgList.filter((item) => {
          return item.mwindow === 0;
        });
        let msg_two = msgList.filter((item) => {
          return item.mwindow === 1;
        });
        if (state?.currentUser?.userName) {
          Vue.set(
            state.chatMsgs,
            state.currentUser.userName +
              "#" +
              state.currentSession.friendAccount,
            msg_one
          );
        }
        Vue.set(state.chatMsgs, "群聊", msg_two);
      }
    },
    //
    FILTER_MSG_BY_ACCOUNT: async (state) => {
      let account = state.currentUser.admin
        ? state.currentSession.userAccount
        : state.currentSession.friendAccount;
      let currentAccount = state.currentUser.userName;
      let friendAccount = account;

      let msgList = state.currentMsgList.filter((item) => {
        return (
          (item.mfromUser == currentAccount &&
            item.mtoUser == friendAccount &&
            item.mwindow == 0) ||
          (item.mtoUser == currentAccount &&
            item.mfromUser == friendAccount &&
            item.mwindow == 0)
        );
      });
      Vue.set(state.chatMsgs, currentAccount + "#" + friendAccount, msgList);
    },
    //保存系统所有用户
    INIT_USER(state, data) {
      state.chatUsers = data;
    },
    GET_USERS: async (state, user) => {
      //获取好友列表
      let res = await getFriends({
        isAdmin: user.isAdmin,
        id: user.id,
      });
      let { code, msg, data } = res;
      if (code === 200) {
        state.chatUsers = data;
      } else {
        this.msgInfo("获取好友列表失败:" + msg);
      }
    },
    // 保存当前登录用户
    SAVE_USER(state, data) {
      state.currentUser = data;
    },

    ADD_FRIEND: async (state, data) => {
      let res = await appendFriend({
        friendAccount: data.friendAccount,
        userAccount: data.userAccount,
      });
      let { code } = res;
      if (code === 200) {
        Notification.success({
          title: "系统消息",
          message: "添加成功",
          position: "top-right",
        });
      }
    },
  },
  actions: {
    /**
     * 作用：初始化数据
     * action函数接受一个与store实例具有相同方法和属性的context对象
     * @param context
     */
    initData(context) {
      //初始化聊天记录
      context.commit("INIT_DATA");
    },
    changeCurrentSession(context, data) {
      context.commit("CHANGE_SESSION", data);
    },
    filterMsgByAccount(context) {
      context.commit("FILTER_MSG_BY_ACCOUNT");
    },
    getUser(context, data) {
      //获取用户列表
      context.commit("GET_USERS", data);
    },
    saveUser(context, data) {
      context.commit("SAVE_USER", data);
    },
    addFriend(context, data) {
      context.commit("ADD_FRIEND", data);
    },
    /**
     * 实现连接服务端连接与消息订阅
     * @param context 与store实例具有相同方法和属性的context对象
     */
    connect(context) {
      //连接Stomp站点
      context.state.stomp = Stomp.over(new SockJS("/ws/chat"));
      context.state.stomp.connect(
        { Authorization: getToken() }, //传递token
        (success) => {
          /**
           * 订阅系统广播通知消息
           */
          context.state.stomp.subscribe("/topic/notification", (msg) => {
            //判断是否是系统广播通知
            Notification.info({
              title: "系统消息",
              message: msg.body.substr(5),
              position: "top-right",
            });
            //更新用户列表（的登录状态）
            context.commit("GET_USERS");
          });
          /**
           * 订阅群聊消息
           */
          context.state.stomp.subscribe("/topic/greetings", (msg) => {
            //接收到的消息数据
            let receiveMsg = JSON.parse(msg.body);
            // console.log("收到消息" + receiveMsg);
            //当前点击的聊天界面不是群聊,默认为消息未读
            if (context.state.currentSession.friendAccount != "群聊") {
              Vue.set(
                context.state.isDot,
                context.state.currentUser.userName + "#群聊",
                true
              );
            }
            //提交消息记录
            context.commit("addGroupMessage", receiveMsg);
          });

          /**
           * 订阅私人消息
           */
          context.state.stomp.subscribe("/queue/chat", (msg) => {
            //接收到的消息数据
            let receiveMsg = JSON.parse(msg.body);
            //没有选中用户或选中用户不是发来消息的那一方
            if (
              !context.state.currentSession ||
              receiveMsg.mfromUser != context.state.currentSession.friendAccount
            ) {
              Notification.info({
                title: "【" + receiveMsg.mtoUser + "】发来一条消息",
                message:
                  receiveMsg.mmsg.length < 8
                    ? receiveMsg.mmsg
                    : receiveMsg.mmsg.substring(0, 8) + "...",
                position: "bottom-right",
              });
              //默认为消息未读
              Vue.set(
                context.state.isDot,
                context.state.currentUser.userName + "#" + receiveMsg.mfromUser,
                true
              );
            }
            //标识这个消息不是自己发的
            receiveMsg.notSelf = true;
            //获取发送方
            receiveMsg.mtoUser = receiveMsg.mfromUser;
            //提交消息记录
            context.commit("addMessage", receiveMsg);
          });
        },
        (error) => {
          Notification.error({
            title: "系统消息",
            message: "无法与服务端建立连接，请尝试重新登陆系统~",
            position: "top-right",
          });
        }
      );
    },
    //与Websocket服务端断开连接
    disconnect(context) {
      if (context.state.stomp != null) {
        context.state.stomp.disconnect();
        console.log("关闭连接~");
      }
    },
  },
};

export default chat;
