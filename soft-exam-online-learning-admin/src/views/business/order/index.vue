<template>
  <div class="order">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>课程管理</el-breadcrumb-item>
      <el-breadcrumb-item>{{
        isDetail ? "订单详情" : "订单列表"
      }}</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="discuss-card" v-if="!isDetail">
      <el-row :gutter="22">
        <el-col :span="12">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            @click="searchOrder"
            >搜索</el-button
          >
          <el-button
            size="small"
            type="primary"
            icon="el-icon-refresh-right"
            @click="getOrderListData"
            >刷新</el-button
          >
        </el-col>
      </el-row>
      <el-table
        size="mini"
        :data="orderData"
        v-loading="loading"
        style="width: 100%; margin-top: 20px"
        height="500"
        border
        stripe
      >
        <el-table-column
          prop="orderNo"
          label="订单号"
          min-width="150"
          align="center"
        />
        <el-table-column
          prop="account"
          align="center"
          label="用户账号"
          min-width="100"
        />
        <el-table-column
          prop="courseCover"
          label="课程信息"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <div class="table-info-item">
              <img
                style="width: 30px; height: 30px"
                :src="scope.row.courseCover"
              />
              <span>
                {{ scope.row.courseTitle }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalFee" label="实际支付(￥)" align="center" />
        <el-table-column prop="discussNum" label="订单状态" align="center">
          <!-- status -->
          <template slot-scope="scope">
            <div class="table-info-item">
              <span
                class="z-circle-icon"
                :class="getStatusClass(scope.row.status)"
              ></span>
              <span class="z-item-txt">{{
                getStatusText(scope.row.status)
              }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          align="center"
          min-width="150"
        ></el-table-column>

        <el-table-column label="操作" align="center" min-width="150">
          <!-- 操作 -->
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-info"
              :loading="btnLoading"
              :disabled="btnDisabled"
              @click="handleDetail(scope.row.id)"
              >详情</el-button
            >
            <el-button
              v-if="scope.row.parentId != 0"
              type="danger"
              size="mini"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.id)"
              v-hasPermission="['system:discuss:del']"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card class="info-card" v-else>
      <div slot="header">
        <div class="header-status">
          <span class="status"
            ><i class="el-icon-warning"></i>当前订单状态：{{
              getStatusText(orderInfo.order.status)
            }}</span
          >
          <span class="btn-back" @click="isDetail = false">返回列表</span>
          <span class="btn-note">备注订单</span>
        </div>
      </div>

      <div class="info-item">
        <div class="title">订单信息</div>
        <div class="table-layout">
          <el-row :gutter="24" class="row-title">
            <el-col :span="6">订单编号</el-col>
            <el-col :span="6">用户账号</el-col>
            <el-col :span="6">支付方式</el-col>
            <el-col :span="6">支付时间</el-col>
          </el-row>
          <el-row :gutter="24" class="row-txt">
            <el-col :span="6">{{ orderInfo.order.orderNo }}</el-col>
            <el-col :span="6">{{ orderInfo.order.account }}</el-col>
            <el-col :span="6">{{ getOrderType(orderInfo.order) }}</el-col>
            <el-col :span="6">{{ getOrderTime(orderInfo.order) }}</el-col>
          </el-row>
        </div>
      </div>

      <div class="info-item">
        <div class="title">用户信息</div>
        <div class="table-layout">
          <el-row :gutter="24" class="row-title">
            <el-col :span="6">用户账号</el-col>
            <el-col :span="6">用户姓名</el-col>
            <el-col :span="6">用户性别</el-col>
            <el-col :span="6">用户电话</el-col>
          </el-row>
          <el-row :gutter="24" class="row-txt">
            <el-col :span="6">{{ orderInfo.user.userName }}</el-col>
            <el-col :span="6">{{ orderInfo.user.nickName }}</el-col>
            <el-col :span="6">{{
              orderInfo.user.sex == 0 ? "男" : "女"
            }}</el-col>
            <el-col :span="6">{{ orderInfo.user.phone }}</el-col>
          </el-row>
        </div>
      </div>

      <div class="info-item">
        <div class="title">课程信息</div>
        <div class="table-layout">
          <el-row :gutter="24" class="row-title">
            <el-col :span="6">课程封面</el-col>
            <el-col :span="6">讲师</el-col>
            <el-col :span="6">课程名称</el-col>
            <el-col :span="6">课程类型</el-col>
          </el-row>
          <el-row :gutter="24" class="row-txt row-info">
            <el-col :span="6"
              ><img
                :src="orderInfo.lesson.logo"
                style="width: 50px; height: 50px"
            /></el-col>
            <el-col :span="6">{{ orderInfo.lesson.nickName }}</el-col>
            <el-col :span="6">{{ orderInfo.lesson.name }}</el-col>
            <el-col :span="6">{{ orderInfo.lesson.type }}</el-col>
          </el-row>
        </div>
      </div>

      <div class="info-item">
        <div class="title">费用信息</div>
        <div class="table-layout">
          <el-row :gutter="24" class="row-title">
            <el-col :span="6">课程名称</el-col>
            <el-col :span="6">课程原价</el-col>
            <el-col :span="6">课程优惠</el-col>
            <el-col :span="6">应付金额</el-col>
          </el-row>
          <el-row :gutter="24" class="row-txt">
            <el-col :span="6">{{ orderInfo.lesson.name }}</el-col>
            <el-col :span="6">{{ orderInfo.lesson.original }}</el-col>
            <el-col :span="6">{{ orderInfo.lesson.discount }}</el-col>
            <el-col :span="6">{{ orderInfo.order.totalFee }}</el-col>
          </el-row>
        </div>
      </div>

      <div class="info-item">
        <div class="title">操作信息</div>
        <div class="table-layout">
          <el-row :gutter="24" class="row-txt">
            <el-col :span="8">操作时间</el-col>
            <el-col :span="16">备注</el-col>
          </el-row>
          <template v-for="(item, index) in orderInfo.operate">
            <el-row
              :gutter="24"
              class="row-txt"
              :key="'time' + index"
              v-if="item.info"
            >
              <el-col :span="8">{{ item.time }}</el-col>
              <el-col :span="16">{{ item.info }}</el-col>
            </el-row>
          </template>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
import { getOrderStatusText } from "@/utils/zeng";
import { getOrderList, delOrder, getOrderInfo } from "@/api/business/order";
export default {
  data() {
    return {
      loading: false,
      btnLoading: false,
      btnDisabled: false,
      isDetail: false,
      queryOrderMap: {
        pageNum: 1,
        pageSize: 6,
      },
      orderData: [],
      orderInfo: {
        order: {},
        lesson: {},
        user: {},
        operate: {},
      },
    };
  },
  methods: {
    searchOrder() {
      this.queryOrderMap.pageNum = 1;
      this.getOrderListData();
    },
    // 获取详情
    handleDetail(id) {
      this.getOrderData(id);
      this.isDetail = true;
    },
    // 获取状态文本
    getStatusClass(status) {
      switch (status) {
        case 0:
          return "z-wait";
        case 1:
          return "z-success";
      }
    },
    getOrderType(order) {
      switch (order.status) {
        case 0:
          return "待付款";
        case 1:
          if (order.type == 0) {
            return "支付宝支付";
          } else {
            return "微信支付";
          }
      }
    },
    getOrderTime(order) {
      switch (order.status) {
        case 0:
          return "待付款";
        case 1:
          return order.modifiedTime;
      }
    },
    getStatusText(status) {
      return getOrderStatusText(status);
    },
    async getOrderData(id) {
      let res = await getOrderInfo(id);
      let { code, data } = res;
      if (code === 200) {
        this.orderInfo.order = data.orderVO;
        this.orderInfo.lesson = data.lessonVO;
        this.orderInfo.lesson.logo = data.lessonVO.logo;
        this.orderInfo.operate = data.operate;
        this.orderInfo.user = data.sysUser;
      }
    },
    async getOrderListData() {
      this.loading = true;
      let res = await getOrderList(this.queryOrderMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.orderData = data.rows;
        this.total = data.total;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取列表数据错误：" + msg);
      }
    },
    /**
     * 删除
     */
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该订单, 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      ).catch(() => {
        this.$message({
          type: "info",
          message: "已取消删除",
        });
      });
      if (res === "confirm") {
        let res = await delOrder(id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("订单删除成功");
        } else {
          this.msgInfo("订单删除失败:" + msg);
        }
        this.getOrderListData();
      }
    },
  },
  created() {
    this.getOrderListData();
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/common.scss";

.order {
  height: 100%;
  padding: 0px;
}
::v-deep .table-info-item {
  display: flex;
  justify-content: center;
  align-items: center;
  img {
    vertical-align: middle;
  }
}
::v-deep .el-card__header {
  padding: 0px;
}
.info-card {
  margin: 10px;
  border: 1px solid #ebeef5;
  background-color: #fff;
  color: #303133;
  -webkit-transition: 0.3s;
  transition: 0.3s;
  .header-status {
    background: #f2f6fc;
    height: 80px;
    line-height: 80px;
    display: flex;
    align-items: center;
    .status {
      padding-left: 20px;
      font-size: 14px;
      i {
        padding-right: 5px;
      }
    }
    .btn-back {
      margin-left: 60%;
      cursor: pointer;
      text-align: center;
      display: inline-block;
      height: 25px;
      line-height: 25px;
      width: 80px;
      font-size: 12px;
      background-color: #fff;
      border: 1px solid rgba(grey, 0.5);
    }
    .btn-note {
      margin-left: 20px;
      cursor: pointer;
      text-align: center;
      display: inline-block;
      height: 25px;
      line-height: 25px;
      width: 80px;
      font-size: 12px;
      background-color: #fff;
      border: 1px solid rgba(grey, 0.5);
    }
  }
}
::v-deep .info-item {
  .title {
    font-size: 12px;
    padding: 20px;
  }
  .table-layout {
    .el-col {
      border-right: 1px solid #dcdfe6;
      border-bottom: 1px solid #dcdfe6;
      padding: 10px;
      font-size: 14px;
      text-align: center;
    }
    .row-title {
      .el-col {
        background: #f2f6fc;
        color: #303133;
      }
    }
  }
  .row-info {
    .el-col {
      height: 70px;
      line-height: 70px;
    }
  }
}
</style>
