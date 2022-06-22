<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>课程管理</el-breadcrumb-item>
      <el-breadcrumb-item>课程列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="6">
        <el-col :span="5">
          <el-select
            size="small"
            v-model="queryLessonMap.type"
            clearable
            placeholder="请选择课程类型"
          >
            <el-option
              v-for="item in lessonTypeOption"
              :key="item.name"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            @click="searchLesson"
            >查询</el-button
          >
          <el-button
            size="small"
            type="success"
            icon="el-icon-upload"
            @click="handleAdd"
          >
            新增课程<el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>

          <el-button size="small" icon="el-icon-refresh" @click="getLessonList"
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
        :data="lessonData"
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column
          prop="name"
          label="课程名称"
          align="center"
          min-width="100"
        ></el-table-column>
        <el-table-column
          label="课程封面图"
          min-width="120"
          prop="logo"
          align="center"
        >
          <template slot-scope="scope">
            <el-image
              lazy
              style="width: 110px; height: 110px"
              :src="scope.row.logo"
              :preview-src-list="[scope.row.logo]"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column
          prop="nickName"
          label="讲师"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="typeName"
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
        <el-table-column
          prop="isPutAway"
          label="上下架"
          align="center"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="scope.row.approvalStatus === 1"
              v-model="scope.row.isPutAway"
              @change="changStatus(scope.row)"
            ></el-switch>
            <el-tag type="warning" v-else>审核通过后开启</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          align="center"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="scope.row.status != 2"
              v-model="scope.row.status"
              @change="changLessonStatus(scope.row)"
            ></el-switch>
            <span v-else>{{ getLessonStatus(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.schooling && scope.row.approvalStatus === 1"
              type="text"
              icon="el-icon-video-play"
              :loading="btnLoading"
              :disabled="btnDisabled"
              @click="handleSchooling(scope.row)"
            >
              进入授课
            </el-button>
            <el-button
              type="text"
              icon="el-icon-edit"
              :loading="btnLoading"
              :disabled="btnDisabled"
              @click="handleOpenEdit(scope.row.id)"
              >编辑</el-button
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
        :current-page="queryLessonMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryLessonMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>
    <!-- 添加、新增弹出框 -->
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
        :rules="handleDialogFormRoles"
        ref="handleDialogFormRef"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="课程简介" prop="introduce">
              <WangEditor
                ref="wangEditor"
                @changeContent="changeContent"
                :placeholder="'请输入课程简介'"
                :upload="upload"
                :content="handleDialogForm.introduce"
              />
            </el-form-item>
            <div></div>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="封面LOGO" prop="logo">
              <el-upload
                :disabled="upload.uploadDisabled"
                ref="uploadImg"
                :data="upload.dataType"
                :action="upload.api"
                :name="upload.name"
                :limit="upload.limit"
                :headers="upload.headers"
                :multiple="upload.multiple"
                list-type="picture-card"
                :file-list="upload.file"
                :accept="upload.fileType"
                :on-preview="handleImgUploadPreview"
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
          <el-col :span="11">
            <el-form-item label="讲师名称" prop="nickName">
              <el-input
                size="small"
                v-model="handleDialogForm.nickName"
                disabled
                placeholder="请输入讲师名称"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="课程名称" prop="name">
              <el-input
                placeholder="请输入课程名称"
                size="small"
                v-model="handleDialogForm.name"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <!--开始时间-->
          <el-col :span="11">
            <el-form-item label="开始时间：" prop="teachTime">
              <el-date-picker
                v-model="handleDialogForm.teachTime"
                type="date"
                :picker-options="pickerOptionsStart"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择开始时间"
                style="width: 100%"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <!--结束时间-->
          <el-col :span="11">
            <el-form-item label="结束时间：" prop="endTime">
              <el-date-picker
                v-model="handleDialogForm.endTime"
                type="date"
                :picker-options="pickerOptionsEnd"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择结束时间"
                style="width: 100%"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="是否免费" prop="isFree">
              <el-radio-group v-model="handleDialogForm.isFree" size="small">
                <el-radio :label="1">免费</el-radio>
                <el-radio :label="0">收费</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="课程分类" prop="type">
              <el-select
                v-model="handleDialogForm.type"
                clearable
                placeholder="请选择分类"
                size="small"
              >
                <el-option
                  v-for="item in lessonTypeOption"
                  :key="item.name"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="原价" prop="original">
              <el-input-number
                v-model="handleDialogForm.original"
                :min="1"
                label="原价"
                size="small"
                :precision="2"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="优惠折扣" prop="discount">
              <el-input-number
                v-model="handleDialogForm.discount"
                :min="0"
                :max="1"
                label="优惠折扣"
                size="small"
                :precision="2"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="handleDialogForm.sort"
                :min="1"
                label="排序"
                size="small"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="dialog-footer">
        <div class="footer-button">
          <el-button @click="handleClose" size="small">取 消</el-button>
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
    <!-- 图片预览 -->
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from "vuex";
import {
  getListLesson,
  getLessonInfo,
  addLesson,
  updateLesson,
  delLesson,
  updateStatus,
  updateLessonStatus,
} from "@/api/business/lesson/lesson";
import { getToken } from "@/utils/auth";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import { getListCategory } from "@/api/business/category";
import WangEditor from "@/components/Editor/edit";
export default {
  components: {
    WangEditor,
  },
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      handleDialogVisible: false,
      dialogVisible: false,
      total: 0, //总共多少条数据
      lessonData: [], //表格数据
      // 搜索条件
      lessonTypeOption: [],
      queryLessonMap: {
        pageNum: 1,
        pageSize: 6,
        type: null,
      },
      queryTypeMap: {
        pageNum: 1,
        pageSize: 100,
        type: "lesson",
      },
      handleDialogForm: {},
      handleDialogTitle: "",
      dialogImageUrl: null,
      upload: {
        dataType: {
          type: "Lesson",
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
      handleDialogFormRoles: {
        introduce: [
          { required: true, message: "请输入课程简介", trigger: "blur" },
        ],
        name: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
        isFree: [
          { required: true, message: "请选择是否免费", trigger: "blur" },
        ],
        type: [
          { required: true, message: "请选择课程分类", trigger: "change" },
        ],
        original: [
          { required: true, message: "请输入课程原价", trigger: "blur" },
        ],
        discount: [
          { required: true, message: "请输入课程优惠折扣", trigger: "blur" },
        ],
        logo: [{ required: true, message: "请上传课程封面", trigger: "blur" }],
      },
      // 开始时间限制
      pickerOptionsStart: {
        disabledDate: (time) => {
          if (this.handleDialogForm.endTime) {
            return (
              time.getTime() >
                new Date(this.handleDialogForm.endTime).getTime() ||
              time.getTime() <= new Date().getTime() - 86400000
            );
          }
          return time.getTime() <= new Date().getTime() - 86400000;
        },
      },
      // 结束时间限制
      pickerOptionsEnd: {
        disabledDate: (time) => {
          if (this.handleDialogForm.teachTime) {
            return (
              time.getTime() <
              new Date(this.handleDialogForm.teachTime).getTime()
            );
          }
          return time.getTime() <= new Date().getTime() - 86400000;
        },
      },
    };
  },
  methods: {
    //重置查询表单
    resetForm() {
      this.queryLessonMap = {
        pageNum: 1,
        pageSize: 6,
        type: null,
      };
    },
    // 添加
    handleAdd() {
      if (this.userInfo.nickName === "" || this.userInfo.nickName === null) {
        this.msgInfo("请先完善个人信息！");
      } else {
        this.handleDialogForm.nickName = this.userInfo.nickName;
        this.handleDialogTitle = "添加课程";
        this.isUpdate = false;
        this.handleDialogVisible = true;
      }
    },
    //关闭弹出框
    handleClose() {
      this.$refs.wangEditor.clearEditorContent();
      this.handleDialogVisible = false;
      this.handleDialogForm.logo = [];
      this.handleDialogForm = {};
      this.upload.file = [];
      this.$refs.handleDialogFormRef.clearValidate();
      this.getLessonList();
      this.loading = false;
    },
    handleRemove(file, fileList) {
      this.upload.uploadDisabled = fileList.length >= this.limitCount;
      this.upload.file = fileList;
      this.handleDialogForm.logo = [];
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    changeContent(val) {
      this.handleDialogForm.introduce = val;
    },
    /**
     * 搜索科目
     */
    searchLesson() {
      this.queryLessonMap.pageNum = 1;
      this.getLessonList();
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryLessonMap.pageSize = newSize;
      this.getLessonList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryLessonMap.pageNum = current;
      this.getLessonList();
    },
    handleImgUploadSuccess(response, file, fileList) {
      this.uploadDisabled = true;
      this.handleDialogForm.logo = response.msg;
      this.upload.file.push({
        url: response.msg,
      });
    },
    handleImgUploadPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    // 启用状态改变
    changLessonStatus(row) {
      if (row.approvalStatus != 1) {
        this.msgWarning("请先通过审核！");
        row.status = !row.status;
        return;
      }
      let text = row.status ? "启用" : "停用";
      this.$confirm('确认要"' + text + '""' + row.name + '"课程吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(function () {
          return updateLessonStatus(row.id, row.status ? 1 : 0);
        })
        .then(() => {
          this.msgSuccess(text + "成功");
        })
        .catch(function () {
          row.status = !row.status;
        });
    },
    /**
     * 修改信息
     */
    async handleOpenEdit(id) {
      this.handleDialogTitle = "更新课程信息";
      this.isUpdate = true;
      let res = await getLessonInfo(id);
      let { code, data } = res;
      if (code === 200) {
        this.handleDialogForm = data;
        this.upload.file.push({
          url: data.logo,
        });
      }
      this.handleDialogVisible = true;
    },
    // 课程上下架
    async changStatus(row) {
      let text = row.isPutAway ? "上架" : "下架";
      this.$confirm('确认要"' + text + '""' + row.name + '"课程吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(function () {
          return updateStatus(row.id, row.isPutAway);
        })
        .then(() => {
          this.msgSuccess(text + "成功");
        })
        .catch(function () {
          row.isPutAway = !row.isPutAway;
        });
    },
    /**
     * 删除文件
     */
    async handleDelete(id) {
      var res = await this.$confirm(
        "此操作将永久删除该文件, 是否继续?",
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
        let res = await delLesson(id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("课程删除成功");
        } else {
          this.msgInfo("课程删除失败:" + msg);
        }
        this.getLessonList();
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
            let res = await updateLesson(id, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("课程信息更新成功");
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
              this.upload.file = [];
              this.getLessonList();
            } else {
              this.msgError("课程信息更新失败：" + msg);
            }
          } else {
            this.handleDialogForm.userNo = this.userInfo?.userId;
            let res = await addLesson(this.handleDialogForm);
            let { code, msg, data } = res;
            if (code === 200 && data === 1) {
              this.msgSuccess("课程信息新增成功");
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
              this.upload.file = [];
              this.getLessonList();
            } else {
              if (data === 0) {
                this.$message({
                  message: "新增失败，请先申请成为讲师",
                  type: "warning",
                  duration: 4000,
                });
                this.btnDisabled = false;
                this.btnLoading = false;
                this.handleDialogVisible = false;
                this.handleDialogForm = {};
                this.upload.file = [];
                this.getLessonList();
              } else {
                this.msgError("试卷信息新增失败：" + msg);
              }
            }
          }
        }
      });
    },
    /**
     * 加载列表
     */
    async getLessonList() {
      this.loading = true;
      let res = await getListLesson(this.queryLessonMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.lessonData = data.rows;
        this.lessonData.map((item) => {
          if (item.nickName === this.userInfo.nickName) {
            if (new Date(item.teachTime).getTime() == new Date().getTime()) {
              item.schooling = true;
            }
            // 结束时间<当前时间
            if (new Date(item.endTime).getTime() < new Date().getTime()) {
              item.schooling = false;
            }
          }
          if (item.status != 2) {
            item.status = item.status == 1;
          }
        });
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取课程列表错误：" + msg);
      }
    },
    uploadArticleImg() {},
    //加载分类数据
    async getCategoryList() {
      let res = await getListCategory(this.queryTypeMap);
      let { code, data, msg } = res;
      if (code === 200) {
        let lessonTypeOption = data.rows.map((item) => {
          return {
            name: item.name,
            id: item.id,
          };
        });
        this.lessonTypeOption = lessonTypeOption;
      } else {
        this.msgError("获取分类列表失败:" + msg);
      }
    },
    // 状态
    getLessonStatus(status) {
      switch (status) {
        case 0:
          return "禁用";
        case 1:
          return "启用中";
        case 2:
          return "课程已结束";
      }
    },
    // 授课
    handleSchooling(row) {
      let status = row.status;
      if (status != 2 && !status) {
        this.msgWarning("请先将课程启动！");
      }
    },
  },
  created() {
    this.getLessonList();
    this.getCategoryList();
    this.upload.fileType = ACCEPT_CONFIG.image.join(",");
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.user.user,
    }),
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
