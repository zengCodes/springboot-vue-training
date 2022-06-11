<template>
  <!-- 考试系统主页 -->
  <div id="exam">
    <el-card style="height: 260px">
      <el-row :gutter="20" class="row-main">
        <el-col :span="3"><span class="select-label">已选条件：</span></el-col>
        <el-col :span="6" class="type">
          <el-tag
            class="tag-item"
            v-if="selectTags.level !== ''"
            closable
            @close="handleClose(1)"
            >{{ selectTags.level }}</el-tag
          >
          <el-tag
            class="tag-item"
            v-if="selectTags.course !== ''"
            closable
            @close="handleClose(3)"
            >{{ selectTags.course }}</el-tag
          >
          <el-tag
            class="tag-item"
            v-if="selectTags.time !== ''"
            closable
            @close="handleClose(2)"
            >{{ selectTags.time }}</el-tag
          >
        </el-col>
      </el-row>
      <el-divider />
      <el-row :gutter="20" class="row-main">
        <el-col :span="3"><span class="select-label">等级：</span></el-col>
        <el-col :span="17" class="type" v-if="level.length > 0">
          <template v-for="l in level">
            <span
              class="time-select-item"
              :key="l.id"
              @click="selectKey(l.name, l.lev)"
            >
              {{ l.name }}</span
            >
          </template>
        </el-col>
      </el-row>
      <el-divider />
      <el-row :gutter="20" class="row-main">
        <el-col :span="3"><span class="select-label">科目：</span></el-col>
        <el-col :span="17" class="type">
          <swiper
            :options="swiperOption"
            ref="mySwiper"
            style="height: 100%"
            v-loading="loading"
          >
            <template v-for="c in course">
              <swiper-slide :key="c.id">
                <span
                  class="time-select-item"
                  @click="selectKey(c.name, c.lev)"
                >
                  {{ c.name }}</span
                >
              </swiper-slide>
            </template>
          </swiper>
        </el-col>
      </el-row>
      <el-divider />
      <el-row :gutter="20" class="row-main">
        <el-col :span="3"><span class="select-label">时间段：</span></el-col>
        <el-col :span="2.5">
          <el-select
            v-model="queryPaperMap.time"
            clearable
            placeholder="请选择年份"
            size="small"
          >
            <el-option
              v-for="item in yearOption"
              :key="item.label"
              :label="item.label"
              :value="item.label"
            ></el-option>
          </el-select>
        </el-col>
        <!-- <el-col :span="3.5">
          <el-select
            v-model="queryPaperMap.annual"
            clearable
            placeholder="请选择年度"
            size="small"
          >
            <el-option
              v-for="item in annualOption"
              :key="item.label"
              :label="item.label"
              :value="item.label"
            ></el-option>
          </el-select>
        </el-col> -->
      </el-row>
      <el-divider />
    </el-card>
    <div class="bg-main">
      <div class="menu-box">
        <span class="box-title">{{ currentSelectTag }}</span>
      </div>
      <div class="over-scroll">
        <exam-list
          :paperList="paperFilterList"
          @showPaperInfo="showPaperInfo"
        ></exam-list>
      </div>
    </div>
  </div>
</template>
<script>
import { Swiper, SwiperSlide } from "vue-awesome-swiper";
import ExamList from "@/components/exam/examList";
import { getListCategory } from "@/api/business/category";
import { getAllPaperList } from "@/api/business/paper";
import { mapState } from "vuex";
import { multiFilter } from "@/utils/zeng";
export default {
  data() {
    return {
      isSearch: true,
      msg: "",
      insert: "",
      loading: true,
      // 试卷列表
      paperList: [],
      paperFilterList: [],
      //等级
      level: [],
      //科目
      course: [],
      queryCategoryMap: {
        type: "course",
      },
      //筛选参数
      selectTags: {
        level: "中级",
        course: "软件设计师",
        time: "",
      },
      queryPaperMap: {
        name: "软件设计师",
        level: "中级",
        annual: null,
        time: null,
      },
      swiperOption: {
        initialSlide: 0,
        slidesPerView: 3, //一行显示3个
        spaceBetween: 20, //间隔30
        freeMode: true,
        speed: 1000, //滑动速度
        width: 500,
      },
      yearOption: [
        {
          label: "2020",
          value: 0,
        },
        {
          label: "2021",
          value: 1,
        },
      ],
      annualOption: [
        {
          label: "上半年",
          value: 0,
        },
        {
          label: "下半年",
          value: 1,
        },
      ],
    };
  },
  components: {
    ExamList,
    Swiper,
    SwiperSlide,
  },
  methods: {
    handleGetChildren(list) {
      let data = [];
      list.forEach((item) => {
        data.push(item);
      });
      return data;
    },
    //获取分类
    async getCatetorys() {
      let res = await getListCategory(this.queryCategoryMap);
      let { data, code, msg } = res;
      if (code === 200) {
        this.getHolderData(data.rows);
      } else {
        this.msgInfo("获取科目类别失败" + msg);
      }
    },
    /**
     * 加载试卷列表
     */
    async getPaperList() {
      this.loading = true;
      let res = await getAllPaperList({
        status: 1,
      });
      let { code, msg, data } = res;
      if (code === 200) {
        this.paperList = data;
        this.filterPaperData();
        setTimeout(() => {
          this.loading = false;
        }, 500);
      } else {
        this.msgError("获取所有试卷列表错误：" + msg);
      }
    },

    // 重构数据
    getHolderData(data) {
      if (data.length > 0) {
        let level = [];
        data.map((item) => {
          if (item.lev == 1) {
            level.push(item);
          }
        });
        let course = [];
        data.forEach((ele) => {
          if (ele.children != null) {
            let data = this.handleGetChildren(ele.children);
            for (let i in data) {
              if (data[i].lev === 2) {
                course.push(data[i]);
              }
            }
          }
        });
        this.level = level;
        this.course = course;
      }
    },
    //筛选
    selectKey(tag, key) {
      if (key === 1) this.selectTags.level = tag;
      else this.selectTags.course = tag;
      this.initQuery();
    },
    //删掉已选中的选项
    handleClose(tag) {
      if (tag === 1) this.selectTags.level = "";
      else if (tag === 2) this.selectTags.time = "";
      else this.selectTags.course = "";
      this.initQuery();
    },
    onSwiper(swiper) {
      console.log(swiper);
    },
    onSlideChange() {
      console.log("slide change");
    },
    close() {
      this.isSearch = !this.isSearch;
    },
    initQuery() {
      this.queryPaperMap.name = this.selectTags.course;
      this.queryPaperMap.level = this.selectTags.level;
    },
    // 点击详情
    showPaperInfo(row) {
      //  检测用户是否有真实姓名
      if (this.userData.user?.nickName != null) {
        this.$router.push({
          path: "paperInformation",
        });
        this.$store.dispatch("setPaperData", row);
      } else {
        this.msgWarning("请先实名认证才可进行下一步！");
      }
    },
    // 过滤
    filterPaperData() {
      this.paperFilterList = multiFilter(this.paperList, this.queryPaperMap);
    },
  },
  created() {
    this.getCatetorys();
    this.getPaperList();
  },
  computed: {
    ...mapState({
      userData: (state) => state.user,
    }),
    currentSelectTag() {
      let selectTags = this.selectTags;
      return selectTags.level + "-" + selectTags.course;
    },
    time() {
      return this.queryPaperMap.year + this.queryPaperMap.annual;
    },
  },
  watch: {
    queryPaperMap: {
      handler() {
        this.filterPaperData();
      },
      deep: true,
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .type {
  display: flex;
}
.el-row {
  margin: 0px 0 10px 10px;
}
.el-tag {
  margin-right: 10px;
}
.el-button--medium {
  padding: 0px 10px;
  font-size: 16px;
  border-radius: 4px;
}
//试卷
.pagination {
  padding: 20px 0px 30px 0px;
  .el-pagination {
    display: flex;
    justify-content: center;
  }
}
.bg-main {
  //后续更改滚轮样式
  background: #fff;
  border-bottom: 2px solid #25bb9b;
  border: 1px solid #eaeaea;
}
.el-card {
  height: 250px;
  margin: 0px;
}
::v-deep .swiper-slide {
  width: 130px !important;
}
::v-deep {
  .el-divider--horizontal {
    margin: 15px 0 !important;
  }
}
// 已选条件
::v-deep .tag-item {
  height: 30px;
  background: #edfaf8 !important;
  color: #00bc9b !important;
  &:hover {
    .el-tag__close {
      background-color: #1abc9c !important;
      color: #fff;
      display: inline-block;
    }
  }
}
::v-deep .el-col-3 {
  width: 10%;
}
#exam {
  height: 100%;
  width: 100%;
  overflow: hidden;
  // 每个row
  .row-main {
    height: 30px;
    line-height: 30px;
  }
  //
  .menu-box {
    margin-bottom: 20px;
    height: 40px;
    width: 100%;
    background: #fff;
    border-bottom: 2px solid #25bb9b;
    .box-title {
      background: #25bb9b;
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      height: 40px;
      line-height: 40px;
      width: 200px;
      float: left;
    }
  }
  .select-label {
    font-weight: 600;
    line-height: 30px;
    float: left;
    margin-left: 20px;
  }
  .select-label::before {
    background: #ff6547;
    content: "";
    display: inline-block;
    width: 7px;
    height: 7px;
    margin-right: 5px;
    margin-bottom: 2px;
    vertical-align: middle;
  }
  // 时间段
  .time-slect {
    width: 150px;
    height: 30px;
    color: #000;
    display: inline-block;
    margin: 0 10px 5px 0;
    font-size: 14px;
  }
  .time-select-item {
    width: 150px;
    height: 20px;
    line-height: 20px;
    color: #000;
    display: inline-block;
    margin: 0 10px 5px 0;
    font-size: 14px;
  }
  .time-select-item:hover {
    background: #edfaf8;
    color: #00bc9b;
  }
}
::v-deep .el-select {
  input {
    border: 0;
    background: #edfaf8;
    color: #00bc9b;
  }
}
::v-deep .el-select-dropdown__item.selected {
  color: #00bc9b !important;
}
// 滚动条样式
.bg-main {
  margin-top: 10px;
  height: 350px;
  .over-scroll {
    overflow-y: scroll;
    overflow-x: hidden;
    height: 250px;
  }
}
</style>
