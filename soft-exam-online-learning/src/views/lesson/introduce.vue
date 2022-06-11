<template>
  <div class="app-container">
    <div class="over-scroll">
      <!-- 课程显示 -->
      <el-card class="header-card">
        <el-row :gutter="22" class="header-row">
          <el-col :span="15">
            <div class="left-main">
              <img :src="courseInfo.logo" class="details-img" />
            </div>
          </el-col>
          <el-col :span="5">
            <div class="right-main">
              <span class="main-title">{{ courseInfo.name }}</span>
              <div class="main-price">
                <div class="item">
                  <span class="txt">价格：</span>
                  <span class="num">￥{{ courseInfo.original }}</span>
                </div>
              </div>
              <div class="main-lecture">
                <span class="txt">讲师：</span>
                <span class="txt">{{ courseInfo.nickName }}</span>
              </div>
              <div class="main-buy">
                <span class="txt">购买人数：</span>
                <span class="txt">{{ courseInfo.buyCount || 0 }}</span>
              </div>
              <span class="buy-button" @click="buyLesson">
                {{ courseInfo.isFree === 1 ? "免费观看" : "购买" }}
              </span>
              <div class="main-study">
                <span class="txt">
                  <i class="el-icon-user-solid"></i>
                  {{ courseInfo.studyCount || 0 }}个人学习</span
                >
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <!-- 课程介绍 -->
      <el-card class="center-card">
        <el-row :gutter="15">
          <el-col :span="15">
            <div class="article-status-menu">
              <span
                @click="selectStatus('introduce')"
                :class="isActive('introduce')"
                >课程介绍</span
              >
              <span @click="selectStatus('video')" :class="isActive('video')">
                课程视频
              </span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="left-two-card">
        <el-row :gutter="15">
          <el-col :span="22" v-if="activeStatus == 'introduce'">
            <span class="main-txt" v-html="courseInfo.introduce"></span>
            <p class="main-txt">
              完善的在线教学功能：录播、直播、题库、资源、社区、营销、博客等
              全面的系统技术交付：提供全套教育系统的程序源码、文档、脚本
            </p>
            <p></p>
          </el-col>
          <el-col :span="22" v-if="activeStatus == 'video'">
            <div
              class="link-content"
              v-for="item in lessonChapterList"
              :key="item.id"
              @click="showCurrentChapter(item)"
            >
              <span class="page-num">P{{ item.sort }}</span>
              <span class="part">{{ item.title }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="bottom-card">
        <el-row :gutter="15">
          <el-col :span="15">
            <span class="title">讲师简介</span>
            <div class="introduce">
              <img :src="lecturerInfo.coverImg" alt="" />
              <span class="introduce-title">{{
                lecturerInfo.lecturerName
              }}</span>
              <span
                class="introduce-main"
                v-html="lecturerInfo.introduce"
              ></span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <!-- 课程 -->
    </div>
  </div>
</template>
<script>
import { getLecturerData } from "@/api/business/lesson/lessonLecturer";
import { mapState } from "vuex";

import { getLesson, saveLessonStudy } from "@/api/business/lesson/lesson";
import { getLessonChapter } from "@/api/business/chapter";
import { getOrderStatus } from "@/api/business/order";

export default {
  data() {
    return {
      courseInfo: {
        courseLogo: "",
      },
      activatedIndex: null,
      activeStatus: "introduce",
      lessonChapterList: [],
      lecturerInfo: {},
      // 是否支付
      isUserPayment: false,
    };
  },
  mounted() {
    this.activatedIndex = this.$route.query.id;
    this.getLessonInfo(this.activatedIndex);
    if (this.courseInfo.isFree != 1) {
      // 判断是否支付过订单
      this.isPayment();
    }
  },
  methods: {
    // 获取课程信息
    async getLessonInfo(id) {
      let res = await getLesson(id);
      let { code, data } = res;
      if (code === 200) {
        this.courseInfo = data;
        this.getLessonLecturer(data.userNo);
      }
    },
    // 查询讲师信息
    async getLessonLecturer(id) {
      let res = await getLecturerData(id);
      let { code, data } = res;
      if (code === 200) {
        this.lecturerInfo = data;
      }
    },
    async getLessonChapterData(id) {
      let res = await getLessonChapter({
        lesson: id,
      });
      let { code, data } = res;
      if (code === 200) {
        this.lessonChapterList = data;
      }
    },
    showCurrentChapter(item) {
      if (this.courseInfo.isFree === 1) {
        this.toVideo(item);
      } else {
        if (this.isUserPayment) {
          this.toVideo(item);
        } else {
          this.$confirm(
            "您还未购买此课程，购买后才能观看此付费课程",
            "是否前去购买",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            }
          )
            .then(() => {
              this.buyLesson(item);
            })
            .catch(() => {
              this.$message({
                type: "info",
                message: "已取消购买",
              });
            });
        }
      }
    },
    toVideo(item) {
      let routeData = this.$router.resolve({
        path: "/video",
        query: {
          lesson: this.courseInfo.id,
          id: item.id,
        },
      });
      // 保存学习记录
      this.saveLessonStudyRecord(this.courseInfo.id);
      window.open(routeData.href, "_blank");
    },
    async saveLessonStudyRecord(id) {
      let res = await saveLessonStudy(id);
      console.log(res.msg);
    },
    // 选择状态
    selectStatus(status) {
      switch (status) {
        case "introduce":
          break;
        case "video":
          this.getLessonChapterData(this.activatedIndex);
          break;
        case "secret":
          break;
        case "draft":
          break;
        case "delete":
          break;
      }
      this.activeStatus = status;
    },
    // 是否支付
    async isPayment() {
      getOrderStatus(this.activatedIndex, this.userData.user.userId)
        .then((res) => {
          let { code, data } = res;
          if (code === 200) {
            if (data) {
              this.isUserPayment = true;
            } else {
              this.isUserPayment = false;
            }
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    buyLesson() {
      if (this.courseInfo.isFree === 1) {
        alert("免费课程！无需购买");
      } else {
        if (this.isUserPayment) {
          alert("您已经购买过了，无需购买！");
        } else {
          this.$router.push({
            path: "/order",
            query: {
              lesson: this.courseInfo.id,
            },
          });
        }
      }
    },
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    },
    ...mapState({
      userData: (state) => state.user,
    }),
  },
};
</script>

<style lang="scss" scoped>
.app-container {
  height: 90vh;
  .over-scroll {
    overflow-y: scroll;
    overflow-x: hidden;
    height: 83vh;
  }
}
::v-deep .header-card {
  width: 100%;
  margin: 20px auto;
  border-radius: 10px;
  background-color: rgb(61, 68, 76);
}
.left-main {
  width: 100%;
  height: 100%;
  .details-img {
    width: 65%;
    height: 250px;
    z-index: 1;
    border-radius: 10px;
  }
}
.right-main {
  width: 100%;
  height: 100%;
  position: relative;
  // margin-left: 100px;
  .main-title {
    width: 560px;
    display: inline-block;
    position: absolute;
    top: 0px;
    left: -60px;
    font-size: 25px;
    line-height: 50px;
    height: 50px;
    color: #fff;
    word-break: break-all;
  }
  .main-price {
    position: absolute;
    top: 50px;
    left: 0px;
    height: 80px;
    line-height: 50px;
    .txt {
      padding-bottom: 10px;
      color: #666;
      font-size: 15px;
      margin: 20px 0;
      width: 100%;
    }
    .num {
      font-size: 24px;
      color: #d51423;
      font-weight: 700;
      margin-left: 20px;
    }
  }
  .main-lecture {
    position: absolute;
    top: 100px;
    left: 0px;
    .txt {
      padding-bottom: 10px;
      color: #666;
      font-size: 15px;
      margin: 20px 0;
      width: 100%;
    }
  }
  .main-buy {
    position: absolute;
    top: 150px;
    left: 0px;
    .txt {
      padding-bottom: 10px;
      color: #666;
      font-size: 15px;
      margin: 20px 0;
      width: 100%;
    }
  }
  .buy-button {
    cursor: pointer;
    position: absolute;
    top: 200px;
    left: 0px;
    display: inline-block;
    height: 30px;
    width: 100px;
    line-height: 30px;
    color: #fff;
    font-size: 15px;
    text-align: center;
    background-color: #d51423;
  }
  .main-study {
    position: absolute;
    right: 0px;
    top: 200px;
    color: #666;
    font-size: 15px;
  }
}
//
::v-deep .center-card {
  margin-top: 20px;
  background: #fff;
  border-radius: 8px;
  height: 60px;
  line-height: 40px;
  width: 50%;
  border-radius: 10px;
  float: left;
  margin-left: 200px;
  .article-status-menu {
    font-size: 15px;
    color: #999;
    margin: -10px 0px 0px -100px;
  }
  .article-status-menu span {
    margin-right: 30px;
  }
  .status {
    cursor: pointer;
  }
  .active-status {
    cursor: pointer;
    color: #d51423;
    font-weight: bold;
  }
}
::v-deep .left-two-card {
  margin-top: 20px;
  background: #fff;
  border-radius: 8px;
  height: 400px;
  line-height: 40px;
  width: 50%;
  border-radius: 10px;
  float: left;
  margin-left: 200px;
  .main-txt {
    padding: 10px;
    text-align: left;
    font-size: 12px;
    color: #000;
  }
}
::v-deep .bottom-card {
  margin-top: -60px;
  background: #fff;
  border-radius: 8px;
  height: 480px;
  line-height: 40px;
  width: 20%;
  border-radius: 10px;
  float: right;
  margin-right: 200px;
  .title {
    width: 150%;
    display: inline-block;
    line-height: 60px;
    height: 60px;
    padding-left: 20px;
    font-size: 14px;
    color: #333;
    text-align: left;
    border-bottom: 1px solid #e4e4e4;
  }
  .introduce {
    width: 100%;
    height: 100%;
    position: relative;
    img {
      width: 46px;
      height: 46px;
      border-radius: 50%;
      background: #e4e4e4;
      text-align: center;
      line-height: 46px;
      font-size: 13px;
      color: #999;
      float: left;
      margin: 20px 10px;
    }
    .introduce-title {
      position: absolute;
      font-size: 14px;
      font-weight: 700;
      color: #333;
      top: 20px;
      left: 100px;
    }
    .introduce-main {
      display: inline-block;
      width: 200px;
      height: 100%;
      font-size: 14px;
      margin: -15px 35px;
      text-align: left;
      text-indent: 20px;
    }
  }
}
.link-content {
  display: flex;
  align-items: center;
  flex-shrink: 1;
  overflow: hidden;
  position: relative;
  background: #f4f4f4;
  padding-left: 20px;
  z-index: 1;
  height: 30px;
  line-height: 30px;
  cursor: pointer;
  margin-bottom: 5px;
  padding-top: 5px;
  .page-num {
    margin-right: 10px;
    font-size: 13px;
  }
  .part {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    flex-shrink: 1;
    font-size: 13px;
  }
}
</style>
