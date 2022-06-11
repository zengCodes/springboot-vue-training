<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>试卷管理</el-breadcrumb-item>
      <el-breadcrumb-item>答题情况</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="6">
        <el-col :span="5">
          <el-select
            size="small"
            v-model="queryTestMap.paperId"
            clearable
            placeholder="请选择试卷查询"
          >
            <el-option
              v-for="item in paperData"
              :key="item.time + '-' + item.name"
              :label="item.time + '-' + item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-input
            clearable
            size="small"
            v-model="queryTestMap.nickName"
            placeholder="请输入考生姓名查询"
            @clear="search"
            class="input-with-select"
          ></el-input>
        </el-col>
        <el-col :span="8">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            @click="search"
            >查找</el-button
          >
          <el-button
            size="small"
            icon="el-icon-refresh-right"
            type="warning"
            @click="resetForm"
            >重置</el-button
          >
          <el-button size="small" icon="el-icon-refresh" @click="getTestList"
            >刷新</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <template>
        <el-table
          size="mini"
          v-loading="loading"
          border
          stripe
          :data="testData"
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column
            prop="paperId"
            label="试卷ID"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="paperName"
            label="试卷名称"
            width="150"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="nickName"
            label="考生姓名"
            width="100"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="email"
            label="考生邮箱"
            width="150"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="phone"
            label="考生联系方式"
            width="150"
            align="center"
          >
          </el-table-column>
          <el-table-column label="考试分数" mini-width="100" align="center">
            <template slot-scope="scope">
              <el-tag
                type="success"
                size="mini"
                closable
                v-text="scope.row.total"
              ></el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="考试时间"
            mini-width="150"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button
                type="text"
                size="mini"
                icon="el-icon-edit"
                @click="handleOpenEdit(scope.row)"
                >查看情况</el-button
              >
              <el-button
                style="margin-left: 10px"
                type="text"
                size="mini"
                icon="el-icon-delete"
                @click="del(scope.row.id)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </template>
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 10px"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryTestMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryTestMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>
    <!-- 试卷添加弹出框 -->
    <el-dialog
      :title="handleDialogTitle"
      :visible.sync="handleDialogVisible"
      width="50%"
      @close="handleClose"
      custom-class="paper-dialog"
    >
      <el-form
        size="mini"
        :model="handleDialogForm"
        ref="handleDialogFormRef"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="考生姓名" prop="nickName">
              <el-input
                size="small"
                v-model="handleDialogForm.nickName"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="试卷名称" prop="paperName">
              <el-input
                disabled
                size="small"
                v-model="handleDialogForm.paperName"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属科目" prop="courseName">
              <el-input
                disabled
                size="small"
                v-model="handleDialogForm.courseName"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <div class="grid-content bg-purple-light">
              <el-form-item label="考试分数" prop="total">
                <el-input
                  disabled
                  size="small"
                  v-model="handleDialogForm.total"
                ></el-input>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="考试时间" prop="createTime">
                <el-date-picker
                  v-model="handleDialogForm.time"
                  type="date"
                  value-format="yyyy-MM-dd hh:mm:ss"
                  size="small"
                  disabled
                ></el-date-picker>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="考生邮箱" prop="email">
                <el-input
                  disabled
                  size="small"
                  v-model="handleDialogForm.email"
                ></el-input>
              </el-form-item>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <div class="grid-content bg-purple" style="margin-left: 100px">
              <el-button
                size="small"
                type="primary"
                icon="el-icon-search"
                @click="showPaper"
                >查看试卷</el-button
              >
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { getListPaper, getPaper } from "@/api/business/paper";
import { getRecordList, delTestRecord } from "@/api/business/testRecord";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      paperOption: [], //试卷选择
      total: 0, //  总共多少条数据
      testData: [], //表格数据
      paperData: [],
      queryTestMap: {
        pageNum: 1,
        pageSize: 6,
        nickName: undefined,
        paperId: undefined,
      },
      //查询对象
      queryPaperMap: {
        pageNum: 1,
        pageSize: 100,
      },
      handleDialogVisible: false,
      handleDialogTitle: "",
      handleDialogForm: {}, //表单数据
    };
  },
  methods: {
    // 重置查询表单
    resetForm() {
      this.queryTestMap = {
        pageNum: 1,
        pageSize: 6,
        nickName: undefined,
        paperId: undefined,
      };
    },
    /**
     * 搜索
     */
    search() {
      this.queryTestMap.pageNum = 1;
      this.getTestList();
    },
    // 查看试卷
    async showPaper() {
      let res = await getPaper(this.handleDialogForm.paperId);
      this.$router.push({
        path: "/practice",
      });
      let { code, data } = res;
      if (code === 200) {
        this.$store.dispatch("setPaperData", data);
      }
    },
    handleOpenEdit(row) {
      this.handleDialogTitle = "考试试卷信息";
      this.handleDialogForm = JSON.parse(JSON.stringify(row));
      this.handleDialogVisible = true;
    },
    handleClose() {
      this.handleDialogVisible = false;
      this.handleDialogForm = {};
    },
    /**
     * 加载试卷列表
     */
    async getTestList() {
      this.loading = true;
      let res = await getRecordList(this.queryTestMap);
      let { code, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.testData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    },
    /**
     * 加载试卷列表
     */
    async getPaperList() {
      let res = await getListPaper(this.queryPaperMap);
      let { code, data } = res;
      if (code === 200) {
        this.paperData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    },
    /**
     * 删除
     */
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该试卷, 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      ).catch(() => {
        this.$message({
          type: "info",
          message: "已取消删除",
        });
      });
      if (res === "confirm") {
        let res = await delTestRecord(id);
        let { code, msg, data } = res;
        if (code === 200) {
          if (data === 1) {
            this.msgSuccess("考生试卷删除成功");
            this.getTestList();
          }
        } else {
          this.msgInfo("考生试卷删除失败：" + msg);
        }
      }
    },
    handleChange(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
    },
    handleRemove(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
      this.addRuleForm.imageUrl = "";
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryTestMap.pageSize = newSize;
      this.getTestList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryTestMap.pageNum = current;
      this.getTestList();
    },
    //关闭弹出框
    closeAddDialog() {
      this.$refs.addRuleFormRef.clearValidate();
      this.addRuleForm = {};
    },
    //关闭弹出框
    closeEditDialog() {
      this.$refs.editRuleFormRef.clearValidate();
      this.editRuleForm = {};
    },
  },
  created() {
    this.getTestList();
    this.getPaperList();
    setTimeout(() => {
      this.loading = false;
    }, 500);
  },
};
</script>
