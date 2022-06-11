<template>
  <div id="welcome">
    <el-breadcrumb
      separator="/"
      style="padding-left: 5px; padding-bottom: 5px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>欢迎</el-breadcrumb-item>
    </el-breadcrumb>
    <el-row :gutter="24" style="margin-top: 10px">
      <!-- 左边部分 -->
      <el-col :span="9">
        <!-- 用户信息表格 -->
        <el-card style="height: 120px; padding: 0; margin: 0">
          <el-tooltip
            class="avatar-item"
            effect="dark"
            content="用户头像"
            placement="top-start"
          >
            <el-avatar
              shape="square"
              :size="66"
              :src="userInfo.avatar"
              style="float: left"
              :key="1"
            ></el-avatar>
          </el-tooltip>
          <div class="right" style="float: right; width: 85%; height: 100px">
            <el-table :data="tableInfo" border size="mini">
              <el-table-column
                prop="username"
                label="用户账号"
              ></el-table-column>
              <el-table-column
                prop="email"
                label="邮箱"
                min-width="100"
              ></el-table-column>
              <el-table-column
                fixed="right"
                prop="roles"
                label="用户角色"
                width="100"
              ></el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="static-card">
          <el-avatar
            class="static-icon"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/288b89194e657603ff40db39e8072640.svg~tplv-49unhts6dw-image.image"
          ></el-avatar>
          <div class="static-item">
            <div class="item-title">学员数量</div>
            <div class="item-content">
              <div class="number">8888 <span>人</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="static-card">
          <el-avatar
            class="static-icon"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/fdc66b07224cdf18843c6076c2587eb5.svg~tplv-49unhts6dw-image.image"
          ></el-avatar>
          <div class="static-item">
            <div class="item-title">课程订单数量</div>
            <div class="item-content">
              <div class="number">8888 <span>单</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="static-card">
          <el-avatar
            class="static-icon"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/77d74c9a245adeae1ec7fb5d4539738d.svg~tplv-49unhts6dw-image.image"
          ></el-avatar>
          <div class="static-item">
            <div class="item-title">考试场次</div>
            <div class="item-content">
              <div class="number">8888 <span>次</span></div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 中间部分 -->
    </el-row>
    <el-row :gutter="24">
      <el-col :span="24">
        <!-- 功能列表 -->
        <el-card class="box-card-two">
          <!-- 用户登入报表 -->
          <div class="title">用户一周登入统计</div>
          <lineChart :lineData="lineData"> </lineChart>
          <!-- <chart ref="chart" :antVData="chartData" class="login-chart"></chart> -->
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="24">
      <el-col :span="24">
        <el-card style="min-height: 86vh; margin-bottom: 5px">
          <node-tree ref="tree"></node-tree>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import Chart from "@/components/antV/Chart";
import lineChart from "@/components/echarts";
import NodeTree from "@/components/antV/NodeTree";
import { mapState } from "vuex";
import { getCategoryTree } from "@/api/business/category";
import { getLoginReport } from "@/api/monitor/loginLog";
import { awaitWrapper } from "@/utils/zeng";
export default {
  components: {
    //引入组件
    Chart,
    NodeTree,
    lineChart,
  },
  name: "Welcome",
  data() {
    return {
      chartData: {},
      lineData: {},
      treeData: [],
      queryMap: { pageNum: 1, pageSize: 5 },
      value: new Date(),
      userInfo: {},
      tableInfo: [],
      // 是否显示公告信息展开收缩
      showDisplay: false,
      // 控制展开与收缩阈值
      changeOpen: false,
    };
  },
  methods: {
    /**
     * 加载登入报表数据
     */
    async loginReport(username) {
      let res = await awaitWrapper(
        getLoginReport({
          userName: username,
        })
      );
      if (res[0]) {
        // error
      } else {
        let { code, msg, data } = res[1];
        if (code === 200) {
          this.chartData = data;
          this.$set(this.lineData, "count", {
            all: this.filterProperty(data.all, "count"),
            me: this.filterProperty(data.me, "count"),
          });
          this.$set(
            this.lineData,
            "days",
            this.filterProperty(data.all, "days")
          );
        } else {
          this.msgInfo("获取登入报表数据失败:" + msg);
        }
      }
    },
    filterProperty(arr, name) {
      return arr.map((item) => (item[name] ? item[name] : undefined));
    },
    /**
     * 获取科目分类树
     */
    async getCategoryList() {
      let res = await getCategoryTree();
      let { code, data, msg } = res;
      if (code === 200) {
        this.$refs.tree.getTreeData(data);
      } else {
        this.msgInfo("获取分类树数据错误：" + msg);
      }
    },
  },
  created() {
    this.userInfo = this.userData.user;
    const roles = this.userInfo.isAdmin ? "超级管理员" : this.userData.roles;
    this.tableInfo.push({
      username: this.userInfo.userName,
      roles: roles,
      email: this.userInfo.email,
      avatar: this.userInfo.avatar,
    });
  },
  mounted() {
    this.loginReport(this.userInfo.userName);
    this.$nextTick(() => {
      this.getCategoryList();
    });
  },
  computed: {
    ...mapState({
      userData: (state) => state.user,
    }),
  },
};
</script>

<style lang="scss" scoped>
/*定义滚动条高宽及背景 高宽分别对应横竖滚动条的尺寸*/
::-webkit-scrollbar {
  width: 3px;
  height: 3px;
  background-color: #f5f5f5;
}

/*定义滑块 内阴影+圆角*/
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #d4d3d3;
}

/**滚动条没有滑块的部分 */
::-webkit-scrollbar-track-piece {
  background-color: white;
}
#welcome {
  overflow: hidden;
  .login-chart {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
  }
  .el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
  }

  // 登入图表
  .box-card-two {
    margin-bottom: 10px;
    position: relative;
    height: 50vh;
    width: 100%;
    padding: 0px;
    margin-top: 12px;
    .el-card__body {
      width: 1200px;
      height: 100%;
    }
    .title {
      padding: 5px;
      font-size: 22px;
      font-weight: bold;
      display: flex;
      justify-content: center;
      text-align: center;
    }
  }
}
::v-deep .el-card__body {
  padding: 15px;
}
::v-deep .static-card {
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  .el-card__body {
    display: flex;
    .static-icon {
      justify-content: flex-start;
      width: 60px;
      height: 60px;
      font-size: 28px;
      background-color: #e1e4f0;
    }
    .static-item {
      justify-content: flex-end;
      margin-left: 20px;
      .item-title {
        margin-bottom: 8px;
        font-size: 14px;
      }
      .item-content {
        font-weight: 500;
        font-size: 26px;
        white-space: nowrap;
        .number {
          span {
            font-weight: 400;
            font-size: 12px;
            white-space: nowrap;
          }
        }
      }
    }
  }
}
</style>
