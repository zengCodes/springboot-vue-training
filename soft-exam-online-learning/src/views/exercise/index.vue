<template>
  <div id="exercise">
    <el-tabs
      :tab-position="tabPosition"
      v-model="queryExMap.name"
      type="border-card"
      @tab-click="courseChange"
    >
      <el-row :gutter="18">
        <el-col :span="3">
          <span class="exercise-title"
            ><i class="el-icon-s-order"></i>练习导航</span
          >
        </el-col>
      </el-row>
      <el-tab-pane
        v-for="item in paperLeftMenuList"
        :label="item.name"
        :key="item.id"
        :name="String(item.id)"
      >
        <el-radio-group v-model="queryExMap.time" @change="handleChange">
          <el-radio label="5">上半年</el-radio>
          <el-radio label="11">下半年</el-radio>
        </el-radio-group>
        <el-table
          :data="paperFilterList"
          style="margin-top: 20px"
          :row-class-name="tableRowClassName"
        >
          <el-table-column
            prop="course"
            label="科目"
            min-width="200"
          ></el-table-column>
          <el-table-column
            prop="year"
            label="年分"
            min-width="200"
          ></el-table-column>
          <el-table-column
            prop="time"
            label="年度"
            min-width="200"
          ></el-table-column>
          <el-table-column
            prop="exerciseNum"
            label="练习次数"
            min-width="200"
          ></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="primary"
                @click="handleEdit(scope.row)"
                >练习</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { arrByKeyUnique, multiFilter } from "@/utils/zeng";
import { getListPaper } from "@/api/business/paper";
import { saveExerciseRecord } from "@/api/business/testRecord";
import { getListCourse } from "@/api/business/course";
export default {
  data() {
    return {
      tabPosition: "left",
      timeRadio: 0,
      queryMap: { pageNum: 1, pageSize: 100 },
      paperQuestionData: [],
      paperMenuList: [],
      selectPaperList: [],
      paperFilterList: [],
      // 左边菜单选择
      queryExMap: {
        name: undefined,
        time: undefined,
        status: true,
      },
      // 当前选择菜单
      selectMenu: "",
    };
  },
  methods: {
    // 获取左边导航数据
    async getPaperQuestionMenuList() {
      let res = await getListCourse(this.queryMap);
      let { code, data, msg } = res;
      if (code === 200) {
        this.paperMenuList = data.rows;
      } else {
        this.msgError("获取科目列表失败:" + msg);
      }
    },
    // 过滤
    filterPaperData() {
      this.paperFilterList = multiFilter(
        this.paperQuestionData,
        this.queryExMap
      );
    },
    // 处理左边导航数据
    handlePaperQuestionData(val) {
      console.log(val);
      let arr = Array.from(val);
      if (arr.length > 0) {
        arr.map((item) => {
          let timeArr = item.time.split("-");
          item.year = timeArr[0];
          item.time = timeArr[1] < 6 ? "上半年" : "下半年";
        });
      }
    },
    // 获取右边导航数据
    async getPaperQuestionList() {
      let res = await getListPaper(this.queryMap);
      let { code, data, msg } = res;
      if (code === 200) {
        this.paperQuestionData = data.rows;
        this.filterPaperData();
        await this.handlePaperQuestionData(this.paperFilterList);
      } else {
        this.msgError("获取试卷列表错误：" + msg);
      }
    },

    courseChange(val) {
      this.queryExMap.name = val.label;
      this.getPaperQuestionList();
    },
    handleChange(val) {
      this.queryExMap.time = val;
      this.getPaperQuestionList();
    },
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex === 1) {
        return "warning-row";
      } else if (rowIndex === 3) {
        return "success-row";
      }
      return "";
    },
    handleEdit(row) {
      this.saveRecord(row.id);
      this.$router.push({
        path: "/practice",
        query: {
          course: row.name,
          year: row.year,
          time: row.time,
          id: row.id,
        },
      });
    },
    // 保存练习记录
    async saveRecord(id) {
      let res = await saveExerciseRecord({
        paperId: id,
      });
      let { code } = res;
      if (code === 200) {
        this.msgSuccess("进入练习");
      }
    },
  },
  created() {
    this.getPaperQuestionMenuList();
    this.getPaperQuestionList();
  },
  computed: {
    paperLeftMenuList() {
      return arrByKeyUnique(this.paperMenuList, "name");
    },
  },
};
</script>
<style lang="scss" scoped>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}

.exercise-title {
  margin-left: -25px;
  background-color: #f3f3f6;
  color: #000;
  height: 40px;
  line-height: 30px;
  font-size: 16px;
  font-weight: 500;
}
::v-deep .el-tabs__header {
}
</style>
