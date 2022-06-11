<template>
  <div id="card">
    <header></header>
    <footer></footer>
    <el-input
      class="search"
      type="text"
      size="mini"
      v-model="filterKey"
      placeholder="搜索"
      prefix-icon="el-icon-search"
    ></el-input>
    <el-button class="searchBtn" size="mini" @click="addUser"
      ><i class="el-icon-plus" aria-hidden="true"></i
    ></el-button>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'card',
  data() {
    return { filterKey: '' }
  },
  computed: {
    user() {
      return this.userInfo.user
    },
    ...mapState({
      userInfo: state => state.user,
    }),
  },
  methods: {
    addUser() {
      if (this.filterKey == '') {
        this.msgWarning('请输入要添加的好友账号')
      } else {
        let data = null
        let isAdmin = this.$store.state.chat.currentUser.admin
        if (isAdmin) {
          data = {
            friendAccount: this.user.userName,
            userAccount: this.filterKey,
          }
        } else {
          data = {
            friendAccount: this.filterKey,
            userAccount: this.user.userName,
          }
        }
        this.$store.dispatch('addFriend', data)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
#card {
  padding: 12px;
  margin-bottom: 5px;
  .avatar {
    width: 40px;
    height: 40px;
    vertical-align: middle; /*这个是图片和文字居中对齐*/
  }
  .name {
    display: inline-block;
    padding: 10px;
    margin-bottom: 15px;
    font-size: 16px;
  }
  .search {
    background-color: #dbd9d8;
    width: 150px;
    height: 30px;
    line-height: 30px;
    padding: 0 10px;
    border: 0;
    border-radius: 4px;
    outline: none; /*鼠标点击后不会出现蓝色边框*/
    color: #fff;
  }
  .searchBtn {
    background-color: #dbd9d8;
    border: 0;
    margin-left: 3px;
  }
}
</style>
<style>
/*当前组件的el-input样式设置*/
#card .el-input__inner {
  background-color: #dbd9d8;
  outline: none; /*鼠标点击后不会出现蓝色边框*/
}
</style>
