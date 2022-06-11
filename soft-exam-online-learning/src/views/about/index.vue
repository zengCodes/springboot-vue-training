<template>
  <div id="home">
    <div class="main-search">
      <el-card>
        <el-row :gutter="20">
          <el-col :span="3" style="color: yellowgreen">已选条件：</el-col>
          <el-col :span="6" class="type">
            <el-tag
              v-if="this.selectTags.level !== ''"
              closable
              size="small"
              @close="handleClose(0)"
              >{{ this.selectTags.level }}</el-tag
            >
            <el-tag
              v-if="this.selectTags.course !== ''"
              closable
              size="small"
              @close="handleClose(1)"
              >{{ this.selectTags.course }}</el-tag
            >
            <el-tag
              v-if="this.selectTags.time !== ''"
              closable
              size="small"
              @close="handleClose(2)"
              >{{ this.selectTags.time }}</el-tag
            >
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="3" style="color: yellowgreen">等级：</el-col>
          <el-col :span="4" class="type">
            <el-button
              type="text"
              plain
              v-for="l in level"
              :key="l"
              size="medium"
              @click="selectKey(l, 0)"
              >{{ l }}</el-button
            >
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="3" style="color: yellowgreen">科目：</el-col>
          <el-col :span="17" class="type">
            <swiper :options="swiperOption" ref="mySwiper" style="height: 100%">
              <swiper-slide v-for="c in course" :key="c">
                <el-button type="text" size="medium" @click="selectKey(c, 1)">{{
                  c
                }}</el-button>
              </swiper-slide>
            </swiper>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="3" style="color: yellowgreen">时间段：</el-col>
          <el-col :span="3">
            <el-button
              type="text"
              v-for="t in time"
              :key="t"
              size="medium"
              @click="selectKey(t, 2)"
              >{{ t }}</el-button
            >
          </el-col>
        </el-row>
      </el-card>
    </div>
    <div class="myExam">
      <div class="wrapper">
        <!-- <ul class="top">
          <li class="order">试卷列表</li>
          <li class="search-li">
            <div class="icon">
              <input type="text" placeholder="试卷名称" class="search" />
              <i class="el-icon-search"></i>
            </div>
          </li>
          <li>
            <el-button type="primary" @click="search()">搜索试卷</el-button>
          </li>
        </ul>-->
        <div class="exam-item">
          <exam-list></exam-list>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { Swiper, SwiperSlide } from 'vue-awesome-swiper'
import ExamList from '@/components/exam/examList'
export default {
  data() {
    return {
      msg: '',
      insert: '',
      //等级
      level: ['初级', '中级', '高级'],
      //科目
      course: [
        '软件设计师',
        '软件测评师',
        '数据库系统工程师',
        '系统集成项目管理工程师',
        '软件设计师',
        '软件测评师',
        '数据库系统工程师',
        '系统集成项目管理工程师',
        '软件设计师',
        '软件测评师',
        '数据库系统工程师',
        '系统集成项目管理工程师',
      ],
      //时间
      time: ['上午题', '下午题'],
      //筛选参数
      selectTags: {
        level: '',
        course: '软件设计师',
        time: '',
      },
      swiperOption: {
        initialSlide: 0,
        slidesPerView: 3, //一行显示3个
        spaceBetween: 50, //间隔30
        freeMode: true,
        speed: 1000, //滑动速度
        width: 500,
        // navigation: {
        //   nextEl: ".swiper-button-next",
        //   prevEl: ".swiper-button-prev",
        // },
      },
    }
  },
  components: {
    ExamList,
    Swiper,
    SwiperSlide,
  },
  methods: {
    //筛选
    selectKey(tag, key) {
      if (key === 0) this.selectTags.level = tag
      else if (key === 1) this.selectTags.course = tag
      else this.selectTags.time = tag
    },
    //删掉已选中的选项
    handleClose(tag) {
      if (tag === 0) this.selectTags.level = ''
      else if (tag === 1) this.selectTags.course = ''
      else this.selectTags.time = ''
    },
    onSwiper(swiper) {
      console.log(swiper)
    },
    onSlideChange() {
      console.log('slide change')
    },
  },
}
</script>
<style lang="scss" scoped>
#home {
  height: 100%;
  width: 100%;
}
//搜索
::v-deep .main-search {
  .type {
    display: flex;
    // width: 80%;
    // height: 30px;
  }
  .el-row {
    margin: 0px 0 15px 10px;
  }
  .el-tag {
    margin-right: 10px;
  }
  .el-button--medium {
    padding: 0px 10px;
    font-size: 16px;
    border-radius: 4px;
  }
}
//试卷
.pagination {
  padding: 20px 0px 30px 0px;
  .el-pagination {
    display: flex;
    justify-content: center;
  }
}
.wrapper .top .order {
  cursor: pointer;
}
.wrapper .top .order:hover {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.wrapper .top .order:visited {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.top .el-icon-search {
  position: absolute;
  right: 10px;
  top: 10px;
}
.top .icon {
  position: relative;
}
.wrapper .top {
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}
.myExam .search-li {
  margin-left: auto;
}
.top .search-li {
  margin-left: auto;
}
.top li {
  display: flex;
  align-items: center;
}
.top .search {
  margin-left: auto;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #eee;
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
}
.top .search:hover {
  color: #0195ff;
  border-color: #0195ff;
}
.wrapper .top {
  display: flex;
}
.wrapper .top li {
  margin: 20px;
}
.myExam {
  //后续更改滚轮样式
  overflow: auto;
  height: 400px;
  width: 100%;
  margin: 0 auto;
}
// .myExam .title {
//   margin: 20px;
// }
// .myExam .wrapper {
//   background-color: #fff;
// }
</style>
