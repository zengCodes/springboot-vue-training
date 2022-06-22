<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>讲师管理</el-breadcrumb-item>
      <el-breadcrumb-item>讲师审核</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="22">
        <el-col :span="5">
          <el-input
            clearable
            size="small"
            v-model="queryMap.lecturerName"
            placeholder="请输入讲师名称查询"
          ></el-input>
        </el-col>
        <el-col :span="5">
          <el-select
            size="small"
            v-model="queryMap.approvalStatus"
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
          <el-button
            size="small"
            icon="el-icon-refresh"
            @click="getLecturerApprovalList"
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
          :data="lecturerApprovalData"
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column
            prop="lessonLecturer.lecturerName"
            label="讲师名称"
            align="center"
          ></el-table-column>
          <el-table-column
            label="讲师封面图"
            prop="lessonLecturer.coverImg"
            align="center"
          >
            <template slot-scope="scope">
              <el-image
                lazy
                style="width: 110px; height: 110px"
                :src="scope.row.lessonLecturer.coverImg"
                :preview-src-list="[scope.row.lessonLecturer.coverImg]"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column
            prop="lessonLecturer.introduce"
            label="简介"
            min-width="100"
            align="center"
            :formatter="filterHtml"
          ></el-table-column>
          <el-table-column
            prop="lessonLecturer.lecturerEmail"
            label="邮箱"
          ></el-table-column>
          <el-table-column
            prop="lessonLecturer.lecturerMobile"
            label="手机号"
            align="center"
          ></el-table-column>
          <el-table-column prop="reason" label="申请原因"></el-table-column>
          <el-table-column
            prop="approvalStatus"
            label="审核状态"
            align="center"
          >
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
          <el-table-column label="操作" min-width="100">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.approvalStatus != 1"
                style="margin-left: 10px"
                type="text"
                size="mini"
                icon="el-icon-delete"
                @click="handleApproval(scope.row)"
                >审核</el-button
              >
              <el-button
                style="margin-left: 10px"
                type="text"
                size="mini"
                icon="el-icon-delete"
                @click="handleDelete(scope.row.id)"
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
        :current-page="queryMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryMap.pageSize"
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
import { getUserStatus, getApprovalStatus } from "@/utils/zeng";
import {
  getListLecturerApproval,
  delLecturerApproval,
  approvalLecturerStatus,
} from "@/api/business/lecturer/lecturerApproval";
export default {
  name: "LecturerApproval",
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      total: 0, //总共多少条数据
      lecturerApprovalData: [], //表格数据
      queryMap: {
        pageNum: 1,
        pageSize: 6,
        approvalStatus: undefined,
        lecturerName: undefined,
      },
      approvalRuleForm: {}, //审核表单数据
      editRuleForm: {}, //修改表单数据
      addRuleForm: {}, //新增
      addRules: {
        lecturerName: [
          { required: true, message: "请输入讲师名称", trigger: "blur" },
          {
            min: 2,
            max: 10,
            message: "长度在 2 到 10 个字符",
            trigger: "blur",
          },
        ],
        lecturerEmail: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
        ],
        introduce: [{ required: true, message: "请选择简介", trigger: "blur" }],
      }, //添加验证
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
        ?.replace(/<[^>]*>/g)
        ?.replaceAll("undefined", "")
        ?.replaceAll("&nbsp;", "");
    },
    //重置查询表单
    resetForm() {
      this.queryMap = {
        pageNum: 1,
        pageSize: 6,
        approvalStatus: undefined,
        lecturerName: undefined,
      };
    },
    // 关闭弹出框
    closeDialog(formName) {
      this.approvalDialogVisible = false;
      this.$refs[formName].resetFields();
    },
    /**
     * 搜索科目
     */
    search() {
      this.queryMap.pageNum = 1;
      this.getLecturerApprovalList();
    },
    approval() {
      this.addDialogVisible = true;
    },

    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getLecturerApprovalList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryPaperMap.pageNum = current;
      this.getLecturerApprovalList();
    },
    getApprovalStatus(status) {
      getApprovalStatus(status);
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
          let res = await approvalLecturerStatus(id, this.approvalForm);
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
     * 删除
     */
    async handleDelete(id) {
      var res = await this.$confirm(
        "此操作将永久删除该讲师信息, 是否继续?",
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
        let res = await delLecturerApproval(parseInt(id));
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("讲师删除成功");
          this.getLecturerApprovalList();
        } else {
          this.msgInfo("删除失败：" + msg);
        }
      }
    },
    /**
     * 加载试卷列表
     */
    async getLecturerApprovalList() {
      this.loading = true;
      let res = await getListLecturerApproval(this.queryMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.lecturerApprovalData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgInfo("获取审核列表错误：" + msg);
      }
    },
  },
  created() {
    this.getLecturerApprovalList();
    this.statusOptions = getUserStatus();
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
.add-menu {
  margin-left: 10px;
  margin-right: 10px;
}
.import-tip {
  margin-bottom: 10px;
}

.icon-word {
  font-size: 85px;
  margin-left: 25px;
  color: #008df0;
}
.word-dialog {
  overflow: scroll;
}
.word-main {
  width: 100%;
  height: 600px;
}
</style>
