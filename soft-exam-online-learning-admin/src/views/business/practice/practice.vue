<template>
  <div class="markTestPaper testPaper">
    <div class="paper-main">
      <!-- 考试信息 -->
      <div class="title">
        <h3 class="testName">{{ paperForm.name }}-{{ paperForm.time }}</h3>
        <ul>
          <li class="test-info">试卷Id编号: {{ paperForm.id }}</li>
          <li class="test-info">出卷者: {{ paperForm.user }}</li>
          <li class="test-info">答题时间: {{ paperForm.suggestTime }} 分钟</li>
          <li class="test-info">
            题目数量: 共 {{ paperForm.questionCount }} 道
          </li>
          <li class="test-info">总分: {{ paperForm.score }} 分</li>
        </ul>
      </div>
      <div class="test-content">
        <!-- 题目内容 -->
        <div class="topics">
          <div
            class="topic"
            v-for="(practice, index) in practiceForm"
            :key="index"
          >
            <div class="topicsType" v-if="practice.data.length != 0">
              <h4>{{ bigQuestionName_mixin(practice.type, index) }}</h4>
              <!-- 题目类型名称 -->
              <ul>
                <li
                  class="topic-content"
                  v-for="(t, index) in practice.data"
                  :key="index"
                >
                  <div class="_location"></div>
                  <!-- 题目 -->
                  <div class="left">
                    <div class="question">
                      <span class="question_number"
                        >{{ topicNavIndex_mixin(practice.type, index) }}、</span
                      >
                      <span v-html="t.name"></span>
                      <span class="score">({{ t.score }}分)</span>
                    </div>

                    <!-- 单选题 -->
                    <div class="radio" v-if="t.typeId == 34">
                      <el-radio
                        v-for="(c, cIndex) in t.choice"
                        :key="cIndex"
                        :label="c.select"
                        :disabled="isRead"
                        v-model="t.correctAnswer"
                        :class="
                          c.select == t.correctAnswer ? 'correct' : 'error'
                        "
                      >
                        {{ String.fromCharCode(65 + cIndex) }}、{{ c.value }}
                      </el-radio>
                    </div>

                    <!-- 判断题 -->
                    <div class="TrueOrFalse" v-if="t.typeId == 4">
                      <el-radio
                        v-model="t.userAnswer"
                        label="true"
                        :class="'true' == t.correctAnswer ? 'correct' : 'error'"
                        :disabled="isRead"
                        >正确</el-radio
                      >
                      <el-radio
                        v-model="t.userAnswer"
                        label="false"
                        :class="
                          'false' == t.correctAnswer ? 'correct' : 'error'
                        "
                        :disabled="isRead"
                        >错误</el-radio
                      >
                    </div>

                    <!-- 填空题 -->
                    <div class="fillInBlank" v-if="t.typeId == 33">
                      <div v-for="(q, index) in t.correctAnswer" :key="index">
                        <el-input
                          type="textarea"
                          autosize
                          placeholder="(学生没有回答)"
                          :disabled="isRead"
                          v-model="t.userAnswer[index]"
                        >
                        </el-input>
                      </div>
                    </div>

                    <!-- 简答题 -->
                    <div class="text" v-if="t.typeId === 33">
                      <el-input
                        type="textarea"
                        v-model="t.userAnswer"
                        :autosize="{ minRows: 3, maxRows: 10 }"
                        placeholder="(学生没有回答)"
                        :disabled="isRead"
                      >
                      </el-input>
                    </div>

                    <div v-if="t.typeId === 34" class="correctAnswer">
                      参考答案: {{ t.correctAnswer }}
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
          <div class="topic-nav-describe">
            <!-- <span class="topic-nav-but correct"> </span> 正确
            <span class="space"></span>
            <span class="topic-nav-but error"> </span> 错误 -->
            <el-button
              size="small"
              type="primary"
              icon="el-icon-download"
              @click="exportWord"
              >导出试卷到word</el-button
            >
          </div>

          <div
            v-for="(topics, Topics_index) in practiceForm"
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
import Cookies from "js-cookie";
import paperMixin from "@/utils/paperMixin.js";
import { getListChoice } from "@/api/business/question/choice";
import { getListAnswer } from "@/api/business/question/answer";
import { exportPaperWord } from "@/api/business/paper";
export default {
  name: "practice",
  mixins: [paperMixin],
  data() {
    return {
      // 接口访问数目
      apiNum: 0,
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
      // 当前题目
      currentPractice: {},
      // 当前题目数量
      currentSum: 0,
      // 当前题目下标
      currentPracticeIndex: 0,
      // 查询试题
      queryMap: {
        pageNum: 1,
        pageSize: 100,
        qPaper: null,
      },
      //按题目类型分类好的题目数据
      //试卷数据 题目类型==>  0:单选题   1:简答题
      practiceForm: [
        { type: 0, data: [] },
        { type: 1, data: [] },
      ],
      isRead: true, //是否为只读模式
      //侧导航栏是否悬浮
      isFixed: false,
      topic_nav_style: "top:0px",
    };
  },

  async created() {
    let data = this.paperForm;
    console.log(data);
    this.year = data.time;
    this.queryMap.paperId = data.id;
    await this.findPracticeList();
    await this.getQuestionList();
  },
  mounted() {
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
        let data = await exportPaperWord({ id: this.queryMap.paperId });
        let blob = new Blob([data], {
          type: "application/msword;charset=utf-8",
        });
        let objectUrl = URL.createObjectURL(blob);
        window.location.href = objectUrl;
      }
    },
    // 根据试卷查询题目
    async findPracticeList() {
      let res = await getListChoice(this.queryMap);
      let { code, data, msg } = res;
      if (code === 200) {
        let { rows } = data;
        if (rows.length > 0) {
          rows.map((item) => {
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
          });
          this.apiNum += 1;
          this.practiceChoiceData = rows;
          this.handlePracticeData(0, this.practiceChoiceData);
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
        this.apiNum += 1;
        this.practiceAnswerData = rows;
        this.handlePracticeData(1, this.practiceAnswerData);
      } else {
        this.msgError("获取试题列表失败:" + msg);
      }
    },
    // 处理数据
    handlePracticeData(val, data) {
      let choiceData = [];
      let answerData = [];
      if (val === 0) {
        choiceData = data;
        this.practiceForm.push({
          type: 0,
          data: choiceData,
        });
      } else {
        answerData = data;
        this.practiceForm.push({
          type: 1,
          data: answerData,
        });
      }
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
      document.getElementsByClassName("_location")[i - 1].scrollIntoView(true);
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
          is = true;
        }
      });
      if (is) {
        return "correct";
      } else {
        return "error";
      }
    },

    backTop_mixin() {
      document.getElementsByClassName("paper-main")[0].scrollIntoView(true);
    },
  },
  watch: {
    apiNum: {
      handler(val) {
        if (val === 2) {
          let flag =
            this.practiceChoiceData.length > 0 ||
            this.practiceAnswerData.length > 0
              ? true
              : false;
          if (!flag) {
            this.$message({
              message: "该试卷试题不存在！返回试卷列表",
              type: "warning",
              duration: 2000,
            });
            this.$router.back();
          }
        }
      },
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

  .testName .el-input__inner {
    margin: 0 0 20px 40px;
    border: none;
    background: rgba(255, 255, 255, 0);
    font-size: 20px;
    color: #333;
    font-weight: bold;
    text-decoration: underline;
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
