<template>
  <div class="login-way">
    <el-form ref="loginForm" :model="loginForm" :rules="rules">
      <el-form-item prop="username">
        <el-input
          ref="username"
          v-model.trim="loginForm.username"
          placeholder="请输入账号"
          clearable
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          ref="passowrd"
          v-model.trim="loginForm.password"
          placeholder="请输入密码"
          show-password
        />
      </el-form-item>
      <el-form-item v-if="index === 1" prop="ckpassword">
        <el-input
          v-model.trim="loginForm.ckpassword"
          placeholder="请再次输入密码"
          show-password
        />
      </el-form-item>
      <el-form-item class="auto-login-item">
        <template v-if="index == 0">
          <el-checkbox v-model="loginForm.rememberMe">
            七天内自动登录
          </el-checkbox>
          <div class="auto-login-btn-box">
            <span>找回密码</span>
            <span>无法登陆</span>
          </div>
        </template>
        <template v-else>
          <el-checkbox v-model="loginForm.argement"> 同意 </el-checkbox>
          <span class="agreement">《安全注册协议》</span>
        </template>
      </el-form-item>
    </el-form>
    <button
      class="login-btn"
      :class="{
        'is-loading': isLoading || (index == 1 && !loginForm.argement),
      }"
      @click="handleValidateForm"
    >
      {{ btnText }}
    </button>
  </div>
</template>
<script>
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { addUser } from '@/api/system/user'
import { mapMutations } from 'vuex'
export default {
  props: {
    index: Number,
  },
  data() {
    const checkPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.loginForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    const rules = {
      username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        {
          min: 3,
          max: 12,
          message: '长度在 3 到 12 个字符',
          trigger: 'blur',
        },
      ],
      password: [
        { required: true, message: '请输入用户密码', trigger: 'blur' },
        {
          min: 6,
          max: 15,
          message: '长度在 6 到 15 个字符',
          trigger: 'blur',
        },
      ],
      ckpassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        { validator: checkPassword, trigger: 'blur' },
      ],
    }
    return {
      isLoading: false,
      rules: rules,
      loginForm: {
        username: 'test',
        password: 'admin123',
        rememberMe: false,
        ckpassword: '',
        argement: false,
      },
    }
  },
  mounted() {
    // 自动聚焦
    const usernameRef = this.$refs.username
    const passwordRef = this.$refs.password
    if (!this.loginForm.username) {
      usernameRef.focus()
    } else if (!this.loginForm.password) {
      passwordRef.focus()
    }
    // 监听enter事件
    window.addEventListener('keyup', this.handleListenKeyup)
  },
  methods: {
    // 表单校验
    handleValidateForm() {
      if (this.isLoading || (this.index === 1 && !this.loginForm.argement)) {
        return false
      }
      this.$refs['loginForm'].validate(valid => {
        if (valid) {
          this.success()
        }
      })
    },
    //验证成功
    success() {
      this.loading = true
      this.loading = true
      if (this.loginForm.rememberMe) {
        Cookies.set('username', this.loginForm.username, { expires: 30 })
        Cookies.set('password', encrypt(this.loginForm.password), {
          expires: 30,
        })
        Cookies.set('rememberMe', this.loginForm.rememberMe, {
          expires: 30,
        })
      } else {
        Cookies.remove('username')
        Cookies.remove('password')
        Cookies.remove('rememberMe')
      }
      this.$store
        .dispatch('Login', this.loginForm)
        .then(() => {
          this.$router.push({ path: '/index' }).catch(() => {})
        })
        .catch(() => {
          this.loading = false
        })
    },

    // 监听页面enter事件
    handleListenKeyup(e) {
      if (e.keyCode === 13) {
        this.handleValidateForm()
      }
    },
    // 获取cookie
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password:
          password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      }
    },
  },
  watch: {
    index() {
      this.$refs.loginForm.resetFields()
    },
  },
  computed: {
    btnText() {
      let text = ''
      if (this.index === 0) {
        text = this.isLoading ? '登录中...' : '登录'
      } else {
        text = this.isLoading ? '注册中...' : '注册'
      }
      return text
    },
  },
  create() {
    this.getCookie()
  },
  beforeDestroy() {
    window.removeEventListener('keyup', this.handleListenKeyup)
  },
}
</script>
<style lang="scss" scoped>
@import '@/assets/styles/login-form.scss';
</style>
