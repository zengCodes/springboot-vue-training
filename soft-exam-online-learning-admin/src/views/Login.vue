<template>
  <div id="login">
    <h1
      style="
        position: absolute;
        color: #fff;
        left: 50%;
        transform: translateX(-50%);
        top: -110px;
      "
    >
      软考-在线培训管理系统
    </h1>
    <el-form
      :model="userLoginForm"
      :rules="loginRules"
      status-icon
      ref="userLoginFormRef"
      label-position="left"
      label-width="0px"
      class="demo-ruleForm login-page"
    >
      <h3 class="title">系统登录</h3>
      <el-form-item prop="username">
        <el-input
          type="text"
          @keyup.enter.native="handleSubmit"
          v-model="userLoginForm.username"
          auto-complete="off"
          placeholder="用户名"
          prefix-icon="iconfont el-icon-user"
        ></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          @keyup.enter.native="handleSubmit"
          type="password"
          v-model="userLoginForm.password"
          auto-complete="off"
          placeholder="密码"
          prefix-icon="el-icon-suitcase-1"
        ></el-input>
      </el-form-item>
      <!-- 验证码 -->
      <el-row :gutter="20">
        <el-col :span="11">
          <el-form-item prop="code">
            <el-input
              placeholder="验证码"
              v-model="userLoginForm.code"
              @keyup.enter.native="handleSubmit"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col class="line" :span="1">&nbsp;</el-col>
        <el-col :span="8">
          <img :src="codeUrl" width="160px" @click="getCode" />
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item prop="rememberMe">
            <el-checkbox
              v-model="userLoginForm.rememberMe"
              style="margin: 0px 0px 25px 0px"
              >记住密码</el-checkbox
            >
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item style="width: 100%">
        <div style="float: right">
          <el-button
            type="primary"
            class="el-icon-mobile-phone"
            @click="handleSubmit"
            >登录</el-button
          >
          <el-button class="el-icon-refresh" @click="resetForm">重置</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { getCodeImg } from "@/api/login";
import { encrypt, decrypt } from "@/utils/jsencrypt";

export default {
  name: "login",
  data() {
    return {
      //表单用户登入数据
      userLoginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: "",
      },
      // 验证码
      codeUrl: "",
      //验证规则
      loginRules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          {
            min: 3,
            max: 12,
            message: "长度在 3 到 12 个字符",
            trigger: "blur",
          },
        ],
        password: [
          { required: true, message: "请输入用户密码", trigger: "blur" },
          {
            min: 6,
            max: 15,
            message: "长度在 6 到 15 个字符",
            trigger: "blur",
          },
        ],
        code: [
          { required: true, message: "请输入验证码", trigger: "blur" },
          {
            min: 5,
            max: 5,
            message: "验证码长度为5个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  components: {},

  methods: {
    // 获取验证码
    getCode() {
      getCodeImg().then((res) => {
        let { code, img, msg, uuid } = res;
        if (code === 200) {
          this.codeUrl = "data:image/gif;base64," + img;
          this.userLoginForm.uuid = uuid;
        }
      });
    },
    // 获取cookie
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      this.userLoginForm = {
        username:
          username === undefined ? this.userLoginForm.username : username,
        password:
          password === undefined
            ? this.userLoginForm.password
            : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      };
    },
    //登入提交
    handleSubmit: function () {
      this.$refs.userLoginFormRef.validate((valid) => {
        if (!valid) {
          return;
        } else {
          this.success();
        }
      });
    },
    //重置表单
    resetForm() {
      this.$refs.userLoginFormRef.resetFields();
    },
    //验证成功
    async success() {
      this.loading = true;
      if (this.userLoginForm.rememberMe) {
        Cookies.set("username", this.userLoginForm.username, { expires: 30 });
        Cookies.set("password", encrypt(this.userLoginForm.password), {
          expires: 30,
        });
        Cookies.set("rememberMe", this.userLoginForm.rememberMe, {
          expires: 30,
        });
      } else {
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      this.$store
        .dispatch("Login", this.userLoginForm)
        .then(() => {
          // this.$router.push({ path: "/home" }).catch(() => {});
          this.$router.push({ path:  "/home" }).catch(() => {});
        })
        .catch(() => {
          this.loading = false;
          this.getCode();
        });
    },
  },
  mounted() {},
  created() {
    this.getCode();
    this.getCookie();
  },
};
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100%;
}

#login {
  position: relative;
}

.login-page {
  position: relative;
  -webkit-border-radius: 5px;
  border-radius: 5px;
  margin: 190px auto;
  width: 370px;
  padding: 40px 35px 15px;
  background: #fff;
  border: 1px solid #eaeaea;
}

label.el-checkbox.rememberme {
  margin: 0px 0px 15px;
  text-align: left;
}
</style>
