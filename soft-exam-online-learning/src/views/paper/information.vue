<template>
  <div class="paper">
    <el-card class="card-box">
      <div class="header">
        <img src="@/assets/images/paper/head.png" class="header-img" />
        <span class="header-icon">
          <i class="iconfont icon-item">&#xe63f;</i>
        </span>
      </div>
      <div class="main">
        <div class="main-title">
          <span>【{{ paperYear }}-{{ paperGrade }}】{{ paperData.name }}</span>
        </div>
        <div class="main-info">
          <ul class="row-list">
            <li>
              <div class="row-item item-one">
                <span>题目类型</span>
                <span>选择题</span>
                <span>简答题</span>
              </div>
            </li>
            <li>
              <div class="row-item item-two">
                <span>数目</span>
                <span>{{ choiceNum }}</span>
                <span>{{ answerNum }}</span>
              </div>
            </li>
          </ul>
          <div class="btn-item">
            <span class="submit-button" @click="handleSubmit"
              >提交信息,开始做题</span
            >
          </div>
        </div>
        <div class="main-notice">
          <h4><span>试卷说明</span></h4>
          <ol>
            <li>
              本试卷由企业提供，用户可免费申请练习，成绩优秀用户有机会获得免费获得付费课程机会
            </li>
            <li>
              本试卷由企业提供，用户可免费申请练习，成绩优秀用户有机会获得免费获得付费课程机会
            </li>
            <li>
              本试卷由企业提供，用户可免费申请练习，成绩优秀用户有机会获得免费获得付费课程机会
            </li>
            <li>
              本试卷由企业提供，牛客网用户可免费申请练习，成绩优秀用户有机会获得免费获得付费课程机会
            </li>
          </ol>
        </div>
      </div>
    </el-card>
    <!-- 弹出框填写信息 -->
    <el-dialog
      :title="handleDialogTitle"
      :visible.sync="handleDialogVisible"
      width="40%"
      @close="handleClose"
      custom-class="paper-dialog"
    >
      <el-form
        size="mini"
        :model="handleDialogForm"
        :rules="handleDialogFormRoles"
        ref="handleDialogFormRef"
        class="demo-ruleForm"
      >
        <el-row :gutter="24">
          <el-col :span="24">
            <div class="header-main">
              <p>
                <i class="el-icon-warning"></i>
                首次练习公司真题试卷需要提交基本信息，提交后即可无限制练习该类题目；点击"确定"按钮提交信息，即表示你同意
                《用户隐私政策》。我们会对你的信息严格保密，请认真填写以下信息。
              </p>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item prop="username">
              <el-input
                size="small"
                v-model="handleDialogForm.nickName"
                placeholder="请输入真实姓名"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="sex">
              <el-select
                v-model="handleDialogForm.sex"
                clearable
                placeholder="请选择性别"
                size="small"
                width="100%"
              >
                <el-option
                  v-for="item in sexOption"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item prop="email">
              <el-input
                size="small"
                v-model="handleDialogForm.email"
                placeholder="请输入邮箱"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="phone">
              <el-input
                size="small"
                v-model="handleDialogForm.phone"
                placeholder="请输入手机号"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="dialog-footer">
          <div class="footer-button">
            <el-button
              size="small"
              @click="handleDialogSubmit"
              :disabled="btnDisabled"
              :loading="btnLoading"
              >确 定</el-button
            >
          </div>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { getSexOption } from "@/utils/zeng";
import { mapState } from "vuex";
import Cookies from "js-cookie";
import { getListChoice } from "@/api/business/question/choice";
import { getListAnswer } from "@/api/business/question/answer";
import { saveTestRecord } from "@/api/business/testRecord";
export default {
  name: "Paper",
  data() {
    return {
      btnDisabled: false,
      btnLoading: false,
      //试卷信息
      paperData: {},
      // 性别信息
      sexOption: [],
      handleDialogTitle: "",
      handleDialogVisible: false,
      handleDialogForm: {
        paperId: null,
      },
      // 查询试题
      queryMap: {
        pageNum: 1,
        pageSize: 100,
        paperId: null,
      },
      handleDialogFormRoles: {
        nickName: [
          { required: true, message: "请输入真实姓名", trigger: "blur" },
        ],
        sex: [
          {
            required: true,
            message: "请选择性别",
            trigger: "blur",
          },
        ],
        email: [
          {
            required: true,
            message: "请输入邮箱",
            trigger: "blur",
          },
        ],
        phone: [
          {
            required: true,
            message: "请输入手机号",
            trigger: "blur",
          },
        ],
      },
      choiceNum: 0,
      answerNum: 0,
    };
  },
  created() {
    this.sexOption = getSexOption();
    this.paperData = JSON.parse(Cookies.get("paper"));
    console.log(this.paperData);
    this.queryMap.paperId = this.paperData.id;
    this.getChoiceNum();
    this.getAnswerNum();
  },
  methods: {
    handleClose() {
      this.btnDisabled = false;
      this.btnLoading = false;
      this.handleDialogVisible = false;
      this.handleDialogForm = {};
    },
    handleSubmit() {
      this.handleDialogTitle = "填写信息，开始考试";
      this.handleDialogVisible = true;
    },
    // 弹出框确定
    handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          if (this.handleDialogForm.username != this.userData.user.nickName) {
            this.$confirm("该信息与用户认证信息不一致，是否继续", "提示", {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            })
              .then(() => {
                this.savePaperRecord();
              })
              .catch(() => {
                this.$message({
                  type: "info",
                  message: "已取消",
                });
              });
          } else {
            this.savePaperRecord();
          }
        }
      });
    },
    // 保存考试记录
    async savePaperRecord() {
      this.handleDialogForm.paperId = this.paperData.id;
      this.handleDialogForm.sex = parseInt(this.handleDialogForm.sex);
      let res = await saveTestRecord(this.handleDialogForm);
      let { code, data } = res;
      if (code === 200) {
        this.btnDisabled = true;
        this.btnLoading = true;
        let routeUrl = this.$router.resolve({
          path: "paper",
          query: {
            id: data,
          },
        });
        window.open(routeUrl.href, "_blank");
      }
    },
    // 根据试卷查询题目
    async getChoiceNum() {
      let res = await getListChoice(this.queryMap);
      let { code, data } = res;
      if (code === 200) {
        this.choiceNum = data.total;
      }
    },
    /**
     * 加载试题列表
     */
    async getAnswerNum() {
      let res = await getListAnswer(this.queryMap);
      let { code, data } = res;
      if (code === 200) {
        this.answerNum = data.total;
      }
    },
  },
  computed: {
    ...mapState({
      userData: (state) => state.user,
    }),
    paperYear() {
      return this.paperData.time.split("-")[0];
    },
    paperGrade() {
      let msg = "";
      let month = this.paperData.time.split("-")[1];
      if (month > 6) {
        msg = "上半年";
      } else {
        msg = "下半年";
      }
      return msg;
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
.paper {
  height: 82vh;
}
::v-deep .el-card__body {
  padding: 0px;
}
.card-box {
  width: 75%;
  height: 78vh;
  overflow-y: scroll;
  overflow-x: hidden;
  margin: 20px auto;
}
.header {
  position: relative;
  width: 100%;
  .header-img {
    width: 100%;
    height: 150px;
  }
  .header-icon {
    border: 1px solid chocolate;
    border-radius: 50%;
    z-index: 1;
    display: inline-block;
    width: 80px;
    height: 80px;
    color: chocolate;
    position: absolute;
    bottom: -20px;
    left: 50%;
    transform: translateX(-50%);
    background-color: #fff;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    .icon-item {
      font-size: 50px;
    }
  }
}
.main {
  margin-top: 50px;
  .main-title {
    span {
      letter-spacing: 1;
      font-size: 20px;
      display: inline-block;
      vertical-align: middle;
    }
  }
  .main-info {
    width: 400px;
    margin: 30px auto;
    .row-list {
      width: 100%;
      height: 80px;
      .row-item {
        line-height: 20px;
        height: 40px;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        span {
          flex: 33%;
          font-size: 15px;
          color: rgba(0, 0, 0, 0.8);
        }
      }
      .item-one {
        background-color: #e5e5e5;
      }
      .item-two {
        background-color: #f3f3f3;
      }
    }
    .btn-item {
      margin: 40px auto;
      width: 400px;
      height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      .submit-button {
        cursor: pointer;
        width: 200px;
        height: 35px;
        line-height: 33px;
        color: #fff;
        text-align: center;
        font-size: 14px;
        background-color: #25bb9b;
      }
    }
  }
  .main-notice {
    margin: 40px auto;
    width: 550px;
    padding-bottom: 40px;
    h4 {
      text-align: center;
      position: relative;
      margin-bottom: 20px;
      &::before {
        content: "";
        width: 100%;
        height: 1px;
        background: #ddd;
        position: absolute;
        left: 0;
        top: 10px;
      }
      span {
        z-index: 2;
        background: #fff;
        padding: 0 30px;
        position: relative;
      }
    }
    ol {
      background: #f3f3f3;
      color: #888;
      padding: 20px 20px 20px 40px;
      list-style: decimal;
      text-align: left;
      li {
        line-height: 2.2;
        font-size: 12px;
      }
    }
  }
}
//
.header-main {
  background: #f0f0f0;
  color: #666;
  line-height: 2;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
  width: 96%;
}
::v-deep .paper-dialog {
  margin-top: 200px !important;
  .el-select {
    width: 100%;
  }
}
.footer-button {
  button {
    width: 100px;
    color: #fff;
    background-color: #25bb9b;
  }
}
</style>
