<template>
  <div class="login-container">
    <div class="login-mask" @click="handleMaskClick" />
    <div class="login-main">
      <div class="login-tab">
        <span
          v-for="(item, index) in loginTabs"
          :key="index"
          class="login-tab-item"
          :class="{ active: index == currentTabIndex }"
          @click="currentTabIndex = index"
          >{{ item }}</span
        >
        <i class="login-close el-icon-close" @click="setShowLogin(false)"></i>
      </div>
      <component :is="componentName" :index="currentTabIndex" />
      <div v-show="componentName != 'qrcode-way'" class="three-login-way">
        <span class="phone-login">{{ threeTitle }}</span>
        <span class="three-way-item">
          <i class="iconfont wechart">&#xe6db;</i>
          <i class="iconfont qq">&#xe659;</i>
        </span>
      </div>
    </div>
  </div>
</template>
<script>
import LoginWay from './login.vue'
import QrcodeWay from './wx-login.vue'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      loginTabs: ['登录', '注册'],
      currentTabIndex: 0,
      componentName: 'login-way',
    }
  },
  created() {
    console.log('---------' + this.loginAction)
    this.currentTabIndex = this.loginAction === 'login' ? 0 : 1
  },
  methods: {
    setShowLogin(flag) {
      this.$store.dispatch('setShowLogin', flag)
    },
    // 遮罩点击
    handleMaskClick() {
      this.$emit('maskClick')
    },
    // 二维码点击事件
    handleQrcodeClick() {
      this.componentName =
        this.componentName === 'qrcode-way' ? 'login-way' : 'qrcode-way'
    },
  },
  computed: {
    getQrcodeBackground() {
      let background =
        this.componentName === 'qrcode-way' ? 'pcLogin.png' : 'erweima.png'
      return {
        background: `url('https://www.imooc.com/static/img/${background}') no-repeat 0 0`,
      }
    },
    threeTitle() {
      return this.currentTabIndex === 0 ? '账号密码登陆' : '其它登陆方式'
    },
    ...mapState({
      loginAction: state => state.login.action,
    }),
  },
  components: {
    LoginWay,
    QrcodeWay,
  },
}
</script>

<style lang="scss" scoped>
.login-container {
  z-index: 333;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  right: 0;
  .login-mask {
    background-color: rgba(0, 0, 0, 0.6);
    z-index: 1000;
    height: 100%;
  }
  .login-main {
    z-index: 100000;
    position: absolute;
    left: 50%;
    top: 50%;
    padding: 10px 0 30px;
    width: 384px;
    transform: translate(-50%, -50%);
    background-color: #fff;
    border-radius: 12px;
    min-height: 300px;
    .login-tab {
      position: relative;
      padding: 0 20px 25px;
      .login-close {
        float: right;
        display: inline-block;
        padding: 0 10px;
        line-height: 50px;
        font-size: 24px;
        color: #787d82;
        cursor: pointer;
        &:hover {
          transform: scale(1.2);
          color: red;
        }
      }
      .login-tab-item {
        display: inline-block;
        width: 80px;
        font-size: 16px;
        line-height: 50px;
        color: #787d82;
        text-align: center;
        font-weight: 700;
        cursor: pointer;
        &.active {
          color: red;
          &::after {
            content: '';
            display: block;
            margin: 0 auto;
            width: 16px;
            height: 4px;
            border-radius: 4px;
            background-color: red;
          }
        }
      }
    }
    .three-login-way {
      margin-top: 22px;
      padding: 0 32px;
      .phone-login {
        display: inline-block;
        vertical-align: middle;
        margin-right: 24px;
        padding-left: 24px;
        padding-right: 24px;
        border-right: 1px solid #1a1c1f21;
        line-height: 24px;
        font-size: 14px;
        color: #f20d0d;
        .three-way-item {
          display: inline-block;
          vertical-align: middle;
          line-height: 24px;
        }
      }
      .iconfont {
        margin: 0 12px;
        display: inline-block;
        vertical-align: middle;
        color: #b5b9bc;
        font-size: 24px;
        cursor: pointer;
        &.weibo:hover {
          color: #f20d0d;
        }
        &.wechart:hover {
          color: #00b33b;
        }

        &.qq:hover {
          color: #0088cc;
        }
      }
    }
    .qrcode-way {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 60px;
      height: 60px;
      background: url('https://www.imooc.com/static/img/erweima.png') no-repeat
        0 0;
      border-bottom-right-radius: 12px;
      cursor: pointer;
    }
  }
}
</style>
