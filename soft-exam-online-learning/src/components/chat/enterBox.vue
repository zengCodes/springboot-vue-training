<template>
  <div class="web__msg" @keyup.enter="addMessage">
    <textarea
      v-model="currentMsg"
      rows="3"
      :placeholder="placeholder"
      class="web__msg-input"
      ref="msgBox"
    />
    <div class="web__msg-menu">
      <el-button
        class="web__msg-submit"
        type="primary"
        size="mini"
        @click="handleSend"
        >发送</el-button
      >
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'enterText',
  props: {
    placeholder: {
      type: String,
      default: '请输入内容...',
    },
  },
  data() {
    return {
      currentMsg: '',
    }
  },
  computed: {
    ...mapState({
      userInfo: state => state.user,
      currentSession: state => state.chat.currentSession,
    }),
  },
  methods: {
    handleSend() {
      if (!this.currentMsg || this.currentMsg.match(/^[ ]*$/)) {
        this.$message({
          showClose: true,
          message: '不能发送空白信息',
        })
        return
      }
      let msgObj = new Object()
      msgObj.mmsg = this.currentMsg
      msgObj.mtype = 1
      msgObj.mwindow = 0
      msgObj.mfromUser = this.userInfo.user.userName
      msgObj.mtoUser = this.currentSession.userName
      msgObj.mtoAvatar = this.currentSession.avatar
      console.log(msgObj)
      this.$store.state.chat.stomp.send('/ws/chat', {}, JSON.stringify(msgObj))
      // 提交私聊消息记录
      this.$store.commit('addMessage', msgObj)
      // 滚动到底部
      this.$store.commit('scrollBottom', true)
      //清空输入框
      this.currentMsg = ''
    },
    addMessage(e) {
      if (e.ctrlKey && e.keyCode === 13 && this.currentMsg.length) {
        this.addMessageByClick()
      }
    },
  },
}
</script>
<style scoped lang="scss">
.web__msg {
  background-color: #fff;
  padding: 6px 10px;
  height: auto;
  overflow: hidden;
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
</style>
