<template>
  <!-- 登录页 -->
  <div id="login-register">
    <div class="login" v-show="current == true">
      <el-form
        :model="userLoginForm"
        :rules="loginRules"
        :inline="true"
        status-icon
        ref="userLoginFormRef"
        label-position="left"
        label-width="0px"
        class="demo-ruleForm login_form"
      >
        <div class="con">
          <h3 class="title">软考在线培训系统-登录</h3>
          <el-form-item prop="username">
            <el-input
              @keyup.enter.native="handleSubmit"
              v-model="userLoginForm.username"
              class="el-input-text"
              type="text"
              auto-complete="off"
              placeholder="用户名"
              prefix-icon="iconfont el-icon-user"
            ></el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              @keyup.enter.native="handleSubmit"
              v-model="userLoginForm.password"
              class="el-input-text"
              type="password"
              auto-complete="off"
              placeholder="密码"
              prefix-icon="el-icon-suitcase-1"
            ></el-input>
          </el-form-item>

          <div class="other_login">
            <el-form-item>
              <el-button
                class="check-register"
                type="primary"
                @click="showToggle"
                >还没有账号？去注册</el-button
              >
            </el-form-item>
            <el-form-item>
              <el-tooltip content="QQ" placement="bottom">
                <el-button type="primary" circle class="login_qq">
                  <i class="iconfont">&#xe659;</i>
                </el-button>
              </el-tooltip>

              <el-tooltip content="微信" placement="bottom">
                <el-button type="primary" circle class="login_weixin">
                  <i class="iconfont">&#xe6db;</i>
                </el-button>
              </el-tooltip>
            </el-form-item>
          </div>
          <el-checkbox v-model="userLoginForm.rememberMe" class="rememberme"
            >记住密码</el-checkbox
          >
          <el-row>
            <el-button
              class="login-button"
              type="primary"
              @click="handleSubmit"
              :loading="loading"
              >登录</el-button
            >
            <el-button
              class="el-icon-refresh"
              @click="resetForm('userLoginFormRef')"
              >重置</el-button
            >
          </el-row>
        </div>
      </el-form>
      <!-- 验证码 -->
      <Vcode
        :show="isShow"
        @success="success"
        @close="close"
        :canvasWidth="500"
        :canvasHeight="350"
      />
    </div>
    <div class="register" v-show="current == false">
      <el-form
        :model="userRegisterForm"
        :rules="loginRules"
        :inline="true"
        status-icon
        ref="userRegisterFormRef"
        label-position="left"
        label-width="0px"
        class="demo-ruleForm redister_form"
      >
        <div class="con">
          <h3 class="title">注册</h3>
          <el-form-item prop="userName">
            <el-input
              @keyup.enter.native="register"
              v-model="userRegisterForm.userName"
              class="el-input-text"
              type="text"
              auto-complete="off"
              placeholder="用户名"
              prefix-icon="iconfont el-icon-user"
            ></el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              @keyup.enter.native="register"
              v-model="userRegisterForm.password"
              class="el-input-text"
              type="password"
              auto-complete="off"
              placeholder="密码"
              prefix-icon="el-icon-suitcase-1"
            ></el-input>
          </el-form-item>

          <div class="btn_code">
            <el-form-item>
              <el-button class="check-login" type="primary" @click="showToggle"
                >已有账号？去登录</el-button
              >
            </el-form-item>
          </div>
          <el-row>
            <el-button class="register-button" type="primary" @click="register"
              >注册</el-button
            >
            <el-button
              class="el-icon-refresh"
              @click="resetForm('userRegisterFormRef')"
              >重置</el-button
            >
          </el-row>
        </div>
      </el-form>
    </div>
    <div class="login_wrap">
      <vue-particles
        color="#fff"
        :particleOpacity="0.7"
        :particlesNumber="80"
        shapeType="star"
        :particleSize="4"
        linesColor="#fff"
        :linesWidth="1"
        :lineLinked="true"
        :lineOpacity="0.4"
        :linesDistance="150"
        :moveSpeed="3"
        :hoverEffect="true"
        hoverMode="grab"
        :clickEffect="true"
        clickMode="push"
        class="lizi"
      ></vue-particles>
    </div>
  </div>
</template>
<script>
import Vcode from 'vue-puzzle-vcode'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { addUser } from '@/api/system/user'
import Cookies from 'js-cookie'

export default {
  data() {
    return {
      isShow: false,
      current: true,
      dialogVisible: false,
      imgCode: undefined,
      //表单用户登入数据
      loading: false,
      userLoginForm: {
        username: 'test',
        password: 'admin123',
        rememberMe: false,
      },
      userRegisterForm: {
        userName: '',
        password: '',
      }, //添加表单
      //验证规则
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
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
      },
    }
  },
  components: {
    Vcode,
  },

  methods: {
    //登录注册切换
    showToggle() {
      this.current = !this.current
    },
    //登入提交
    handleSubmit: function () {
      this.$refs.userLoginFormRef.validate(valid => {
        if (!valid) {
          return
        } else {
          this.isShow = true
        }
      })
    },
    //重置表单
    resetForm(val) {
      this.$refs[val].resetFields()
    },
    // 获取cookie
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.userLoginForm = {
        username:
          username === undefined ? this.userLoginForm.username : username,
        password:
          password === undefined
            ? this.userLoginForm.password
            : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      }
    },
    //验证成功
    success() {
      this.loading = true
      this.loading = true
      if (this.userLoginForm.rememberMe) {
        Cookies.set('username', this.userLoginForm.username, { expires: 30 })
        Cookies.set('password', encrypt(this.userLoginForm.password), {
          expires: 30,
        })
        Cookies.set('rememberMe', this.userLoginForm.rememberMe, {
          expires: 30,
        })
      } else {
        Cookies.remove('username')
        Cookies.remove('password')
        Cookies.remove('rememberMe')
      }
      this.$store
        .dispatch('Login', this.userLoginForm)
        .then(() => {
          this.$router.push({ path: '/index' }).catch(() => {})
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 注册用户
     */
    async register() {
      this.$refs.userRegisterFormRef.validate(async valid => {
        if (!valid) {
          return
        } else {
          let res = await addUser(this.userRegisterForm)
          let { code, msg } = res
          if (code === 200) {
            this.$notify.success({
              title: '操作成功',
              message: '用户注册成功,跳回登录',
            })
            this.current = true
          } else {
            this.$message.error('用户注册失败:' + msg)
          }
        }
      })
    },
    close() {
      this.isShow = false
    },
  },
  created() {
    this.getCookie()
  },
}
</script>
<style lang="scss" scoped>
#login-register {
  overflow-y: hidden;
  padding: 0;
  margin: 0;
  .login,
  .register {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .lizi {
    background: url('~@/assets/images/bg1.png') center;
    background-size: 100% 100%;
    overflow-y: hidden;
    min-height: 100%;
    height: calc(100% - 100px);
  }
  // 登录-注册按钮
  .login_form {
    .login-button {
      margin-right: 30px;
    }
  }
  .redister_form {
    .register-button {
      margin-right: 30px;
      margin-top: 30px;
    }
  }
}
/*登录框样式*/
::v-deep .login_form,
.redister_form {
  color: #ffffff;
  position: absolute;
  top: 20%;
  left: 50%;
  transform: translateX(-50%);
  width: 450px;
  height: 450px;
  background: linear-gradient(to bottom, #132536, #132536);
  animation: dynamics 6s ease infinite;
  -webkit-animation: dynamics 6s ease infinite;
  -moz-animation: dynamics 6s ease infinite;
  opacity: 0.9;
  border: solid 1px #13a1fc;
  background-size: 1400% 300%;
  .con {
    position: relative;
    z-index: 9999;
    box-shadow: -15px 15px 15px rgba(6, 17, 47, 0.7);
    background: linear-gradient(#13a1fc, #13a1fc) left top,
      linear-gradient(#13a1fc, #13a1fc) left top,
      linear-gradient(#13a1fc, #13a1fc) right top,
      linear-gradient(#13a1fc, #13a1fc) right top,
      linear-gradient(#13a1fc, #13a1fc) left bottom,
      linear-gradient(#13a1fc, #13a1fc) left bottom,
      linear-gradient(#13a1fc, #13a1fc) right bottom,
      linear-gradient(#13a1fc, #13a1fc) right bottom;
    background-repeat: no-repeat;
    background-size: 3px 20px, 20px 3px;
    height: 100%;
    margin: -2px;
    padding: 3px 1px 1px 0;
    border-radius: 3px;
  }
  .el-input-text .el-input__inner {
    display: block;
    margin: 10px auto;
    width: 350px;
    height: 35px;
    background-color: #ffffff;
    border-radius: 4px;
    opacity: 0.9;
    border: 0;
    font-size: 16px;
    padding-left: 50px;
  }
  // 切换注册登录按钮
  .check-login {
    margin-left: -190px;
  }
  .check-register {
    margin-left: -16px;
  }
}
.con .title {
  position: relative;
  font-size: 28px;
  font-weight: bold;
  line-height: 24px;
  letter-spacing: 2px;
  display: block;
  margin: 25px 0 60px 0;
}
.con .title::after {
  content: '';
  width: 150px;
  height: 3px;
  background: #ffffff;
  margin-top: 30px;
  margin-bottom: 20px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translateX(-50%);
}
.other_login {
  width: 320px;
  margin: 0 auto;
  display: flex;
  .login_weixin,
  .login_qq {
    margin-left: 30px;
  }
}
.rememberme {
  margin: 20px auto;
}
</style>
