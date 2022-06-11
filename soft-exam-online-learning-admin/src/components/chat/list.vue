<template>
  <div id="list">
    <ul v-if="currentList == '群聊'">
      <!--群聊列表-->
      <p style="padding: 2px 4px; height: 20px">群聊列表</p>
      <li
        :class="{
          active: currentSession.friendAccount
            ? '群聊' == currentSession.friendAccount
            : false,
        }"
        @click="changeCurrentSession(chatObj)"
      >
        <el-image
          fit="cover"
          class="avatar"
          src="https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1268761962,3976237305&fm=26&gp=0.jpg"
        />
        <el-badge :is-dot="isDot[user.username + '#群聊']"
          ><p class="name">群聊</p></el-badge
        >
      </li>
    </ul>
    <!--用户列表-->
    <el-scrollbar
      wrap-class="userList"
      wrap-style="height:600px;"
      view-style="height:100%;"
      :native="false"
    >
      <ul v-if="currentList == '私聊'">
        <p style="padding: 2px 4px; height: 20px">用户列表</p>
        <li
          v-for="item in users"
          :class="{
            active: currentSession
              ? (isAdmin ? item.userAccount : item.friendAccount) ===
                (isAdmin
                  ? currentSession.userAccount
                  : currentSession.friendAccount)
              : false,
          }"
          @click="changeCurrentSession(item)"
        >
          <div style="display: flex; justify-content: space-between">
            <div>
              <el-badge
                :is-dot="isDot[user.userName + '#' + item.userAccount]"
                style=""
              >
                <el-image
                  class="avatar"
                  :src="isAdmin ? item.userAvatar : item.friendAvatar"
                  :alt="isAdmin ? item.userAccount : item.friendAccount"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
              </el-badge>
              <p class="name">
                {{ isAdmin ? item.userAccount : item.friendAccount }}
              </p>
            </div>
            <div>
              <el-badge
                :value="item.fstatus == 1 ? '在线' : '离线'"
                :type="item.fstatus == 1 ? 'danger' : 'info'"
              ></el-badge>
            </div>
          </div>
        </li>
      </ul>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "list",
  data() {
    return {
      chatObj: { friendAccount: "群聊", nickname: "群聊" }, //群聊实体对象（为方法复用而构造，对于User对象）
    };
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.user,
      users: (state) => state.chat.chatUsers,
      currentSession: (state) => state.chat.currentSession,
      currentList: (state) => state.chat.currentList,
      isDot: (state) => state.chat.isDot,
    }),
    user() {
      return this.userInfo.user;
    },
    isAdmin() {
      return this.userInfo.user.admin;
    },
  },
  methods: {
    changeCurrentSession(currentSession) {
      this.$store.dispatch("saveUser", this.user);
      this.$store.dispatch("initData");
      this.$store.dispatch("changeCurrentSession", currentSession);
      setTimeout(() => {
        this.$store.dispatch("filterMsgByAccount");
      }, 500);
    },
  },
};
</script>

<style lang="scss" scoped>
#list {
  ul {
    margin-left: 0px;
    padding-left: 0px;
    margin-left: 2px;
  }
  li {
    padding-top: 14px;
    padding-bottom: 14px;
    //padding-right: 40px;
    //border-bottom: 1px solid #292C33;
    list-style: none;
    cursor: pointer;
    &:hover {
      background-color: #d8d6d6;
    }
  }
  li.active {
    /*注意这个是.不是冒号:*/
    background-color: #c8c6c6;
  }
  .avatar {
    border-radius: 2px;
    width: 30px;
    height: 30px;
    vertical-align: middle;
  }
  .name {
    display: inline-block;
    margin-left: 15px;
    margin-top: 0px;
    margin-bottom: 0px;
  }
  .stateItem {
    //在线状态的样式
    /*position: absolute;*/
    /*left: 160px;*/
    //margin-left: 100px;
    //margin-right: 10px;
  }
  .userList {
    max-height: 600px;
  }
  .el-scrollbar__wrap.default-scrollbar__wrap {
    overflow-x: auto;
  }
}
</style>
