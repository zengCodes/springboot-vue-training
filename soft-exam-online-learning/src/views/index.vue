<template>
  <!--学生考试首页-->
  <div id="home">
    <addMail
      class="myAddDialog"
      ref="myAddDialog"
      v-show="isShowAdd"
      v-divDrag
    ></addMail>
    <!-- 聊天 -->
    <myChat class="mychat" ref="chat" v-show="isShowChat" v-divDrag></myChat>
    <el-container>
      <!-- 头部左导航 -->
      <el-header class="myheader" v-if="isShowHeader">
        <el-menu
          class="el-menu-demo"
          mode="horizontal"
          text-color="#cccccc"
          background-color="#3d444c"
          active-text-color="#ffffff"
          @select="handleSelect"
        >
          <el-tooltip content="首页" placement="bottom">
            <el-menu-item index="index">
              <span class="home-title">软考好好考</span>
            </el-menu-item>
          </el-tooltip>
          <el-menu-item index="exercise">
            <span>题目训练</span>
          </el-menu-item>
          <el-menu-item index="exam">
            <span>考试中心</span>
          </el-menu-item>
          <el-submenu index="no-route-one">
            <template slot="title">学习中心</template>
            <el-menu-item index="community">社区讨论中心</el-menu-item>
            <el-menu-item index="lesson">课程中心</el-menu-item>
            <el-menu-item index="no-route"
              ><a
                class="official-website"
                href="https://bm.ruankao.org.cn/sign/welcome"
                target="_blank"
                >软考官网报名</a
              ></el-menu-item
            >
          </el-submenu>
          <el-menu-item index="record">
            <a href="javascript:;">我的记录</a>
          </el-menu-item>
          <el-submenu index="no-route-two">
            <template slot="title">咨询信息</template>
            <el-menu-item index="no-route" @click="changeShowMail"
              >添加咨询人</el-menu-item
            >
            <el-menu-item index="no-route" @click="changeShow"
              >咨询</el-menu-item
            >
          </el-submenu>
        </el-menu>
        <!-- 引入下划线 -->
        <div class="line"></div>
        <!-- 头部右导航 -->
        <div class="menu-right">
          <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            text-color="#cccccc"
            active-text-color="#ffffff"
            background-color="#3d444c"
            @select="handleSelect"
          >
            <el-submenu index="no-route">
              <template slot="title">其他</template>
              <el-menu-item index="userCenter">用户中心</el-menu-item>
              <el-menu-item index="logout">退出</el-menu-item>
            </el-submenu>
          </el-menu>
        </div>
      </el-header>
      <!-- 主内容区域 -->
      <el-main
        class="main"
        :style="[
          { padding: isShowHeader ? '' : '0px' },
          { height: isShowHeader ? '90vh' : '' },
        ]"
      >
        <!--路由区域-->
        <router-view></router-view>
        <!-- router-view自动跳转到了某页面，原因？ -->
      </el-main>

      <div class="footer" v-if="isShowHeader">
        <my-footer></my-footer>
        <!-- 引入组件，尾部的免责声明部分 -->
      </div>
    </el-container>
  </div>
</template>

<script>
import AddMail from "@/components/chat/addMail";
import MyChat from "@/components/chat/myChat";
import myFooter from "@/components/myFooter";
import { mapState } from "vuex";
import { getUser } from "@/api/system/user";

export default {
  components: {
    myFooter,
    MyChat,
    AddMail,
  },

  data() {
    return {
      activeIndex: "1",
      // 管理员列表数据
      adminList: [],
      activeUser: null,
      isAdmin: false,
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      switch (key) {
        case "no-route-one":
          break;
        case "no-route-two":
          break;
        case "no-route":
          break;
        default:
          this.$router.push({
            path: key,
          });
          break;
      }
    },
    //切换
    changeShow() {
      this.$store.commit("handleShowChat");
    },
    changeShowMail() {
      this.$store.commit("handleAddMail");
    },
    // 获取用户信息
    async getAdminInfo(id) {
      let res = await getUser(id);
      let { code, data, msg } = res;
      if (code === 200) {
        this.activeUser = data;
      } else {
        this.msgInfo("获取好友用户信息失败：" + msg);
      }
    },
  },
  created() {
    this.userInfo.roles.forEach((item) => {
      this.isAdmin = item.match(new RegExp(/admin/)) ? true : false;
    });
  },
  mounted() {
    this.$store.dispatch("getUser", {
      isAdmin: this.isAdmin,
      id: this.userId,
    });
  },
  computed: {
    account() {
      return this.userInfo.user.userName;
    },
    userId() {
      return this.userInfo.user.userId;
    },
    ...mapState({
      isShowAdd: (state) => state.chat.isShowAdd,
      isShowChat: (state) => state.chat.isShowChat,
      userInfo: (state) => state.user,
      isShowHeader: (state) => state.app.isShowHeader,
    }),
  },
  watch: {
    isShowChat: {
      handler(flag) {
        if (flag) {
          //连接WebSocket服务
          this.$store.dispatch("connect");
        } else {
          //断开WebSocket服务
          this.$store.dispatch("disconnect");
        }
      },
    },
  },
};
</script>

<style lang="scss" scoped>
.myAddDialog {
  z-index: 1000;
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, 50%);
  transform: translate(-50%, -50%);
}
// 联系管理员弹出框样式
.mychat {
  z-index: 999;
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, 50%);
  transform: translate(-50%, -50%);
}
// 头部盒子
::v-deep .myheader {
  background-color: #3d444c;
  color: #cccccc;
  height: 60px !important;
  line-height: 60px;
}
::v-deep .el-menu-demo {
  height: 60px;
  // 头部标题
  .home-title {
    color: #ffffff;
    font-weight: bold;
    font-size: 22px;
  }
  .el-menu-item {
    font-size: 16px;
  }
}
::v-deep .el-menu--horizontal {
  border-bottom: 0 !important;
}
// 头部左边导航栏
.menu-right {
  position: absolute;
  right: 25px;
  top: 0px;
}
// 主内容盒子样式
::v-deep .el-main {
  // overflow: hidden !important;
  scroll-behavior: smooth;
}
.main {
  width: 100%;
  background-color: rgb(247, 248, 250);
}
// 底部盒子样式
.footer {
  position: fixed;
  height: 50px;
  bottom: 0px;
  width: 100%;
}
.official-website {
  color: #fff;
  font-size: 16px;
}
</style>
