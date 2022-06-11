<template>
  <div id="chat">
    <div class="toolbar">
      <toolbar></toolbar>
    </div>
    <div class="sidebar">
      <card></card>
      <list></list>
    </div>
    <div class="main">
      <chatTitle></chatTitle>
      <message></message>
      <userText></userText>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import card from "@/components/chat/card";
import list from "@/components/chat/list.vue";
import message from "@/components/chat/message.vue";
import userText from "@/components/chat/text.vue";
import toolbar from "@/components/chat/toolbar";
import chatTitle from "@/components/chat/title";

export default {
  name: "ChatRoom",
  props: {
    account: {
      type: String,
    },
    isAdmin: {
      type: Boolean,
      default: false,
    },
    userId: {
      type: Number,
    },
  },
  computed: {
    user() {
      return this.userInfo.user;
    },
    ...mapState({
      userInfo: (state) => state.user,
    }),
  },
  mounted() {
    //初始化数据
    this.$store.dispatch("initData");
    this.$store.dispatch("saveUser", this.user);
    this.$store.dispatch("getUser", {
      isAdmin: this.isAdmin,
      id: this.userId,
    });
    //连接WebSocket服务
    this.$store.dispatch("connect");
  },
  created() {
    //在页面刷新时将vuex里的最新信息保存到sessionStorage里
    window.addEventListener("beforeunload", () => {
      sessionStorage.setItem("state", JSON.stringify(this.$store.state));
    });
  },
  components: {
    toolbar,
    card,
    list,
    message,
    userText,
    chatTitle,
  },
  beforeDestroy() {
    //断开WebSocket服务
    this.$store.dispatch("disconnect");
  },
};
</script>

<style lang="scss" scoped>
#chat {
  width: 100%;
  height: 650px;
  overflow: hidden;
  border-radius: 10px;
  .sidebar,
  .main,
  .toolbar {
    height: 100%;
  }
  .toolbar {
    float: left;
    color: #f4f4f4;
    background-color: #2e3238;
    width: 60px;
  }
  .sidebar {
    float: left;
    color: #000000;
    background-color: #eceae8;
    width: 240px;
  }
  .main {
    position: relative;
    overflow: hidden;
    background-color: #eee;
  }
}
</style>
