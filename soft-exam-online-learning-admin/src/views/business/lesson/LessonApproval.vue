<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>课程管理</el-breadcrumb-item>
      <el-breadcrumb-item>课程审核</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="12">
        <el-col :span="5">
          <el-select
            size="small"
            v-model="queryLessonApprovalMap.approvalStatus"
            clearable
            placeholder="请选择审核状态查询"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            @click="searchLessonApproval"
            >查询</el-button
          >
          <el-button
            size="small"
            icon="el-icon-refresh"
            @click="getLessonApprovalList"
            >刷新</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <el-table
        size="mini"
        v-loading="loading"
        border
        stripe
        :data="lessonApprovalData"
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column
          prop="lesson.name"
          width="100"
          label="课程名称"
          align="center"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="lesson.nickName"
          width="100"
          label="课程讲师"
          align="center"
        ></el-table-column>
        <el-table-column label="课程封面图" prop="lesson.logo" align="center">
          <template slot-scope="scope">
            <el-image
              lazy
              style="width: 110px; height: 110px"
              :src="scope.row.lesson.logo"
              :preview-src-list="[scope.row.lesson.logo]"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column
          prop="lesson.introduce"
          label="课程介绍"
          align="center"
          min-width="100"
          :formatter="filterHtml"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="lesson.typeName"
          label="课程类型"
          align="center"
        ></el-table-column>
        <el-table-column prop="approvalStatus" label="审核状态" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.approvalStatus === 0" type="warning">
              待审核
            </el-tag>
            <el-tag v-else-if="scope.row.approvalStatus === 1" type="success">
              已通过
            </el-tag>
            <el-tag v-else-if="scope.row.approvalStatus === 2" type="info">
              审核不通过
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalTime" label="审核时间" align="center">
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.approvalStatus != 1"
              style="margin-left: 10px"
              type="text"
              size="mini"
              icon="el-icon-folder-checked"
              @click="handleApproval(scope.row)"
              >审核</el-button
            >
            <el-button
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 10px"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryLessonApprovalMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryLessonApprovalMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>
    <!-- 审核 -->
    <el-dialog
      :title="approvalDialogTitle"
      :visible.sync="approvalDialogVisible"
      width="50%"
      @close="closeDialog('approvalFormRef')"
      custom-class="paper-dialog"
    >
      <el-form
        size="mini"
        :model="approvalForm"
        :rules="approvalRule"
        ref="approvalFormRef"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="审核结果" prop="approvalStatus">
              <el-radio-group v-model="approvalForm.approvalStatus">
                <el-radio :label="0">不通过</el-radio>
                <el-radio :label="1">通过</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="意见" prop="comment">
              <el-input
                size="small"
                v-model="approvalForm.comment"
                type="textarea"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="dialog-footer">
        <div class="footer-button">
          <el-button size="small" @click="closeDialog('approvalFormRef')"
            >取 消</el-button
          >
          <el-button
            size="small"
            type="primary"
            @click="handleApprovalSubmit"
            :disabled="btnDisabled"
            :loading="btnLoading"
            >确定</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getApprovalState } from "@/utils/zeng";

import {
  getListLessonApproval,
  approvalLesson,
  delLessonApproval,
} from "@/api/business/lesson/lessonApproval";
export default {
  name: "lessonApproval",
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      total: 0, //总共多少条数据
      lessonApprovalData: [], //表格数据
      queryLessonApprovalMap: {
        pageNum: 1,
        pageSize: 6,
        approvalStatus: null,
        lessonName: null,
      },
      // 审核
      statusOptions: null,
      approvalDialogTitle: "",
      approvalForm: {},
      approvalDialogVisible: false,
      approvalRule: {
        approval_status: [
          { required: true, message: "请选中审核是否通过", trigger: "blur" },
        ],
        common: [
          { required: true, message: "请输入审核意见", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    filterHtml(row, column, value, index) {
      return value
        .replace(/<[^>]*>/g)
        .replaceAll("undefined", "")
        .replaceAll("&nbsp;", "");
    },
    //重置查询表单
    resetForm() {
      this.queryLessonApprovalMap = {
        pageNum: 1,
        pageSize: 6,
      };
    },
    /**
     * 搜索科目
     */
    searchLessonApproval() {
      this.queryLessonApprovalMap.pageNum = 1;
      this.getLessonApprovalList();
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryLessonApprovalMap.pageSize = newSize;
      this.getLessonApprovalList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryLessonApprovalMap.pageNum = current;
      this.getLessonApprovalList();
    },
    /**
     * 审核
     */
    handleApproval(row) {
      this.approvalDialogTitle = "课程审核";
      row.approvalStatus = "";
      this.approvalForm = JSON.parse(JSON.stringify(row));
      this.approvalDialogVisible = true;
    },
    // 关闭弹出框
    closeDialog(formName) {
      this.approvalDialogVisible = false;
      this.$refs[formName].resetFields();
      this.getLessonApprovalList();
      this.loading = false;
    },
    /**
     * 提交审核
     */
    handleApprovalSubmit() {
      let id = this.approvalForm.id;
      this.$refs.approvalFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          let res = await approvalLesson(id, this.approvalForm);
          const text =
            this.approvalForm.approvalStatus === 1 ? "通过" : "不通过";
          let { code, msg } = res;
          if (code === 200) {
            this.msgSuccess("课程审核成功，结果为" + text);
            this.btnDisabled = false;
            this.btnLoading = false;
            this.approvalDialogVisible = false;
            this.approvalForm = {};
            this.getLessonApprovalList();
          } else {
            this.msgError("课程审核错误：" + msg);
          }
        }
      });
    },
    /**
     * 加载文件资源列表
     */
    async getLessonApprovalList() {
      this.loading = true;
      let res = await getListLessonApproval(this.queryLessonApprovalMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.lessonApprovalData = data.rows;
        this.lessonApprovalData.map((item) => {
          item.lesson.logo = this.logoFilter(item);
        });
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgInfo("获取审核列表错误：" + msg);
      }
    },
    async handleDelete(id) {
      var res = await this.$confirm(
        "此操作将永久删除该记录, 是否继续?",
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
        let res = await delLessonApproval(id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("删除成功");
        } else {
          this.msgInfo("删除失败:" + msg);
        }
        this.getLessonList();
      }
    },
    logoFilter(value) {
      return value.lesson.logo;
    },
  },
  created() {
    this.getLessonApprovalList();
    this.statusOptions = getApprovalState();
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
