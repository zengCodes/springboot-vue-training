<template>
  <div class="pay">
    <section class="pay-lesson">
      <p class="pay-left-title">支付信息确认</p>
      <span class="pay-right-title">订单编号：{{ orderInfo.orderNo }}</span>
      <span class="copy-btn">复制订单号</span>
    </section>
    <h3>订单支付</h3>
    <div class="order-main">
      <p class="info-item">
        {{ orderInfo.courseTitle }} (讲师：{{ orderInfo.teacherName }})
      </p>
      <p class="info-time">有效期：<span>7天</span></p>
    </div>
    <div class="tip-main">
      温馨提示：
      考前45天以上报名的，在当次考试考前45天不接受延期处理。考前45天以内报名的，在报名时间超过7天不接受延期处理。
    </div>
    <div class="pay-way-container">
      <!-- 支付方式 -->
      <div class="way-list">
        <p class="way-title">支付方式</p>
        <div
          v-for="(item, index) in payWayList"
          :key="index"
          class="way-item"
          :class="[{ active: currentWayIndex == index }, getClassName(item)]"
          :style="getBackground(item)"
          @click="currentWayIndex = index"
        >
          <!-- 勾选样式 -->
          <div v-if="currentWayIndex == index" class="way-check">
            <i class="iconfont icon-gouxuan"></i>
          </div>
        </div>
      </div>

      <!-- 支付按钮 -->
      <div class="pay-bottom">
        <div class="right">
          <p class="pay-total">
            应付金额：<span>¥ {{ orderInfo.totalFee }}</span>
          </p>
          <button
            class="pay-btn"
            :class="{ 'is-disabled': isDisabled }"
            @click="handlePaySubmit"
          >
            立即支付
          </button>
          <p class="tips">付款有问题？点我</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  getOrderInfoByNo,
  orderPay,
  getOrderStatus,
} from "@/api/business/order";
import { mapState } from "vuex";

export default {
  data() {
    return {
      isDisabled: false,
      charge: 0,
      showList: true,
      currentWayIndex: 0,
      payWayList: [],
      orderInfo: {},
    };
  },
  methods: {
    // 获取支付方式的样式
    getClassName(item) {
      const classMap = {
        0: "Alipay",
        1: "Wxpay",
      };
      return classMap[item.type];
    },
    // 获取背景图片
    getBackground(item) {
      return {
        "background-image": `url(${item.img})`,
      };
    },
    // 根据订单号获取订单信息
    async getOrderInfo() {
      let res = await getOrderInfoByNo(this.code);
      let { code, data } = res;
      if (code === 200) {
        this.orderInfo = data;
      }
    },
    // 在进入页面之前已经判断过了，这里此处可以删掉
    async getOrderPayStatus() {
      let res = await getOrderStatus(this.userInfo.user.userId, this.code);
      return res;
    },
    // 支付
    async handlePaySubmit() {
      // 判断是否支付过
      let isPay = await this.getOrderPayStatus();
      console.log(isPay);
      if (isPay?.data) {
        this.msgWarning("您已经支付过了！！！");
        return false;
      }
      let res = await orderPay({
        orderNum: this.code,
        type: this.way,
        total: this.orderInfo.totalFee,
      });
      this.isDisabled = false;
      const { code, msg } = res;
      if (code === 200) {
        this.$confirm("支付成功，是否查看订单详情？", "提示", {
          confirmButtonText: "确定",
          type: "success",
        }).then(
          () => {
            this.$router.replace("/userCenter");
          },
          () => {
            this.$router.replace("/lesson");
          }
        );
      } else {
        this.$message.error(msg);
      }
    },
  },
  created() {
    this.payWayList = [
      {
        type: 0,
        name: "支付宝",
        img: "https://order.imooc.com/static/module/pay/center/img/pay_s.png",
      },
      {
        type: 1,
        name: "微信",
        img: "https://order.imooc.com/static/module/pay/center/img/pay_s.png",
      },
    ];
  },
  mounted() {
    this.getOrderInfo();
  },
  computed: {
    way() {
      return this.payWayList[this.currentWayIndex].type;
    },
    code() {
      return this.$route.query.orderNo;
    },
    ...mapState({
      userInfo: (state) => state.user,
    }),
  },
};
</script>
<style lang="scss" scoped>
.pay {
  position: relative;
  margin: 20px auto 30px;
  padding: 0 20px;
  width: 1200px;
  background-color: #fff;
  text-align: left;
}
.pay-lesson {
  height: 70px;
  line-height: 70px;
  font-size: 20px;
  color: #1f3d4d;
  border-bottom: 1px solid #ededed;
  position: relative;
  .pay-left-title {
    position: absolute;
    left: 0px;
    top: 50%;
    transform: translateY(-50%);
  }
  .pay-right-title {
    position: absolute;
    right: 100px;
    top: 50%;
    transform: translateY(-50%);
    color: #2590d7;
    font-size: 14px;
  }
  .copy-btn {
    position: absolute;
    right: 0px;
    top: 50%;
    transform: translateY(-50%);
    cursor: pointer;
    display: inline-block;
    width: 86px;
    height: 32px;
    line-height: 32px;
    text-align: center;
    background-color: #f2f7fa;
    border: solid 1px #e5e5e5;
    font-size: 14px;
    color: #999999;
    margin-left: 10px;
    vertical-align: middle;
  }
}
h3 {
  font-size: 20px;
  padding: 10px;
}
::v-deep.order-main {
  margin-top: 10px;
  background-color: #f2f7fa;
  padding: 28px 20px;
  margin-bottom: 20px;
  line-height: 30px;
  .info-item {
    font-size: 16px;
    color: #1f3d4d;
  }
  .info-time {
    font-size: 12px;
    span {
      color: #7a8f99;
      font-size: 12px;
    }
  }
}
.tip-main {
  font-size: 14px;
  color: #cd8447;
  height: 48px;
  border-radius: 5px;
  background-color: #fff7e6;
  border: solid 1px #fde5bf;
  line-height: 48px;
  padding-left: 30px;
  margin: 0px 0px 30px 0px;
}
.pay-way-container {
  padding: 30px 12px 48px;
  border-top: 1px solid #d9dde1;
  .way-list {
    .way-title {
      padding-bottom: 15px;
      font-size: 16px;
      line-height: 36px;
      color: #07111b;
    }
  }
  .way-item {
    position: relative;
    display: inline-block;
    vertical-align: middle;
    margin-right: 18px;
    width: 194px;
    height: 86px;
    background-color: #f3f5f7;
    border: 2px solid #f3f5f7;
    cursor: pointer;
    border-radius: 6px;
    background-repeat: no-repeat;
    background-position: center center;
    .way-check {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 0;
      height: 0;
      border: 10px solid #f01414;
      border-color: transparent #f01414 #f01414 transparent;
      text-align: center;
      .iconfont {
        z-index: 99;
        display: inline-block;
        position: absolute;
        top: -1px;
        right: -10px;
        color: #fff;
        font-size: 12px;
      }
    }

    &.active {
      border: 2px solid #f01414;
    }
    &.Alipay {
      background-position: 33px -304px;
    }
    &.Wxpay {
      background-position: 30px -423px;
    }
    &.Account {
      position: relative;
      background-position: left 20px center;
      background-size: 48px 48px;
    }
    .account-info {
      position: absolute;
      left: 80px;
      top: 50%;
      transform: translateY(-50%);
      .title {
        font-size: 20px;
        color: #1c1f21;
        font-weight: 700;
        line-height: 30px;
      }
      .balance {
        font-size: 12px;
        color: #9199a1;
        line-height: 16px;
      }
    }
  }
  .pay-bottom {
    margin-top: 48px;
    padding-top: 36px;
    overflow: hidden;
    border-top: 1px solid #d9dde1;
    .left {
      float: left;
      .pay-tips {
        color: #f01414;
        font-size: 14px;
        line-height: 30px;
      }
    }
    .right {
      float: right;
      .pay-total {
        font-size: 14px;
        color: #07111b;
        line-height: 36px;
        span {
          display: inline-block;
          vertical-align: middle;
          font-size: 22px;
          color: #f01414;
        }
      }
      .pay-btn {
        margin-left: auto;
        margin-top: 32px;
        margin-bottom: 16px;
        width: 140px;
        height: 40px;
        border: none;
        outline: none;
        text-align: center;
        line-height: 36px;
        background-color: #f01414;
        color: #fff;
        font-size: 14px;
        font-weight: 700;
        cursor: pointer;
        &.is-disabled {
          cursor: not-allowed;
          pointer-events: none;
          background-color: rgba(240, 20, 20, 0.5);
        }
      }
      .tips {
        font-size: 12px;
        color: #4d555d;
        line-height: 24px;
        text-align: right;
        cursor: pointer;
        &:hover {
          color: #f01414;
        }
      }
    }
  }
}
</style>
