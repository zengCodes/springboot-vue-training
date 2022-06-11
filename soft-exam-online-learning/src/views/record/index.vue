<template>
  <div style="margin-top: 10px" id="record">
    <el-row :gutter="30">
      <el-col :span="18" class="table-col">
        <el-card>
          <el-table
            v-loading="listLoading"
            :data="testRecord"
            highlight-current-row
            style="width: 100%"
            @row-click="itemSelect"
            border
          >
            <el-table-column
              prop="id"
              label="序号"
              align="center"
              min-width="20"
            />
            <el-table-column
              prop="paperName"
              label="试卷名称"
              min-width="100"
              align="center"
            />
            <el-table-column
              prop="courseName"
              label="科目名称"
              min-width="60"
              align="center"
            />

            <el-table-column
              prop="createTime"
              label="做题时间"
              min-width="100"
              align="center"
            />
            <el-table-column align="center" label="操作">
              <template slot-scope="{ row }">
                <router-link
                  target="_blank"
                  :to="{
                    path: '/paperDetail',
                    query: { id: row.id },
                  }"
                >
                  <el-button type="text" size="small">查看试卷</el-button>
                </router-link>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            style="margin-top: 10px"
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryParam.pageNum"
            :page-sizes="[6, 10, 15, 20]"
            :page-size="queryParam.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          ></el-pagination>
        </el-card>
      </el-col>
      <!-- <el-col :span="5">
        <el-card class="record-answer-info">
          <el-form label-width="50%">
            <el-form-item label="系统判分：">
              <span>{{ selectItem.responseTotal }}</span>
            </el-form-item>
            <el-form-item label="最终得分：">
              <span>{{ selectItem.responseTotal }}</span>
            </el-form-item>
            <el-form-item label="试卷总分：">
              <span>{{ selectItem.questionTotal }}</span>
            </el-form-item>
            <el-form-item label="正确题数：">
              <span>{{ selectItem.questionCorrect }}</span>
            </el-form-item>
            <el-form-item label="总题数：">
              <span>{{ selectItem.questionTotal }}</span>
            </el-form-item>
            <el-form-item label="用时：">
              <span>{{ selectItem.doTime }}</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col> -->
    </el-row>
  </div>
</template>

<script>
import { getRecordList } from "@/api/business/testRecord";
import { mapState } from "vuex";

export default {
  data() {
    return {
      queryParam: {
        pageNum: 1,
        pageSize: 10,
        userId: undefined,
      },
      listLoading: false,
      testRecord: [],
      total: 0,
      selectItem: {
        responseTotal: 0,
        questionTotal: 0,
        doTime: "0",
        questionCorrect: 0,
        questionCount: 0,
      },
    };
  },
  methods: {
    async getRecordListData() {
      this.listLoading = true;
      let res = await getRecordList(this.queryParam);
      let { code, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.testRecord = data.rows;
        setTimeout(() => {
          this.listLoading = false;
        }, 500);
      }
    },
    itemSelect(row, column, event) {
      this.selectItem = row;
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryParam.pageSize = newSize;
    },
    //翻页
    handleCurrentChange(current) {
      this.queryParam.pageNum = current;
    },
  },
  computed: {
    ...mapState({
      userData: (state) => state.user,
    }),
  },
  created() {
    this.getRecordListData();
    this.queryParam.userId = this.userData.userId;
  },
};
</script>

<style lang="scss" scoped>
::v-deep #record {
  overflow-x: hidden;
  .el-col-18 {
    padding-left: 50px !important;
  }
}
</style>
