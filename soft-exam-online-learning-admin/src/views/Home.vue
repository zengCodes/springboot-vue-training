<template>
  <el-container class="home-container">
    <!--    导航-->
    <el-header>
      <div>
        <span style="margin-left: 20px">软考在线培训管理系统</span>
      </div>
      <el-dropdown>
        <div class="block">
          <el-avatar
            :size="50"
            :src="userInfo.avatar"
            style="cursor: pointer"
          ></el-avatar>
        </div>
        <el-dropdown-menu slot="dropdown" trigger="click">
          <el-dropdown-item>
            <span type="danger" @click="toProfie">
              <span class="el-icon-house"></span> &nbsp;个人中心
            </span>
          </el-dropdown-item>
          <el-dropdown-item>
            <span type="danger" @click="logout">
              <span class="el-icon-switch-button"></span> &nbsp;退出登入
            </span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-header>
    <!--主体-->
    <el-container style="height: 500px">
      <!--菜单-->
      <el-aside :width="isOpen === true ? '64px' : '200px'" class="my-aside">
        <div class="toggle-btn" @click="toggleMenu">|||</div>
        <el-menu
          class="el-menu-vertical-demo"
          :collapse="isOpen"
          :router="true"
          :default-active="activePath"
          background-color="#001529"
          :collapse-transition="false"
          text-color="rgba(255,255,255,0.7)"
          unique-opened
        >
          <MenuTree
            :menuList="menuList"
            @currentActivePath="currentActivePath"
          ></MenuTree>
        </el-menu>
      </el-aside>

      <!--右边主体-->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import MenuTree from "@/components/MenuTree"; //引进菜单模板
import { getRouters } from "@/api/menu";
import { mapState } from "vuex";

export default {
  name: "home",
  data() {
    return {
      loading: true,
      activePath: "", //激活的路径
      isOpen: false,
      menuList: {},
    };
  },
  components: {
    MenuTree,
  },
  methods: {
    /**
     *
     * 退出登入
     */
    async logout() {
      const res = await this.$confirm("此操作将退出系统, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).catch(() => {
        this.$message({
          type: "info",
          message: "已取消退出登入",
        });
      });
      if (res === "confirm") {
        this.$store.dispatch("LogOut").then(() => {
          this.$router.replace("/home");
        });
      }
    },
    /**
     * 去用户个人中心
     */
    toProfie() {
      this.$router.push("/profile");
    },
    /**
     *加载菜单数据
     */
    async getMenuList() {
      let res = await getRouters();
      let { data, code } = res;
      if (code == 200) {
        data.map((item) => {
          item.visible = item.visible === "0";
        });
        this.menuList = data;
      }
    },
    currentActivePath(path) {
      this.activePath = path;
    },
    /**
     * 菜单伸缩
     */
    toggleMenu() {
      this.isOpen = !this.isOpen;
    },
    /**
     * 点击交流
     */
    getContact() {
      const w = window.open("about:blank");
      w.location.href = "https://www.zykcoderman.xyz/";
    },
  },
  created() {
    this.getMenuList();
    // this.activePath = window.sessionStorage.getItem('activePath')
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.user,
    }),
  },
};
</script>

<style lang="scss" scoped>
/* 为对应的路由跳转时设置动画效果 */

.el-header {
  background-color: #001529;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  font-size: 19px;

  padding-left: 0px;
}
::v-deep .el-aside {
  background-color: #001529;
  overflow-x: hidden !important;
  overflow-y: scroll;
}
/* 滚动槽 */
::-webkit-scrollbar {
  width: 3px;
  height: 3px;
}
::-webkit-scrollbar-track {
  border-radius: 3px;
  background: rgba(0, 0, 0, 0.06);
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.08);
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: rgba(0, 0, 0, 0.12);
  -webkit-box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
}
.el-main {
  background-color: #eaedf1;
  // overflow: hidden !important;
  // 平滑滚动
  scroll-behavior: smooth;
}
.home-container {
  width: 100%;
  height: 100% !important;
}
.toggle-btn {
  background-color: #2d8cf0 !important;
  font-size: 10px;
  line-height: 24px;
  color: #fff;
  text-align: center;
  letter-spacing: 0.2em;
  cursor: pointer;
}
</style>
