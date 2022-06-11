<template>
  <div id="mychat">
    <div class="chat">
      <div class="header">
        <div class="header-move"></div>
        <span class="name">添加咨询人 </span>
        <span class="close" @click="handleClose">
          <i class="el-icon-circle-close"></i>
        </span>
      </div>
      <div class="main">
        <div class="web__msg">
          <textarea
            v-model="addMsg"
            rows="3"
            class="web__msg-input"
            ref="msgBox"
            placeholder="请输入添加咨询信息"
          />
        </div>
        <div class="web__msg-menu">
          <el-button
            class="web__msg-submit"
            type="primary"
            size="mini"
            @click="handleSubmit"
            >发送邮箱</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";

export default {
  data() {
    return {
      addMsg: "",
    };
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
    handleClose() {
      this.$store.commit("handleAddMail");
    },
    handleSubmit() {
      if (this.user.email.toString() == "") {
        this.msgInfo("用户邮箱不能为空！");
        return;
      }
      let msgObj = {};
      msgObj.userId = this.user.userId;
      msgObj.nickname = this.user.nickName;
      msgObj.username = this.user.userName;
      msgObj.email = this.user.email;
      msgObj.content = this.addMsg;
      //   console.log(msgObj);
      this.$store.dispatch("handleAddSubmit", msgObj);
    },
  },
};
</script>
<style scoped lang="scss">
//聊天
.chat {
  transition: all 0.2s ease;
  width: 300px;
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
      cursor: move;
    }
    .avater {
      position: absolute;
      top: 50%;
      left: 30px;
      transform: translateY(-50%);
      width: 50px;
      height: 50px;
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
  .main {
    background-color: #fff;
    height: 100px;
    width: 97%;
    padding: 5px;
    .web__msg {
      background-color: #fff;
      padding: 6px 10px;
      height: auto;
      overflow: hidden;
      border: 1px solid #ccc;
      &-input {
        display: block;
        width: 100%;
        height: 60px;
        overflow-x: hidden;
        overflow-y: auto;
        box-sizing: border-box;
        resize: none;
        outline: 0;
        background-color: #fff;
        border: 0;
        word-break: break-all;
        font-size: 13px;
        line-height: 17px;
        -webkit-appearance: none;
      }
      &-menu {
        text-align: right;
      }
      &-submit {
        display: inline-block;
        outline: none;
        cursor: pointer;
        text-align: center;
      }
      .web__msg-menu {
        margin: 0px 0px;
      }
    }
  }
}
</style>
