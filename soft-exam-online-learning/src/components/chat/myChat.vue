<template>
  <div id="mychat">
    <div class="chat">
      <div class="header">
        <div class="header-move"></div>
        <!-- <img src="@/assets/images/avatar/xue.png" class="avater" /> -->
        <span class="name"
          >咨询人列表
          <i class="iconfont icon-tianjiahaoyou add-mail" @click="addMail"></i>
        </span>
        <span class="close" @click="handleClose">
          <i class="el-icon-circle-close"></i>
        </span>
      </div>
      <!-- 列表 -->
      <el-row :gutter="24">
        <el-col :span="6" class="my-col-one">
          <div class="friend-list">
            <ul>
              <li
                v-for="item in users"
                :key="item.userId"
                @click="changeCurrentSession(item)"
              >
                {{ item.userName }}
              </li>
            </ul>
          </div>
        </el-col>
        <el-col :span="18" class="my-col-two">
          <!-- 显示信息 -->
          <div class="scroller">
            <chat-msg-list></chat-msg-list>
          </div>
        </el-col>
      </el-row>
      <ChatEnterBox class="enter-box"></ChatEnterBox>
    </div>
  </div>
</template>
<script>
import ChatEnterBox from "@/components/chat/enterBox";
import ChatMsgList from "@/components/chat/msgList";
import { mapState } from "vuex";

export default {
  data() {
    return {};
  },
  components: {
    ChatEnterBox,
    ChatMsgList,
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.user,
      users: (state) => state.chat.chatUsers,
      currentSession: (state) => state.chat.currentSession,
    }),
    user() {
      return this.userInfo.user;
    },
  },
  methods: {
    // 建立与管理员连接
    changeCurrentSession(currentSession) {
      this.$store.dispatch("saveUser", this.user);
      this.$store.commit("changeCurrentSession", currentSession);
      //初始化数据
      this.$store.dispatch("initData", {
        currentAccount: this.user.userName,
        adminAccount: currentSession.userName,
      });
      setTimeout(() => {
        this.$store.commit("scrollBottom", true);
      }, 100);
    },
    handleClose() {
      this.$store.commit("handleShowChat");
    },
    addMail() {
      this.$store.commit("handleAddMail");
    },
  },
};
</script>
<style scoped lang="scss">
//聊天
.chat {
  transition: all 0.2s ease;
  width: 600px;
  box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.3);
  .header {
    width: 100%;
    // border: 1px solid black;
    background-color: #409eff;
    height: 60px;
    position: relative;
    .header-move {
      position: absolute;
      top: 0px;
      left: 50%;
      transform: translateX(-50%);
      width: 100px;
      height: 20px;
      // border: 1px solid black;
      cursor: move;
    }
    .avater {
      position: absolute;
      top: 50%;
      left: 30px;
      transform: translateY(-50%);
      width: 50px;
      height: 50px;
      // border: 1px solid black;
      border-radius: 50%;
    }
    .name {
      position: absolute;
      left: 30px;
      top: 50%;
      color: #fff;
      transform: translateY(-50%);
      .add-mail {
        cursor: pointer;
      }
    }
    .close {
      position: absolute;
      right: 12px;
      top: 10px;
      font-size: 20px;
      color: #fff;
      cursor: pointer;
    }
  }
  // 好友列表
  .friend-list {
    float: left;
    width: 100%;
    height: 250px;
    background-color: #409eff;
    overflow-y: scroll;
    overflow-x: hidden;
    ul {
      li {
        cursor: pointer;
        border: 1px solid #fff;
        width: 100%;
        display: inline-block;
        height: 40px;
        line-height: 40px;
        text-align: center;
        color: #fff;
        font-size: 14px;
      }
    }
  }
  // 信息
  .msg-list {
    float: right;
    width: 100%;
    height: 250px;
  }
}
.input-dialog {
  transition: all 0.2s ease;
  width: 200px;
  box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.3);
  .add-input {
    position: absolute;
    left: 180px;
    top: 50%;
    color: #fff;
    transform: translateY(-50%);
    .hp-row {
      position: relative;
    }
    .hp-row input {
      border: none;
      border-bottom: 1px solid #c6c6c6;
      background: none;
      outline: none;
      padding: 10px 10px 10px 5px;
      width: 100px;
      display: block;
      box-sizing: border-box;
      font-size: 16px;
      color: #fff;
    }

    .hp-row label {
      position: absolute;
      top: 10px;
      left: -2px;
      font-size: 12px;
      transition: all 0.3s ease;
      -webkit-transition: 0.3s ease all;
      color: #c6c6c6;
      pointer-events: none;
    }
    .hp-row input:focus ~ label,
    .hp-row input:valid ~ label {
      top: -5px;
      font-size: 12px;
      color: #fff;
    }
    .hp-row .bar {
      display: block;
      position: relative;
    }
    .hp-row .bar:before {
      position: absolute;
      bottom: 0px;
      left: 0px;
      content: "";
      width: 0px;
      height: 2px;
      transition: all 0.3s ease;
      -webkit-transition: 0.3s ease all;
    }

    .hp-row input:focus ~ .bar:before,
    .hp-row input:valid ~ .bar:before {
      width: 100px;
      background: #fff;
      color: #fff;
    }
  }
}
::v-deep .my-col-two {
  padding: 0 !important;
}
::v-deep .my-col-one {
  padding-right: 0 !important;
}
::v-deep .el-col-18 {
  width: 73% !important;
}
</style>
