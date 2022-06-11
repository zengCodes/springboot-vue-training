<template>
  <div id="exam">
    <div class="exam-container">
      <el-header class="exam-header">
        <div class="header-title">
          <span>{{ year }}{{ time }}-{{ currentPaperData.name }}</span>
        </div>
      </el-header>
      <!-- 单选题 -->
      <div class="question">
        <div class="main-header">
          <div class="question-count">
            <span>{{ currentPracticeIndex + 1 }}/{{ currentSum }}</span>
          </div>
          <div class="question-icon">
            <i class="el-icon-question"></i>
          </div>
          <div class="question-type">[单选题]</div>
        </div>
        <div class="question-item">
          <div class="item-title" v-html="currentPractice.name"></div>
          <div class="choice-item">
            <el-radio-group
              v-model="responsePractice.userResponse"
              @change="changeResponse"
            >
              <div
                class="item"
                v-for="optionItem in currentRadioOption"
                :key="optionItem.option"
              >
                <el-radio :label="optionItem.label">{{
                  optionItem.option
                }}</el-radio>
              </div>
            </el-radio-group>
          </div>
          <div class="answer-tip" v-show="isAnswer">
            <el-tag type="success" v-if="isCurrentCorrect"
              >恭喜你！答对了</el-tag
            >
            <el-tag type="danger" v-else-if="!isCurrentCorrect"
              >很遗憾！答错了，正确答案是：{{
                currentPractice.correctAnswer
              }}</el-tag
            >
          </div>
          <div class="question-operation">
            <span class="collect" @click="questionCollect">
              <i class="el-icon-star-on"></i>
              收藏本题
            </span>
            <span class="note" @click="questionNote">
              <i class="el-icon-notebook-2"></i>
              笔记
            </span>
            <div class="btn">
              <el-button
                type="primary"
                class="next-btn"
                @click="nextPro"
                v-if="!isLastQuestion"
                >下一题</el-button
              >
            </div>
          </div>
          <div class="question-page">
            <ul class="page-item">
              <li
                :class="item - 1 == currentPracticeIndex ? 'activite-item' : ''"
                @click="choiceItem(item)"
                v-for="(item, indexID) in numberArray"
                :key="indexID"
              >
                <el-button type="primary">{{ item }}</el-button>
              </li>
              <li @click="nextPage" v-if="!isLastQuestion">
                <el-button type="primary">>>></el-button>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <!-- 简答题 -->
    </div>
  </div>
</template>

<script>
import { getPaper } from "@/api/business/paper";
import { getListChoice } from "@/api/business/question/choice";
import { pagination, generateArray } from "@/utils/zeng";

export default {
  data() {
    return {
      // 当前试卷信息
      currentPaperData: {
        name: null,
      },
      // 当前课程
      course: "",
      // 当前年份
      year: "",
      // 开始练习
      beginPractice: false,
      //   选择题答案选项
      choice: "",
      //   题目总数
      total: 0,
      choiceIndex: 0,
      // 选择题数据
      practiceChoiceData: [],
      // 当前页数选择题数据
      currentPracticeChoiceData: [],
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
        paper: null,
      },
      // 答题信息
      responsePractice: {
        type: null,
        id: null,
        paper: null,
        userResponse: null,
        correctAnswer: null,
      },
      responsePracticeList: [],
      // 是否作答
      isAnswer: false,
      // 是否答对
      isCurrentCorrect: false,
      pageSize: 5,
      pageNum: 1,
      numberArray: [],
    };
  },
  created() {
    this.course = this.$route.query.course;
    this.year = this.$route.query.year;
    this.time = this.$route.query.time;
    this.queryMap.paper = this.$route.query.id;
    this.getPaperData(this.queryMap.paper);
    this.findPracticeList();
  },
  mounted() {},
  methods: {
    // 答题触发
    changeResponse(val) {
      // console.log(this.currentPractice.qcorrectAnswer, val);
      this.isAnswer = true;
      if (this.currentPractice.correctAnswer === val) {
        this.isCurrentCorrect = true;
      } else {
        this.isCurrentCorrect = false;
      }
    },
    // 根据试卷查询题目
    async findPracticeList() {
      let res = await getListChoice(this.queryMap);
      let { code, msg, data } = res;
      if (code === 200) {
        let { rows } = data;
        this.practiceChoiceData = rows;
        // 初始化试题
        console.log(this.practiceChoiceData);
        // 先把所有试题添加进去
        this.practiceChoiceData.forEach((element) => {
          let res = {
            type: element.typeId,
            qid: element.id,
            paper: element.paper,
            correctAnswer: element.correctAnswer,
            userResponse: null,
          };
          this.responsePracticeList.push(res);
        });
        this.currentPracticeChoiceData = [
          ...pagination(this.pageNum, this.pageSize, this.practiceChoiceData),
        ];
        this.numberArray = generateArray(
          5 * (this.pageNum - 1) + 1,
          5 * this.pageNum
        );
        console.log(this.numberArray, this.currentPracticeChoiceData);
        this.currentPracticeIndex = this.numberArray[0] - 1;
        if (this.numberArray.length != this.currentPracticeChoiceData.length) {
          this.numberArray = this.numberArray.splice(
            0,
            this.currentPracticeChoiceData.length
          );
        }
        this.currentPractice = rows[this.currentPracticeIndex];
        this.currentSum = rows.length;
      } else {
        this.msgInfo("获取试卷题目失败:" + msg);
      }
    },
    // 查询试卷详细信息
    async getPaperData(id) {
      let res = await getPaper(id);
      let { code, data } = res;
      if (code === 200) {
        this.currentPaperData = data;
      }
    },
    //   切换时间
    switchPlay() {
      this.beginPractice = !this.beginPractice;
    },
    questionCollect() {
      console.log("收藏");
    },
    questionNote() {
      console.log("笔记");
    },
    nextPro() {
      // console.log('下一题')
      console.log(this.currentSum, this.currentPracticeIndex);
      this.responsePractice.userResponse = null;
      if (this.currentSum > this.currentPracticeIndex + 1) {
        this.currentPracticeIndex++;
        this.currentPractice =
          this.practiceChoiceData[this.currentPracticeIndex];
      }
      if (
        this.currentPracticeIndex >
        this.numberArray[this.numberArray.length - 1]
      ) {
        this.nextPage();
      }
      // console.log("当前试题---", this.responsePracticeList);
    },
    choiceItem(index) {
      // console.log("选择页数", this.currentPracticeIndex, index);
      // 去除提示
      this.isAnswer = false;
      this.responsePractice.userResponse = null;
      this.currentPracticeIndex = index - 1;
      this.currentPractice = this.practiceChoiceData[this.currentPracticeIndex];
    },
    nextPage() {
      if (
        this.numberArray[this.numberArray.length - 1] <=
        this.practiceChoiceData.length
      ) {
        this.pageNum++;
        this.findPracticeList();
      }
    },
  },
  computed: {
    // 是否最后一道题
    isLastQuestion() {
      return (
        this.numberArray[this.numberArray.length - 1] ==
        this.practiceChoiceData.length
      );
    },
    currentRadioOption() {
      return [
        {
          isCorrect: false,
          label: "A",
          option: this.currentPractice.selectA,
        },
        {
          isCorrect: false,
          label: "B",
          option: this.currentPractice.selectB,
        },
        {
          isCorrect: false,
          label: "C",
          option: this.currentPractice.selectC,
        },
        { isCorrect: false, label: "D", option: this.currentPractice.selectD },
      ];
    },
  },
};
</script>

<style lang="scss" scoped>
.exam-container {
  width: 70%;
  margin: 0px auto;
  border: 1px solid #e0e0e0;
  border-radius: 3px;
  background-color: #fff;
}
.el-header,
.el-footer {
  text-align: center;
  line-height: 60px;
}

.exam-header {
  position: relative;
  border-bottom: 1px solid #d4d4d4;
  // 头部标题
  .header-title {
    position: absolute;
    top: 6px;
    left: 50px;
    font-size: 20px;
  }
  .header-time {
    position: absolute;
    top: 6px;
    right: 50px;
    font-size: 25px;
    cursor: pointer;
    .play-btn {
    }
    .play-time {
      margin: 0px 0px 0px 10px;
    }
  }
}
::v-deep .question {
  width: 100%;
  .main-header {
    width: 100%;
    height: 30px;
    font-size: 18px;
    margin-bottom: 20px;
    margin-top: 20px;
    margin-left: 20px;
    // border: 1px solid black;
    .question-count {
      background: #e5e5e5;
      float: left;
      padding: 0 5px;
      margin: 8px 0px 0px 10px;
      border-radius: 5px;
    }
    .question-type {
      float: left;
      margin: 3px 0px 0px 10px;
    }
    .question-icon {
      float: left;
      font-size: 25px;
      color: #ff652f;
      margin: 5px 10px 0px 15px;
    }
  }
  .question-item {
    width: 100%;
    height: 450px;
    // border: 1px solid black;
    position: relative;
    .item-title {
      width: 86%;
      text-align: left;
      margin: 20px auto;
      span {
        font-size: 17px !important;
      }
    }
    // 选择题样式
    .choice-item {
      // position: absolute;
      // top: 45px;
      // left: 50%;
      // transform: translateX(-50%);
      width: 95%;
      height: 100%;
      margin: 20px auto;
      .el-radio-group {
        width: 100%;
        height: 100%;
      }
      .item {
        padding: 10px;
        margin: 0px 20px 20px 20px;
        border: 1px solid #d4d4d4;
        border-radius: 2px;
        background: #fff;
        display: block;
        word-break: break-all;
        cursor: pointer;
        text-align: left;
        .el-radio__label {
          font-size: 17px;
        }
        .el-radio {
          margin-left: 5%;
        }
      }
    }
    // 答题提示
    .answer-tip {
      width: 200px;
      height: 50px;
      position: absolute;
      bottom: 110px;
      left: 42px;
      .el-tag {
        width: 200px;
        height: 30px;
      }
    }
    // 操作样式
    .question-operation {
      width: 100%;
      height: 50px;
      position: absolute;
      bottom: 75px;
      left: 0px;
      .collect {
        float: left;
        margin: 10px 0px 0px 55px;
        cursor: pointer;
      }
      .note {
        float: left;
        margin: 10px 0px 0px 50px;
        cursor: pointer;
      }
      .btn {
        display: flex;
        margin: 0px 0px 0px 0px;
        justify-content: flex-end; // 靠右对齐
        flex-wrap: wrap; // 自动换行
      }
      .next-btn {
        flex: 0 0 12%;
        width: 100px;
        margin: 0px 40px 0px 0px;
        background-color: #ff652f;
        border: 0;
      }
    }
  }
  //   分页
  .question-page {
    width: 100%;
    height: 50px;
    // border: 1px solid black;
    position: absolute;
    bottom: 20px;
    left: 0px;
    .page-item {
      margin-left: 30px;
      li {
        float: left;
        margin: 0px 0px 0px 20px;
        .el-button {
          background-color: #fff;
          color: #409eff;
          width: 65px;
        }
      }
      .activite-item .el-button {
        color: #fff !important;
        background-color: #409eff !important;
      }
    }
  }
}
</style>
