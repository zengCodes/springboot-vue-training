<template>
  <div class="nav">
    <ul class="left-nav">
      <li v-for="(item, index) in list" :key="index" class="nav-item">
        <router-link :to="item.path" class="nav-link">
          <img
            src="@/assets/images/logo/logo.png"
            alt=""
            v-if="item.path === 'home'"
          />
          <span v-else>{{ item.title }}</span>
        </router-link>
      </li>
    </ul>
    <ul class="right-nav">
      <li class="item sign" v-if="!isLogin">
        <span class="sign-btn" @click="handleLoginClick">登录</span>/
        <span class="sign-btn" @click="handleRegisterClick">注册</span>
      </li>

      <li class="item" v-else>
        <img :src="userAvatar" alt="" @click="showInfo" />
      </li>
    </ul>
  </div>
</template>
<script>
import { mapState } from 'vuex'

export default {
  props: {
    list: {
      type: Array,
      default() {
        return []
      },
    },
  },
  methods: {
    // 获取导航栏的背景图片
    getBackgroundImage(icon) {
      return {
        background: `url(${icon}) no-repeat center center`,
      }
    },
    // 登录点击
    handleLoginClick() {
      console.log('login')
      this.$store.dispatch('setShowLogin', true)
      this.$store.dispatch('setLoginAction', 'login')
    },
    // 注册点击
    handleRegisterClick() {
      this.$store.dispatch('setShowLogin', true)
      this.$store.dispatch('setLoginAction', 'register')
    },
    showInfo() {},
  },
  computed: {
    ...mapState({
      userInfo: state => state.user,
    }),
    isLogin() {
      return JSON.stringify(this.userInfo.user) != '{}'
    },
    userAvatar() {
      return process.env.VUE_APP_BASE_API + this.userInfo.user.avatar
    },
  },
}
</script>
<style lang="scss" scoped>
.nav {
  .left-nav {
    float: left;
    overflow: hidden;
  }
  .right-nav {
    float: right;
    overflow: hidden;
    line-height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    .item {
      position: relative;
      width: 200px;
      height: 70px;
      border: 1px solid black;
    }
    img {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      right: 80px;
      cursor: pointer;
    }
  }
  .nav-item {
    float: left;
    .nav-link {
      display: block;
      position: relative;
      padding: 0 20px;
      height: 72px;
      line-height: 72px;
      color: #4d555d;
      .nav-icon {
        position: absolute;
        top: 14px;
        right: 4px;
        width: 16px;
        height: 16px;
      }
    }
  }
  .sign {
    margin-left: 10px;
    .sign-btn {
      display: inline-block;
      padding: 0 18px;
      cursor: pointer;
      &:hover {
        color: #f01414;
      }
    }
  }
}
</style>
