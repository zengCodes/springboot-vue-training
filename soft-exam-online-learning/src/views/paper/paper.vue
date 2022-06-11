<template>
  <div class="markTestPaper testPaper">
    <div class="paper-main">
      <!-- 考试信息 -->
      <div class="title">
        <h3 class="testName">{{ paperForm.name }}-{{ paperForm.time }}</h3>
        <ul>
          <li class="test-info">试卷Id: {{ paperForm.id }}</li>
          <li class="test-info">出卷者: {{ paperForm.user }}</li>
          <li class="test-info">答题时间: {{ paperForm.suggestTime }} 分钟</li>
          <li class="test-info">
            题目数量: 共 {{ paperForm.questionCount }} 道
          </li>
          <li class="test-info">总分: {{ paperForm.score }} 分</li>
          <li class="test-info">答题人: {{ userInfo.name }}</li>
        </ul>
        <div class="test-time">
          <p><i class="el-icon-time"></i> {{ `${hour}: ${min}: ${sec}` }}</p>
        </div>
      </div>
      <div class="test-content">
        <!-- 题目内容 -->
        <div class="topics">
          <div
            class="topic"
            v-for="(practice, index) in practiceForm"
            :key="practice.id"
          >
            <div class="topicsType" v-if="practice.data.length != 0">
              <h4>{{ bigQuestionName_mixin(practice.type, index) }}</h4>
              <!-- 题目类型名称 -->
              <ul>
                <li
                  class="topic-content"
                  v-for="(t, pIndex) in practice.data"
                  :key="pIndex"
                >
                  <div class="_location"></div>
                  <!-- 题目 -->
                  <div class="left">
                    <div class="question">
                      <span class="question_nunber"
                        >{{
                          topicNavIndex_mixin(practice.type, pIndex)
                        }}、</span
                      >
                      <span v-html="t.name"></span>
                      <span class="score">({{ t.score }}分)</span>
                    </div>

                    <!-- 单选题 -->
                    <div class="radio" v-if="t.type == 34">
                      <el-radio
                        v-for="(c, cIndex) in t.choice"
                        :key="cIndex"
                        :label="c.select"
                        v-model="t.userResponse"
                      >
                        {{ String.fromCharCode(65 + cIndex) }}、{{ c.value }}
                      </el-radio>
                    </div>
                    <!-- 简答题 -->
                    <div class="text" v-if="t.type == 33">
                      <el-input
                        type="textarea"
                        v-model="t.userAnswer"
                        :autosize="{ minRows: 3, maxRows: 10 }"
                        placeholder="(学生没有回答)"
                        :disabled="isRead"
                      >
                      </el-input>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 题目导航 -->
        <div
          class="topic-nav"
          :class="isFixed ? 'isFixed' : ''"
          :style="topic_nav_style"
        >
          <div
            v-for="(topics, Topics_index) in practiceForm"
            :key="Topics_index"
          >
            <div class="topic-nav-item" v-if="topics.data.length != 0">
              <div class="submit-btn">
                <el-button type="primary" size="small" @click="submitPractice">
                  交 卷
                </el-button>
              </div>
              <div class="nav-title">
                {{ topicTypeName_mixin(topics.type) }}
              </div>

              <span
                class="topic-nav-button"
                @click="topicNav(topics.type, index)"
                v-for="(item, index) in topics.data"
                :key="index"
                :class="isMarkTopic(item)"
              >
                {{ topicNavIndex_mixin(topics.type, index) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="back-top" @click="backTop_mixin">
        <i class="el-icon-arrow-up"></i>
      </div>
    </div>
  </div>
</template>

<script>
import paperMixin from "@/utils/paperMixin.js";
import Cookies from "js-cookie";
import { getListChoice } from "@/api/business/question/choice";
import { getListAnswer } from "@/api/business/question/answer";
import { exportPaperWord } from "@/api/business/paper";
import { sendPracticeList } from "@/api/business/practice";
import { mapState } from "vuex";

export default {
  name: "practice",
  mixins: [paperMixin],
  data() {
    return {
      // 当前年份
      year: null,
      // 开始练习
      beginPractice: false,
      //   选择题答案选项
      choice: null,
      //   题目总数
      total: 0,
      choiceIndex: 0,
      // 选择题数据
      practiceChoiceData: [],
      // 简答题数据
      practiceAnswerData: [],
      // 当前题目数量
      currentSum: 0,
      // 当前题目下标
      currentPracticeIndex: 0,
      // 查询试题
      queryMap: {
        pageNum: 1,
        pageSize: 100,
        paperId: null,
      },
      //按题目类型分类好的题目数据
      //题目类型==>  0:单选题  1:多选题  2:判断题  3:填空题  4:简答题
      sortedTopics: [
        { topic_type: 0, topic_content: [] },
        { topic_type: 1, topic_content: [] },
        { topic_type: 2, topic_content: [] },
        { topic_type: 3, topic_content: [] },
        { topic_type: 4, topic_content: [] },
      ],
      //试卷数据
      practiceForm: [],
      //侧导航栏是否悬浮
      isFixed: false,
      topic_nav_style: "top:0px",
      // 答题信息
      responsePractice: {
        type: null,
        id: null,
        paper: null,
        userResponse: null,
        correctAnswer: null,
      },
      testId: undefined,
      responsePracticeList: [],
      isShowTime: true,
      timer: "",
      hour: 2,
      min: 30,
      sec: 0,
    };
  },

  created() {
    let data = this.paperForm;
    this.year = data.time;
    this.queryMap.paperId = data.id;
    this.testId = this.$route.query.id;
    this.findPracticeList();
  },
  mounted() {
    console.log(this.userInfo);
    this.begin();
    // 监听滚动事件，然后用handleScroll这个方法进行相应的处理
    window.addEventListener("scroll", this.handleScroll);
  },
  computed: {
    paperForm() {
      let paper = this.$store.state.paper.paperData;
      if (paper != null) {
        return paper;
      } else {
        return JSON.parse(Cookies.get("paper"));
      }
    },
    ...mapState({
      userInfo: (state) => state.user,
    }),
  },
  methods: {
    // 导出试卷到word
    async exportWord() {
      const res = await this.$confirm("是否导出试卷到word文档?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).catch(() => {
        this.$message({
          type: "info",
          message: "已取消导出",
        });
      });
      if (res === "confirm") {
        let data = await exportPaperWord({ id: this.queryMap.paper });
        let blob = new Blob([data], {
          type: "application/msword;charset=utf-8",
        });
        let objectUrl = URL.createObjectURL(blob);
        window.location.houref = objectUrl;
      }
    },
    // 根据试卷查询题目
    async findPracticeList() {
      let res = await getListChoice(this.queryMap);
      let { code, data, msg } = res;
      if (code === 200) {
        let { rows } = data;
        if (rows.length > 0) {
          this.practiceChoiceData = rows.map((item) => {
            let choice = [];
            choice.push(
              {
                select: "A",
                value: item.selectA,
              },
              {
                select: "B",
                value: item.selectB,
              },
              {
                select: "C",
                value: item.selectC,
              },
              {
                select: "D",
                value: item.selectD,
              }
            );
            item["choice"] = choice;
            return {
              type: item.typeId,
              id: item.id,
              testId: this.testId,
              correctAnswer: item.correctAnswer,
              choice: item.choice,
              score: item.score,
              name: item.name,
              userResponse: null,
            };
          });
          this.handlePracticeData(1, this.practiceChoiceData);
        } else {
          this.$message({
            message: "该试卷试题不存在！返回试卷列表",
            type: "warning",
            duration: 2000,
          });
          this.$router.back();
        }
      } else {
        this.msgError("获取试卷题目失败:" + msg);
      }
    },
    /**
     * 加载试题列表
     */
    async getQuestionList() {
      let res = await getListAnswer(this.queryMap);
      let { code, data, msg } = res;
      if (code === 200) {
        let { rows } = data;
        this.practiceAnswerData = rows;
        this.handlePracticeData(2, this.practiceAnswerData);
      } else {
        this.msgError("获取试题列表失败:" + msg);
      }
    },
    // 处理数据
    handlePracticeData(val, data) {
      // let arr = []
      let choiceData = [];
      let answerData = [];
      if (val === 1) {
        choiceData = data;
        this.practiceForm.push({
          type: 1,
          data: choiceData,
        });
      } else {
        answerData = data;
        this.practiceForm.push({
          type: 2,
          data: answerData,
        });
      }
      console.log(this.practiceForm);
    },
    //滚动事件
    handleScroll() {
      let scrollTop =
        window.pageYOffset ||
        document.documentElement.scrollTop ||
        document.body.scrollTop; // 滚动条偏移量
      if (scrollTop > 154) {
        this.topic_nav_style = "top:" + (scrollTop + 62) + "px";
        this.isFixed = true;
      } else {
        this.isFixed = false;
      }
    },

    //点击题号定位到题目位置
    topicNav(type, index) {
      let i = this.topicNavIndex_mixin(type, index);
      let dom = document.getElementsByClassName("_location");
      dom[i - 1].scrollIntoView({ behavior: "smooth", block: "start" });
    },

    backTop_mixin() {
      document.getElementsByClassName("paper-main")[0].scrollIntoView(true);
    },

    //判断题目是否批改与正确
    isMarkTopic(topic) {
      //单选题/多选题/判断题/
      if (
        topic.topicType == 0 ||
        topic.topicType == 2 ||
        topic.topicType == 1
      ) {
        if (topic.score == topic.userScore) {
          return "correct";
        } else {
          return "error";
        }
      }

      //如果为自动评分
      if (this.practiceForm.autoMack == 1) {
        if (topic.score == topic.userScore) {
          return "correct";
        } else {
          return "error";
        }
      }
    },

    isCheckboxCorrect(topic, val) {
      let is = false;
      topic.correctAnswer.forEach((item) => {
        if (item == val) {
          console.log(item);
          is = true;
        }
      });
      if (is) {
        return "correct";
      } else {
        return "error";
      }
    },

    //正确选项参考答案选项字母化
    correctOptions(topic) {
      var correctOptions = "";
      switch (topic.topicType) {
        case 0:
          for (let i = 0; i < topic.choice.length; i++) {
            if (topic.choice[i] == topic.correctAnswer[0]) {
              correctOptions = String.fromCharCode(65 + i);
              break;
            }
          }
          break;

        case 1:
          for (let i = 0; i < topic.choice.length; i++) {
            for (let j = 0; j < topic.correctAnswer.length; j++) {
              if (topic.choice[i] == topic.correctAnswer[j]) {
                correctOptions += String.fromCharCode(65 + i);
                continue;
              }
            }
          }
          break;

        case 2:
          if (topic.correctAnswer[0] == "true") {
            correctOptions = "正确";
          } else {
            correctOptions = "错误";
          }
          break;
      }

      return correctOptions;
    },

    // 交卷
    async submitPractice() {
      let flag = false;
      this.practiceForm[0].data.forEach((item) => {
        if (item.userResponse == null) {
          flag = false;
        } else {
          flag = true;
        }
      });
      if (flag) {
        this.openTimeInfo();
      } else {
        this.$confirm("检测到当前还有题目未答完，是否继续交卷？", "确认交卷", {
          distinguishCancelAndClose: true,
          confirmButtonText: "确认交卷",
          cancelButtonText: "取消交卷",
        })
          .then(() => {
            this.send(this.testId, this.practiceForm);
            setTimeout(() => {
              this.$router.push({
                path: "record",
              });
            }, 200);
          })
          .catch((action) => {
            console.log(action);
          });
      }
    },
    async send(id, testDTO) {
      let res = await sendPracticeList(id, testDTO);
      // 弹出本次考试结果，记录
      let { code, data } = res;
      if (code === 200) {
        this.msgSuccess("本次考试分数为：" + data + "分！");
      }
    },
    begin() {
      // 点击按钮后开始计算指定长度的时间
      this.time = Date.parse(new Date()) + 2.5 * 60 * 60 * 1000;
      // 开始执行倒计时
      this.countdown();
      // 更换按钮，根据情况选择v-if或v-show
      this.isShowTime = false;
      this.$message({
        type: "success",
        message: "开始答题",
      });
    },
    countdown() {
      const end = this.time; // 定义结束时间
      const now = Date.parse(new Date()); // 获取本地时间
      const msec = end - now; // 定义总共所需的时间
      // 将时间戳进行格式化
      let hour = parseInt((msec / 1000 / 60 / 60) % 24);
      let min = parseInt((msec / 1000 / 60) % 60);
      let sec = parseInt((msec / 1000) % 60);
      // 倒计时结束时的操作
      const that = this;
      if (this.hour == 0 && this.min == 0 && this.sec == 0) {
        console.log("时间已经结束，答题完毕");
        this.hour = 2;
        this.min = 30;
        this.sec = 0;
      } else {
        // 如时间未归零则继续在一秒后执行
        this.hour = hour > 9 ? hour : "0" + hour;
        this.min = min > 9 ? min : "0" + min;
        this.sec = sec > 9 ? sec : "0" + sec;
        setTimeout(that.countdown, 1000);
      }
    },
    openTimeInfo() {
      this.$confirm("即将结束答题, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then((action) => {
          // eleUI的确定结束回调函数方法
          if (action === "confirm") {
            this.hour = 0;
            this.min = 0;
            this.sec = 0;
            this.isShowTime = true;
            this.send(this.testId, this.practiceForm);
            setTimeout(() => {
              this.$router.push({
                path: "record",
              });
            }, 200);
          }
          this.$message({
            type: "success",
            message: "交卷成功!",
          });
        })
        .catch(() => {
          // 点击取消后
          this.$message({
            type: "info",
            message: "已取消交卷",
          });
        });
    },
  },
  destroyed() {},
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/paper.scss";
.paper-main {
  position: relative;
  width: 90%;
  margin: 0 auto;
  padding: 0 10px;
  box-sizing: border-box;
  padding-bottom: 40px;
}

.testPaper .title,
.topics,
.topic-nav {
  background: #fafafa;
  border-radius: 2px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.testPaper {
  .test-content {
  }

  .topics {
    float: left;
    width: 70%;
    box-sizing: border-box;
    padding: 16px;
    margin-bottom: 30px;
  }
}

.testPaper .title {
  // height: 128px;
  padding: 10px 30px 10px 30px;
  margin-bottom: 10px;

  .test-time {
    float: right;
    margin: -40px 115px 0px 0px;
    color: #f72a3a;
    font-weight: bold;
    font-size: 25px;
  }
  .testName {
    margin: 20px 0px;
    .el-input__inner {
      margin: 0 0 20px 40px;
      border: none;
      background: rgba(255, 255, 255, 0);
      font-size: 20px;
      color: #333;
      font-weight: bold;
      text-decoration: underline;
    }
  }

  ul {
    margin-bottom: 10px;
  }

  ul::after {
    content: "";
    display: block;
    clear: both;
  }

  .test-info {
    float: left;
    margin-right: 40px;

    i {
      color: #333;
      position: relative;
      top: 2px;
      left: 2px;
    }
  }

  .test-info:last-child {
    margin: 0;
  }

  .user-info {
    float: left;
    margin-top: 10px;
    margin-right: 40px;
  }

  .el-input-number {
    width: 72px;
  }

  .el-input-number.is-controls-right .el-input__inner {
    padding-left: 0px;
    padding-right: 28px;
  }
}

.testPaper .title.fixed {
  position: fixed;
  z-index: 999;
  top: 0;
  width: 1180px;
  box-sizing: border-box;
  padding: 14px 30px 0px 30px;
}

.topics .topic {
  .topicsType {
    margin-bottom: 20px;
  }
  .bigQuestionName {
    position: relative;
    display: flex;
    justify-content: space-between;
  }

  .bigQuestionName h4 {
    margin: 20px 0;
  }

  .bigQuestionName .allScore {
    margin: 20px 0;
  }

  .bigQuestionName .allScore button {
    height: 28px;
  }

  .bigQuestionName .allScore button.active {
    background: #666;
    color: #fff;
  }

  .bigQuestionName .allScore .el-input {
    position: absolute;
    top: -30px;
    right: 0;
    width: 120px;
  }

  .bigQuestionName .allScore .el-input::before {
    content: "";
    display: inline-block;
    position: absolute;
    bottom: -4px;
    left: 7px;
    transform: rotate(-45deg);
    width: 8px;
    height: 8px;
    border: 1px solid transparent;
    border-left: 1px solid #dcdfe6;
    border-bottom: 1px solid #dcdfe6;
  }

  .bigQuestionName .allScore .el-input::after {
    content: "";
    display: inline-block;
    position: absolute;
    bottom: -3px;
    left: 8px;
    transform: rotate(-45deg);
    border: 4px solid transparent;
    border-left: 4px solid #fff;
    border-bottom: 4px solid #fff;
  }

  .topic-content {
    position: relative;
    padding: 10px 8px;
    border-radius: 8px;
  }

  .topic-content.isEdit {
    // background: #f5f5f5;
  }

  .topic-content ._location {
    position: absolute;
    top: -50px;
    width: 10px;
    height: 10px;
  }

  .required-symbol {
    color: red;
  }

  ::v-deep .question {
    margin-bottom: 20px;
    font-size: 16px;
    line-height: 26px;
    color: #333;
    span {
      font-size: 15px !important;
    }
  }

  .question .el-textarea {
    width: 730px;
  }

  .el-radio,
  .el-checkbox {
    width: 600px;
    margin-bottom: 10px;
  }

  .userAnswer .el-textarea {
    width: 500px;
  }

  .userAnswer .topicNavIndex {
    display: inline-block;
    width: 30px;
  }

  .score {
    float: right;
  }

  .addRadios {
    margin: 4px 0 0 30px;
  }

  .delRadios.el-button {
    position: relative;
    top: -2px;
    padding: 4px 4px;
    margin-left: 12px;
  }

  .correctAnswer {
    padding-left: 12px;
  }

  .topicScore,
  .difficulty,
  .required {
    display: inline-block;
    box-sizing: border-box;
    width: 33%;
    padding: 0 12px;
    margin-bottom: 10px;
    font-size: 14px;
  }

  .difficulty .el-input {
    width: 120px;
  }

  .analysis,
  .correct_answer {
    padding: 0 10px;
    font-size: 14px;
  }

  .el-radio,
  .el-checkbox {
    display: block;
    margin-left: 20px;
    color: #222;
  }

  .el-radio__label,
  .el-checkbox__label {
    font-size: 16px;
    line-height: 30px;
    word-wrap: break-word;
    white-space: normal;
  }

  .el-checkbox__input {
    float: left;
    margin-top: 8px;
  }

  .fillInBlank .el-textarea {
    width: 50%;
    margin-left: 30px;
    margin-top: 10px;
  }

  .text .el-textarea {
    width: 400px;
    margin: 4px 0 0 30px;
  }

  .text .addRadios {
    margin-bottom: 10px;
  }

  .newTopic {
    margin: 10px 0;
    padding: 10px 0;
    text-align: center;
    color: #64a9e3;
    border: 1px solid #64a9e3;
    cursor: pointer;
  }
}

.testPaper .topic-nav {
  float: right;
  box-sizing: border-box;
  width: 28%;
  min-height: 300px;
  padding: 14px;

  .tool {
    display: flex;
    justify-content: space-between;
    width: 200px;
    height: 30px;
    margin: 0 auto;

    .el-button {
      padding: 6px 10px;
    }
  }

  .topic-nav-describe {
    width: 140px;
    margin: 0 auto;
    font-size: 14px;
  }

  .topic-nav-item {
    margin-bottom: 10px;
    .submit-btn {
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .nav-title {
    width: 50px;
    font-size: 14px;
    margin-left: 10px;
  }

  .topic-nav-button {
    display: inline-block;
    width: 30px;
    height: 30px;
    text-align: center;
    line-height: 30px;
    margin: 10px 6px 0 8px;
    color: #64a9e3;
    border: 1px solid #64a9e3;
    border-radius: 2px;
    cursor: pointer;
    transition: 0.5s;
  }

  .topic-nav-button.hasAnswer {
    background-color: #64a9e3;
    color: #fff;
  }
}

.topic-nav.isFixed {
  position: absolute;
  top: 154px;
  right: 10px;
  // transform: 0.5s;
}

.testPaper .back-top {
  position: fixed;
  bottom: 50px;
  right: 100px;
  background: #fff;
  color: #333;
  font-size: 18px;
  border-radius: 2px;
  padding: 6px 8px;
  cursor: pointer;
}

.el-radio.is-disabled.is-checked
  .el-radio__input.is-disabled
  + span.el-radio__label,
.el-checkbox.is-disabled.is-checked
  .el-checkbox__input.is-disabled
  + span.el-checkbox__label {
  color: #409eff;
}

.topic-nav-button.correct,
.topic-nav-but.correct {
  background: #02c39a !important;
  border: 1px solid #02c39a !important;
  color: #fff !important;
}
.topic-nav-button.error,
.topic-nav-but.error {
  background: #ff6b6b;
  border: 1px solid #ff6b6b !important;
  color: #fff !important;
}

::v-deep .correct .el-radio__label {
  color: #02c39a !important;
}

.testPaper .forbid_copy {
  -moz-user-select: none;
  /*火狐*/
  -webkit-user-select: none;
  /*webkit浏览器*/
  -ms-user-select: none;
  /*IE10*/
  -khtml-user-select: none;
  /*早期浏览器*/
  user-select: none;
}
</style>
