<template>
  <div class="order">
    <section class="order-lesson">
      <p class="order-title">课程信息确认</p>
      <img :src="courseInfo.logo" class="order-img" />
      <div class="order-right">
        <h1>{{ courseInfo.name }}</h1>
        <a href="javascript:;">{{ courseInfo.nickName }}</a>
        <p class="price">¥{{ courseInfo.original }} ·课前随时退</p>
        <div class="detail-main">
          <h2 class="main-title">{{ courseInfo.type }}</h2>
          <p>2021-9-6 至 2026-8-10</p>
        </div>
      </div>
    </section>
    <section class="purchase-mobile">
      <p class="mobile-title">
        <span class="mobile-title-text">课程提醒发送至</span
        ><a href="javascript:void()" class="mobile-update" @click="updateMobile"
          >修改</a
        >
      </p>
      <p class="mobile-phone">手机号：{{ userData.user.phone }}</p>
      <div class="phone-checkbox">
        <el-checkbox v-model="orderMobileAgree"
          ><span>
            <p>允许将手机号授权给机构以提供教学服务，不勾选不会授权</p>
          </span></el-checkbox
        >
      </div>
    </section>
    <section class="purchase-bottom">
      <p class="purchase-bottom-title">需支付金额</p>
      <div class="purchase-bottom-content">
        <div class="purchase-bottom-rows">
          <p class="purchase-bottom-label">课程价格</p>
          <p class="purchase-bottom-right">¥{{ courseInfo.original }}</p>
        </div>
        <div class="purchase-bottom-rows">
          <p class="purchase-bottom-label">机构优惠</p>
          <p class="purchase-bottom-right">-¥{{ courseInfo.discount }}</p>
        </div>
        <div class="purchase-bottom-rows">
          <p class="purchase-bottom-label">应付金额</p>
          <p class="purchase-bottom-right">¥{{ courseInfo.total }}</p>
        </div>
      </div>
      <p class="purchase-checkbox">
        <el-checkbox v-model="orderAgree"
          ><span
            >同意<a href="javascript:;" class="agreement">服务协议</a></span
          ></el-checkbox
        >
      </p>
      <span class="payment-btn" @click="handleOrderSubmit"> 去付款 </span>
    </section>
  </div>
</template>
<script>
import { getLesson } from "@/api/business/lesson/lesson";
import { mapState } from "vuex";
import { createOrder } from "@/api/business/order";
export default {
  data() {
    return {
      orderMobileAgree: undefined,
      orderAgree: undefined,
      courseInfo: {},
    };
  },
  methods: {
    updateMobile() {},
    // 付款
    async handleOrderSubmit() {
      // 判断是否勾选协议
      if (this.orderAgree != undefined && this.orderAgree != "") {
        let order = {
          lesson: this.$route.query.lesson,
          user: this.userData.user.userId,
          phone: this.userData.user.phone,
          pay: this.courseInfo.total,
          type: 1,
        };
        let res = await createOrder(order);
        let { code, msg } = res;
        if (code === 200) {
          // 跳转到支付页面
          this.$router.replace({
            path: "pay",
            query: {
              orderNo: msg,
            },
          });
        }
      } else {
        alert("请勾选协议");
      }
    },
    // 获取课程信息
    async getLessonInfo(id) {
      let res = await getLesson(id);
      let { code, data } = res;
      if (code === 200) {
        this.courseInfo = data;
      }
    },
  },
  created() {
    let lesson = this.$route.query.lesson;
    this.getLessonInfo(lesson);
  },
  mounted() {},
  computed: {
    ...mapState({
      userData: (state) => state.user,
    }),
  },
};
</script>
<style lang="scss" scoped>
.order {
  position: relative;
  margin: 20px auto 30px;
  padding: 0 20px;
  width: 1200px;
  background-color: #fff;
  text-align: left;
}
.order-lesson {
  padding: 30px 0;
  height: 200px;
  border-bottom: 1px solid #eee;
  .order-title {
    font-size: 16px;
    color: #333;
    margin-bottom: 15px;
  }
  .order-img {
    float: left;
    width: 350px;
    height: 160px;
  }
  .order-right {
    float: left;
    margin-left: 30px;
    width: 720px;
    h1 {
      line-height: 1.5;
      overflow: hidden;
      white-space: nowrap;
      -o-text-overflow: ellipsis;
      text-overflow: ellipsis;
    }
    a {
      display: inline-block;
      font-size: 14px;
      color: #999;
    }
  }
  .price {
    margin-top: 5px;
    font-size: 16px;
    color: #ff0042;
    line-height: 24px;
  }
  .detail-main {
    position: relative;
    margin-top: 20px;
    padding: 5px 15px 6px;
    border: 1px solid #ddd;
    line-height: 2.5;
    .main-title {
      position: absolute;
      padding: 0 5px;
      top: -20px;
      line-height: 35px;
      font-size: 16px;
      background-color: #fff;
    }
  }
}
::v-deep .purchase-mobile {
  padding: 30px 0;
  border-bottom: 1px solid #eee;
  .mobile-title {
    margin-bottom: 30px;
    margin-right: 10px;
    font-size: 16px;
    color: #000;
    .mobile-title-text {
      margin-right: 10px;
    }
  }
  .mobile-update {
    font-size: 14px;
    color: #25bb9b;
  }
  .phone-checkbox {
    margin-top: 30px;
    width: 100%;
    font-size: 14px;
    display: inline-block;
    vertical-align: middle;
    margin-right: 10px;
    line-height: 20px;
    cursor: pointer;
  }
}
::v-deep .purchase-bottom {
  position: relative;
  padding: 30px 0;
  border-bottom: 1px solid #eee;
  .purchase-bottom-title {
    margin-bottom: 20px;
    font-size: 16px;
    color: #000;
  }
  .purchase-bottom-content {
    margin-bottom: 30px;
    font-size: 14px;
    .purchase-bottom-rows {
      margin-bottom: 10px;
      min-height: 30px;
      line-height: 30px;
      .purchase-bottom-label {
        float: left;
        width: 85px;
        text-align: left;
      }
      .purchase-bottom-right {
        margin-left: 85px;
      }
    }
  }
  .purchase-checkbox {
    margin-bottom: 10px;
    line-height: 20px;
    .agreement {
      text-decoration: none;
      color: #25bb9b;
    }
  }
  .payment-btn {
    cursor: pointer;
    display: inline-block;
    height: 30px;
    width: 100px;
    line-height: 30px;
    color: #fff;
    font-size: 15px;
    text-align: center;
    background-color: #d51423;
  }
}
::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
.el-checkbox__input.is-indeterminate .el-checkbox__inner {
  background-color: #25bb9b;
  border-color: #25bb9b;
}
::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #25bb9b;
}
</style>
