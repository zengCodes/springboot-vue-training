<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>讲师管理</el-breadcrumb-item>
      <el-breadcrumb-item>讲师列表</el-breadcrumb-item>
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
          <el-input
            clearable
            size="small"
            v-model="queryMap.lecturerEmail"
            placeholder="请输入邮箱查询"
          ></el-input>
        </el-col>
        <el-col :span="5">
          <el-select
            size="small"
            v-model="queryMap.status"
            clearable
            placeholder="请选择状态查询"
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
            type="success"
            icon="el-icon-circle-plus-outline"
            @click="handleHandAdd"
            >申请讲师</el-button
          >
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
            @click="getLecturerList"
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
          :data="lecturerData"
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column
            prop="coverImg"
            label="讲师封面"
            min-width="80"
            align="center"
          >
            <template slot-scope="scope">
              <el-image
                lazy
                style="width: 100px; height: 100px"
                :src="scope.row.coverImg"
                :preview-src-list="[scope.row.coverImg]"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column
            prop="lecturerMobile"
            label="手机号"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="lecturerName"
            label="讲师名称"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="lecturerEmail"
            label="邮箱"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="introduce"
            label="简介"
            min-width="100"
            align="center"
            :formatter="filterHtml"
          ></el-table-column>
          <el-table-column
            prop="lecturerProportion"
            label="分成比例"
            align="center"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.lecturerProportion == null"> 待商量 </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="approvalStatus"
            label="审核状态"
            align="center"
            min-width="100"
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
          <el-table-column
            prop="status"
            label="启用状态"
            min-width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-switch
                v-if="scope.row.approvalStatus === 1"
                v-model="scope.row.status"
                @change="changUserStatus(scope.row)"
              ></el-switch>
              <el-tag type="warning" v-else>审核通过后开启</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" min-width="100">
            <template slot-scope="scope">
              <el-button
                type="text"
                size="mini"
                icon="el-icon-edit"
                @click="handleOpenEdit(scope.row)"
                >编辑</el-button
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
    <!-- 新增弹出框 -->
    <el-dialog
      :title="handleDialogTitle"
      :visible.sync="handleDialogVisible"
      width="50%"
      @close="handleClose('handleDialogFormRef')"
      custom-class="paper-dialog"
    >
      <el-form
        size="mini"
        :model="handleDialogForm"
        :rules="handleDialogFormRoles"
        ref="handleDialogFormRef"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="讲师名称" prop="lecturerName">
              <el-input
                disabled
                size="small"
                v-model="handleDialogForm.lecturerName"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="邮箱" prop="lecturerEmail">
              <el-input
                disabled
                size="small"
                v-model="handleDialogForm.lecturerEmail"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="封面" prop="coverImg">
              <el-upload
                :disabled="upload.uploadDisabled"
                ref="uploadImg"
                :data="upload.dataType"
                :action="upload.api"
                :limit="upload.limit"
                :headers="upload.headers"
                :multiple="upload.multiple"
                list-type="picture-card"
                :file-list="upload.file"
                accept="image/gif,image/jpeg,image/jpg,image/bmp,image/png"
                :auto-upload="upload.auto"
                :on-success="handleImgUploadSuccess"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
              >
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="简介" prop="introduce">
              <WangEditor
                ref="wangEditor"
                @changeContent="changeContent"
                :placeholder="'请输入讲师简介'"
                :upload="upload"
                :content="handleDialogForm.introduce"
              />
            </el-form-item>
          </el-col>
          <el-col :span="22">
            <el-form-item label="申请理由" prop="reason">
              <el-input
                type="textarea"
                size="small"
                v-model="handleDialogForm.reason"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="dialog-footer">
        <div class="footer-button">
          <el-button size="small" @click="handleClose('handleDialogFormRef')"
            >取 消</el-button
          >
          <el-button
            size="small"
            type="primary"
            @click="handleDialogSubmit"
            :disabled="btnDisabled"
            :loading="btnLoading"
            >确 定</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getListLecturer,
  addLecturer,
  updateLecturer,
  delLecturer,
  changLecturerStatus,
} from "@/api/business/lecturer/lecturer";
import { getUserStatus, getApprovalStatus } from "@/utils/zeng";
import { mapState } from "vuex";
import { getToken } from "@/utils/auth";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import WangEditor from "@/components/Editor/edit";

export default {
  name: "Lecturer",
  components: {
    WangEditor,
  },
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      statusOptions: [], //状态选择
      handleDialogVisible: false,
      approvalDialogVisible: false, //添加弹框是否显示
      total: 0, //总共多少条数据
      lecturerData: [], //表格数据
      queryMap: {
        pageNum: 1,
        pageSize: 6,
        status: null,
        lecturerEmail: null,
        lecturerName: null,
      },
      upload: {
        dataType: {
          type: "Lecturer",
        },
        // 文件个数超过后隐藏加号
        uploadDisabled: false,
        // 是否多传
        multiple: false,
        //  上传api
        api: process.env.VUE_APP_BASE_API + "/business/resource/file",
        file: [],
        // 是否自动
        auto: true,
        // 文件个数
        limit: 1,
        name: "file",
        fileType: "",
        headers: { Authorization: "Bearer " + getToken() },
      },
      approvalRuleForm: {}, //审核表单数据
      handleDialogForm: {}, //表单数据
      handleDialogFormRoles: {
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
        introduce: [{ required: true, message: "请输入简介", trigger: "blur" }],
      }, //添加验证
      // 是否更新
      isUpdate: false,
      handleDialogTitle: "",
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
        status: null,
      };
    },
    // 关闭弹出框
    handleClose(formName) {
      this.handleDialogVisible = false;
      this.$refs[formName].clearValidate();
      this.$refs[formName].resetFields();
      this.handleDialogForm = {};
      this.upload.file = [];
    },
    /**
     * 搜索科目
     */
    search() {
      this.queryMap.pageNum = 1;
      this.getLecturerList();
    },
    changeContent(val) {
      this.handleDialogForm.introduce = val;
    },
    handleImgUploadSuccess(response, file, fileList) {
      this.upload.uploadDisabled = true;
      this.handleDialogForm.coverImg = response.msg;
      this.upload.file.push({
        url: response.msg,
      });
    },
    /**
     * 打开添加弹框
     */
    handleHandAdd() {
      this.handleDialogTitle = "申请讲师";
      if (this.userData.nickName === null || this.userData.email === null) {
        this.$message({
          message: "请先完把个人信息填写完整",
          type: "warning",
        });
      } else {
        this.handleDialogForm.lecturerName = this.userData.nickName;
        this.handleDialogForm.lecturerEmail = this.userData.email;
        this.handleDialogForm.lecturerUserNo = this.userData.userId;
        this.isUpdate = false;
        this.handleDialogVisible = true;
      }
    },
    /**
     * 编辑
     */
    handleOpenEdit(row) {
      this.handleDialogTitle = "更新讲师信息";
      this.isUpdate = true;
      this.handleDialogForm = JSON.parse(JSON.stringify(row));
      this.upload.file.push({
        url: row.coverImg,
      });
      this.handleDialogVisible = true;
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    handleRemove(file, fileList) {
      this.upload.uploadDisabled = fileList.length >= this.limitCount;
      this.upload.file = fileList;
      this.handleDialogForm.imageUrl = [];
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getLecturerList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryPaperMap.pageNum = current;
      this.getLecturerList();
    },
    getApprovalStatus(status) {
      getApprovalStatus(status);
    },
    /**
     * 禁用启用用户
     */
    async changUserStatus(row) {
      let res = await changLecturerStatus(row.id, row.status);
      let { code, msg } = res;
      if (code === 200) {
        const message = row.status ? "用户状态已启用" : "用户状态已禁用";
        this.$notify.success({
          title: "操作成功",
          message: message,
        });
      } else {
        row.status = !row.status;
        this.msgInfo("更新用户状态失败:" + msg);
      }
    },
    /**
     * 删除
     */
    async handleDelete(id) {
      let res = await this.$confirm(
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
        delLecturer(id)
          .then((res) => {
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("讲师信息删除成功");
              this.getLecturerList();
            } else {
              this.msgError("讲师信息删除失败：" + msg);
            }
          })
          .catch((err) => {
            console.log(err);
          });
      }
    },
    /**
     * 新增、更新试卷
     */
    handleDialogSubmit() {
      let id = this.handleDialogForm.id;
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          if (this.isUpdate) {
            let res = await updateLecturer(id, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("讲师信息更新成功");
              this.getLecturerList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("讲师信息更新失败" + msg);
            }
          } else {
            let res = await addLecturer(this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("讲师信息新增成功,请等待管理员审核");
              this.getLecturerList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("讲师信息新增失败" + msg);
            }
          }
        }
      });
    },

    /**
     * 加载列表
     */
    async getLecturerList() {
      this.loading = true;
      let res = await getListLecturer(this.queryMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.lecturerData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgInfo("获取讲师列表数据错误：" + msg);
      }
    },
  },
  created() {
    this.getLecturerList();
    this.statusOptions = getUserStatus();
    this.upload.fileType = ACCEPT_CONFIG.image.join(",");
  },
  computed: {
    ...mapState({
      userData: (state) => state.user.user,
    }),
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
