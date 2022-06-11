<template>
  <div class="chatTitle">
    <span class="titleName">{{
      isAdmin
        ? currentSession.userAccount
          ? currentSession.userAccount
          : ''
        : currentSession.friendAccount
    }}</span>
    <el-button class="moreBtn" size="small" icon="el-icon-more"></el-button>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'chatTitle',
  computed: {
    ...mapState({
      userInfo: state => state.user,
      currentSession: state => state.chat.currentSession,
    }),
    isAdmin() {
      return this.userInfo.user.admin
    },
  },
}
</script>

<style lang="scss" scoped>
.chatTitle {
  height: 50px;
  width: 100%;
  font-size: 20px;
  display: flex;
  justify-content: space-between;
  border-bottom: 0.5px solid #c7d2db;
}
::v-deep .moreBtn {
  background-color: #eee;
  border: 0;
  height: 40px;
}
.titleName {
  margin: 10px 20px;
}
</style>
