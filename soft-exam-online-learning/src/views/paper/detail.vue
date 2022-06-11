<template>
  <div class="markTestPaper testPaper">
    <div class="paper-main">
      <!-- 考试信息 -->
      <div class="title">
        <h3 class="testName">{{ paperForm.name }}-{{ paperForm.time }}</h3>
        <ul>
          <li class="test-info">试卷Id: {{ paperForm.pid }}</li>
          <li class="test-info">出卷者: {{ paperForm.user }}</li>
          <li class="test-info">答题时间: {{ paperForm.suggestTime }} 分钟</li>
          <li class="test-info">截至时间: {{ paperForm.suggestTime }}</li>
          <li class="test-info">
            题目数量: 共 {{ paperForm.questionCount }} 道
          </li>
          <li class="test-info">总分: {{ paperForm.score }} 分</li>
          <li class="test-info">答题人: {{ userInfo.user.userName }}</li>
          <li class="test-info">考试分数: {{ paperForm.total }}</li>
        </ul>
      </div>
      <div class="test-content">
        <!-- 题目内容 -->
        <div class="topics">
          <div
            class="topic"
            v-for="(practice, index) in practiceList"
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
                      <span class="question_number"
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
                        disabled
                        v-model="t.userResponse"
                        :class="c.select === t.userResponse ? 'current' : ''"
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
                  <div class="right">
                    <div v-if="t.type === 34">
                      <span class="correctAnswer">
                        参考答案: {{ t.correctAnswer }}
                      </span>
                      <span class="userScore"
                        >得分:
                        {{ t.correctAnswer === t.userResponse ? 1 : 0 }}
                        分</span
                      >
                    </div>
                    <div v-else>
                      <div class="userScore">
                        得分:
                        <el-input-number
                          v-model="t.score"
                          controls-position="right"
                          :min="0"
                          :max="t.score"
                        ></el-input-number>
                      </div>
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
            v-for="(topics, Topics_index) in practiceList"
            :key="Topics_index"
          >
            <div class="topic-nav-item" v-if="topics.data.length != 0">
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
import { mapState } from "vuex";
import { getPaper } from "@/api/business/paper";
import { getPracticeList, getTestInfo } from "@/api/business/testRecord";

import { getListChoice } from "@/api/business/question/choice";
import { getListAnswer } from "@/api/business/question/answer";
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
      paperForm: {},
      userPracticeList: [],
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
        testId: null,
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
      practiceList: [],
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
      responsePracticeList: [],
    };
  },
  created() {
    this.queryMap.testId = this.$route.query.id;
    // 查询试卷信息
    this.getRecordData(this.queryMap.testId);
    this.getPracticeData();
  },
  mounted() {
    // 监听滚动事件，然后用handleScroll这个方法进行相应的处理
    window.addEventListener("scroll", this.handleScroll);
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.user,
    }),
  },
  methods: {
    async getPaperInfo(id, total) {
      let res = await getPaper(id);
      let { code, data } = res;
      if (code === 200) {
        this.paperForm = data;
        this.paperForm.total = total;
      }
    },
    // 查询考试题目信息
    async getPracticeData() {
      let res = await getPracticeList({
        userId: this.userInfo.user.userId,
        testId: this.queryMap.testId,
      });
      let { code, data } = res;
      if (code === 200) {
        this.userPracticeList = data;
        this.queryMap.paperId = data[0].paper.id;
        this.findPracticeList();
        this.getQuestionList();
      }
    },
    // 获取考试记录信息
    async getRecordData() {
      let res = await getTestInfo(this.queryMap.testId);
      let { code, data } = res;
      if (code === 200) {
        this.getPaperInfo(data.paperId, data.total);
      }
    },
    // 根据试卷查询题目
    async findPracticeList() {
      let _this = this;
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
              userResponse: _this.findUserResponse(item.typeId, item.id),
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

    // 查询用户答题
    findUserResponse(type, question) {
      let response = "";
      this.userPracticeList.forEach((item) => {
        if (item.questionId === question && item.questionType === type) {
          response = item.userResponse;
        }
      });
      return response;
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
        this.practiceList.push({
          type: 1,
          data: choiceData,
        });
      } else {
        answerData = data;
        this.practiceList.push({
          type: 2,
          data: answerData,
        });
      }
      console.log(this.practiceList);
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
      if (this.practiceList.autoMack == 1) {
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
  },
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
    display: flex;
    ._location {
      position: absolute;
      top: -50px;
      width: 10px;
      height: 10px;
    }
    .left {
      flex: 1;
    }
    .right {
      flex: 1;
    }
  }

  .required-symbol {
    color: red;
  }

  .question {
    margin-bottom: 20px;
    font-size: 16px;
    line-height: 26px;
    color: #333;
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
::v-deep .error .el-radio__label {
  color: #ff6b6b !important;
}

::v-deep .current .el-radio__label {
  color: #64a9e3 !important;
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
