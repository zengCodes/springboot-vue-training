<template>
  <!-- 课程 -->
  <el-scrollbar style="height: 100%">
    <div id="lesson">
      <div class="_location"></div>
      <!-- 置顶按钮 -->
      <div class="to-top" @click="toTop">
        <i class="el-icon-upload2"></i>
      </div>
      <!-- 头部轮播 -->
      <div class="header">
        <el-carousel indicator-position="outside" class="carousel-main">
          <el-carousel-item v-for="album in albumList" :key="album.id">
            <img :src="album.location" alt="" class="img-item" />
          </el-carousel-item>
        </el-carousel>
        <div class="right-main">
          <span class="title-item">最近学习</span>
          <span class="button-item">点击了解</span>
        </div>
      </div>
      <!-- 导航 -->
      <div class="nav-main">
        <el-row :gutter="24" class="row-main">
          <el-col :span="4"
            ><div class="grid-content nav-title">
              <span class="title">课程导航</span>
            </div></el-col
          >
          <el-col
            :span="5"
            class="col-item"
            v-for="(item, i) in lessonTypeList"
            :key="item.id"
            :style="{ color: item.activated ? '#25bb9b' : '' }"
            ><div
              class="grid-content"
              @mouseover="handleOver(item)"
              @mouseleave="handleLeave(item)"
              :class="item.activated ? 'activate-nav' : ''"
              @click="selectType(i)"
            ></div>
            {{ item.typeName }}</el-col
          >
        </el-row>
      </div>
      <!-- 导航内容 -->
      <template v-for="lesson in lessonTreeData">
        <div class="lesson-main">
          <div class="main-header">
            <img src="@/assets/images/apply.png" alt="" style="width: 230px" />
          </div>
        </div>
        <div class="nav-main">
          <el-row :gutter="24" class="row-main">
            <el-col :span="4"
              ><div class="grid-content nav-title">
                <span class="title">{{ lesson.name }}</span>
              </div></el-col
            >
          </el-row>
        </div>
        <div class="nav-main">
          <el-row
            :gutter="24"
            class="list-main"
            :class="handleTypeClass(lesson.typeId)"
          >
            <el-col :span="5" v-for="item in lesson.children" :key="item.name"
              ><div
                class="grid-content li-item"
                @mouseover="handleOver(item)"
                @mouseleave="handleLeave(item)"
              >
                <span class="item-title">{{ item.name }}</span>
                <span class="item-go" @click="toLessonIntroduce(item)"
                  >GO <i class="el-icon-right"></i
                ></span>
                <span class="item-txt"></span>
                <div class="item-circle">
                  <img :src="item.logo" class="circle-img" />
                </div>
                <!-- <span class="dialog-item" v-show="item.activite"></span> -->
              </div></el-col
            >
          </el-row>
        </div>
      </template>
    </div>
  </el-scrollbar>
</template>
<script>
import { lessonTree } from "@/api/business/lesson/lesson";
import { getListCategory } from "@/api/business/category";
import { getAlbumImage } from "@/api/business/resource";

export default {
  data() {
    return {
      // 导航数据
      queryTypeMap: {
        pageNum: 1,
        pageSize: 100,
        type: "lesson",
      },
      lessonTypeList: [],
      albumList: [],
      lessonTreeData: [],
    };
  },
  components: {},
  methods: {
    handleOver(item) {
      item.activated = true;
    },
    // 鼠标离开
    handleLeave(item) {
      item.activated = false;
    },
    //
    handleTypeClass(type) {
      switch (type) {
        case 0:
          return "type-one";
        case 1:
          return "type-two";
        case 2:
          return "type-three";
        case 3:
          return "type-four";
        case 4:
          return "type-five";
        default:
          return "type-one";
      }
    },
    //加载分类数据
    async getCategoryList() {
      let res = await getListCategory(this.queryTypeMap);
      let { code, data, msg } = res;
      if (code === 200) {
        let lessonTypeList = data.rows.map((item) => {
          return {
            typeName: item.name,
            activated: false,
            typeId: item.id,
          };
        });
        this.lessonTypeList = lessonTypeList;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取分类列表失败:" + msg);
      }
    },
    // 加载课程树
    async getLessonTree() {
      let res = await lessonTree(true);
      let { code, data, msg } = res;
      if (code === 200) {
        data.map((item) => {
          item.children.map((i) => {
            if (i?.name) {
              i.name = i.name.slice(0, 10);
            }
          });
        });
        this.lessonTreeData = data;
      } else {
        this.msgError("获取课程数据错误：" + msg);
      }
    },
    // 加载轮播图片
    async getBannerImage() {
      let res = await getAlbumImage({
        albumType: 39,
        pageNum: 1,
        pageSize: 100,
      });
      let { code, msg, data } = res;
      if (code === 200) {
        this.albumList = data.rows[0].imgList;
      } else {
        this.msgError("获取相册列表失败:" + msg);
      }
    },
    // 锚点
    selectType(i) {
      if (i > 1) {
        document
          .getElementsByClassName("nav-title")
          [i - 1].scrollIntoView({ behavior: "smooth", block: "start" });
      }
    },
    // 置顶
    toTop() {
      document.getElementsByClassName("_location")[0].scrollIntoView(true);
    },
    // 跳转到视频
    toLessonIntroduce(row) {
      this.$router.push({
        path: "/lessonIntroduce",
        query: {
          id: row.id,
        },
      });
    },
  },
  created() {
    // 获取课程分类
    this.getCategoryList();
    this.getLessonTree();
    // 获取轮播
    this.getBannerImage();
  },
};
</script>
<style lang="scss" scoped>
#lesson {
  width: 100%;
  height: 100%;
}
// 置顶按钮
.to-top {
  background: #fff;
  border-radius: 12px;
  bottom: 200px;
  box-shadow: 0px 9px 18px rgba(0, 0, 0, 0.06);
  color: #999;
  font-size: 30px;
  height: 60px;
  line-height: 60px;
  position: fixed;
  right: 40px;
  text-align: center;
  width: 60px;
  cursor: pointer;
}
.header {
  display: flex;
  justify-content: center;
  background-color: #f7f8fa;
  height: 320px;
  width: 100%;
  // border: 1px solid black;
  .carousel-main {
    width: 886px;
    height: 300px;
    overflow: hidden;
    border-radius: 10px;
    background: #ffffff;
    box-shadow: 10px 10px 10px rgba(0, 0, 0, 0.5);
    /*考虑浏览器兼容性*/
    -moz-box-shadow: 10px 10px 10px rgba(0, 0, 0, 0.5);
    -webkit-box-shadow: 10px 10px 10px rgba(0, 0, 0, 0.5);
    .img-item {
      width: 886px;
      height: 300px;
    }
  }
  .right-main {
    position: relative;
    display: inline-block;
    background: #202742;
    color: #fff;
    border-radius: 10px;
    width: 299px;
    height: 250px;
    padding: 25px 0;
    font-size: 14px;
    margin-left: 20px;
    .title-item {
      height: 32px;
      width: 76px;
      line-height: 32px;
      position: absolute;
      font-weight: bold;
      font-size: 14px;
      top: 0;
      right: 0;
      border-radius: 0 6px;
      background-color: #fe5000;
    }
    .button-item {
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      background-color: #22ae90;
      height: 30px;
      line-height: 30px;
      width: 150px;
      display: inline-block;
      border-radius: 5px;
      font-size: 14px;
      cursor: pointer;
    }
  }
}
::v-deep .nav-main {
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #fff;
  .el-col {
    padding-left: 0 !important;
  }
  .row-main {
    width: 78%;
    height: 40px;
    line-height: 40px;
    // border-bottom: 2px solid #25bb9b;
  }
  .col-item {
    position: relative;
    font-size: 16px;
    cursor: pointer;
    font-weight: 700;
    .grid-content {
      position: absolute;
      bottom: 5px;
      left: 50%;
      transform: translateX(-50%);
      width: 80px;
      height: 100%;
    }
  }
  .activate-nav {
    color: #25bb9b;
    border-bottom: 2px solid #25bb9b;
  }
  .nav-title {
    margin-bottom: 20px;
    height: 40px;
    width: 100%;
    background: #fff;
    .title {
      background: #25bb9b;
      color: #fff;
      font-size: 20x;
      font-weight: 700;
      height: 40px;
      line-height: 40px;
      width: 200px;
      float: left;
    }
  }
  .list-main {
    padding: 10px;
    width: 70%;
    // item
    .li-item:hover {
      transform: scale(1.1);
    }
    .li-item {
      position: relative;
      display: inline-block;
      vertical-align: top;
      width: 180px;
      height: 130px;
      font-size: 12px;
      border-radius: 10px;
      transition: all 0.6s;
      .item-title {
        position: absolute;
        top: 15px;
        left: 10px;
        font-size: 16px;
        font-weight: 600;
      }
      .item-circle {
        overflow: hidden;
        position: absolute;
        bottom: 15px;
        right: 20px;
        display: inline-block;
        height: 70px;
        width: 70px;
        border-radius: 50%;
        background-color: rgba(0, 0, 0, 0.5);
        .circle-img {
          height: 70px;
          width: 70px;
        }
      }
      .item-go {
        cursor: pointer;
        text-align: center;
        position: absolute;
        top: 50%;
        left: 10px;
        transform: translateY(-50%);
        border-radius: 10px;
        display: inline-block;
        height: 20px;
        line-height: 20px;
        width: 40px;
        color: #fff;
        padding: 5px;
        background-color: rgba(0, 0, 0, 0.3);
      }
    }
    .dialog-item {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      width: 100%;
      height: 50%;
      border-radius: 5px;
      background: rgba(0, 0, 0, 0.5);
      color: #fff;
      transition: top 0.25s ease;
    }
  }
  .type-one {
    .li-item {
      background-color: #787cef;
    }
  }
  .type-two {
    .li-item {
      background-color: #4feead;
    }
  }
  .type-three {
    .li-item {
      background-color: #feb943;
    }
  }
  .type-four {
    .li-item {
      background-color: #20c9fe;
    }
  }
}
.lesson-main {
  background-color: #fff;
  width: 100%;
  .main-header {
    text-align: center;
  }
}
</style>
