<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>科目管理</el-breadcrumb-item>
      <el-breadcrumb-item>科目列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-container style="margin-bottom: 20px">
        <el-alert
          show-icon
          title="友情提示:科目的分类只支持三级分类"
          type="warning"
        ></el-alert>
      </el-container>
      <el-row :gutter="6">
        <el-col :span="5">
          <el-cascader
            size="small"
            placeholder="请选择分类查询"
            :change-on-select="true"
            @change="selectChange"
            v-model="categorykeys"
            :props="searchSelectProps"
            :options="cateories"
            clearable
          ></el-cascader>
        </el-col>
        <el-col :span="6">
          <el-input
            clearable
            size="small"
            v-model="queryMap.name"
            placeholder="请输入科目名称查询"
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
          <el-button
            size="small"
            type="success"
            icon="el-icon-circle-plus-outline"
            @click="handleOpenAdd"
            v-hasPermission="['business:course:add']"
            >添加</el-button
          >
          <el-button size="small" icon="el-icon-refresh" @click="getCourseList"
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
          :data="courseData"
          style="width: 100%; margin-top: 20px"
          height="450"
        >
          <el-table-column
            label="科目封面图"
            min-width="110"
            prop="img"
            align="center"
          >
            <template slot-scope="scope">
              <el-image
                style="width: 110px; height: 110px"
                :src="scope.row.img"
                :preview-src-list="[scope.row.img]"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </template>
          </el-table-column>

          <el-table-column
            prop="name"
            label="科目名称"
            width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="annual"
            label="科目年度时间"
            width="120"
            align="center"
          >
          </el-table-column>
          <el-table-column label="科目考试时间" width="200" align="center">
            <template slot-scope="scope">
              <el-tag
                type="success"
                size="mini"
                closable
                v-text="scope.row.time"
              ></el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="remark"
            label="备注"
            width="200"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag
                size="mini"
                type="danger"
                effect="plain"
                v-if="scope.row.status == 0"
                >锁定</el-tag
              >
              <el-tag size="mini" effect="plain" v-if="scope.row.status == 1"
                >开放</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <!--给管理员提供的恢复和删除-->
              <span v-if="scope.row.status == 0">
                <el-popconfirm
                  @confirm="handleChangeState(scope.row)"
                  style="margin-left: 10px"
                  confirmButtonText="好的"
                  cancelButtonText="不用了"
                  icon="el-icon-info"
                  iconColor="green"
                  title="确定从回收站中恢复吗？"
                >
                  <el-button
                    type="text"
                    size="mini"
                    slot="reference"
                    icon="el-icon-back"
                    >恢复</el-button
                  >
                </el-popconfirm>
                <el-button
                  style="margin-left: 10px"
                  type="text"
                  size="mini"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row.id)"
                  >删除</el-button
                >
              </span>

              <!-- 给操作员提供的编辑和回收-->
              <span v-if="scope.row.status == 1">
                <el-button
                  v-hasPermission="['business:course:edit']"
                  type="text"
                  size="mini"
                  icon="el-icon-edit"
                  @click="handleOpenEdit(scope.row.id)"
                  >编辑</el-button
                >
                <el-popconfirm
                  @confirm="handleChangeState(scope.row)"
                  style="margin-left: 10px"
                  confirmButtonText="好的"
                  cancelButtonText="不用了"
                  icon="el-icon-info"
                  iconColor="red"
                  title="确定移入回收站吗？"
                >
                  <el-button
                    type="text"
                    slot="reference"
                    size="mini"
                    icon="el-icon-s-operation"
                    >回收站</el-button
                  >
                </el-popconfirm>
              </span>
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
      <!-- 添加、编辑弹出框 -->
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
          <el-form-item label="科目名称" prop="name">
            <el-input
              v-model="handleDialogForm.name"
              size="small"
              placeholder="请输入科目名称"
            ></el-input>
          </el-form-item>
          <el-row :gutter="22">
            <el-col :span="22">
              <el-form-item label="封面LOGO" prop="logo">
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
                  :accept="upload.fileType"
                  :auto-upload="upload.auto"
                  :on-remove="handleRemove"
                  :on-exceed="handleExceed"
                  :on-success="handleImgUploadSuccess"
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="分类" prop="categoryKeys">
                <el-cascader
                  size="small"
                  :options="cateories"
                  clearable
                  filterable
                  :props="selectProps"
                  v-model="handleDialogForm.categoryKeys"
                  placeholder="请选择科目类别"
                ></el-cascader>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <div class="grid-content bg-purple-light">
                <el-form-item label="排序" prop="sort">
                  <el-input-number
                    v-model="handleDialogForm.sort"
                    :min="1"
                    label="排序"
                    size="small"
                  ></el-input-number>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <div class="grid-content bg-purple">
                <el-form-item label="考试时间" prop="time">
                  <el-date-picker
                    size="small"
                    v-model="handleDialogForm.time"
                    type="date"
                    placeholder="请选择日期"
                    value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>
            </el-col>
          </el-row>

          <el-form-item label="备注信息" prop="remark">
            <el-input
              size="small"
              type="textarea"
              v-model="handleDialogForm.remark"
              placeholder="请输入备注信息"
            ></el-input>
          </el-form-item>
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
    </el-card>
  </div>
</template>

<script>
import {
  getListCourse,
  getCourse,
  delCourse,
  addCourse,
  updateCourse,
  changCourseStatus,
} from "@/api/business/course";
import { getToken } from "@/utils/auth";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import { getListCategory } from "@/api/business/category";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      handleDialogTitle: "",
      cateories: [], //类别选择
      selectProps: {
        expandTrigger: "hover",
        value: "id",
        label: "name",
        children: "children",
        checkStrictly: false,
      }, //级联选择器配置项
      searchSelectProps: {
        expandTrigger: "hover",
        value: "id",
        label: "name",
        children: "children",
        checkStrictly: true,
      }, //级联搜索选择器配置项
      //弹框是否显示
      handleDialogVisible: false,
      total: 0, //总共多少条数据
      courseData: [], //表格数据
      queryMap: {
        pageNum: 1,
        pageSize: 6,
        name: null,
        categoryId: null,
      }, //查询对象
      handleDialogForm: {}, //表单数据
      uploadDisabled: false,
      limitCount: 1,
      dialogImageUrl: "",
      dialogVisible: false,
      handleDialogFormRoles: {
        name: [
          { required: true, message: "请输入科目名称", trigger: "blur" },
          {
            min: 2,
            max: 10,
            message: "长度在 2 到 10 个字符",
            trigger: "blur",
          },
        ],
        year: [
          { required: true, message: "请输入科目年份", trigger: "blur" },
          {
            min: 1,
            max: 10,
            message: "长度在 1 到 10 个字符",
            trigger: "blur",
          },
        ],
        time: [
          { required: true, message: "请选择科目考试时间", trigger: "blur" },
        ],
        remark: [
          { required: true, message: "请输入科目备注说明", trigger: "blur" },
        ],
        categorys: [
          { required: true, message: "请输入科目分类", trigger: "blur" },
        ],
        sort: [
          { required: true, message: "请输入科目排序信息", trigger: "blur" },
        ],
        categoryKeys: [
          { required: true, message: "请选择科目分类", trigger: "blur" },
        ],
      }, //添加验证
      imgFilesList: [],
      categorykeys: [], //搜索类别
      // 是否处于更新
      isUpdate: false,
      upload: {
        dataType: {
          type: "course",
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
    };
  },
  methods: {
    //重置查询表单
    resetForm() {
      this.queryMap = {
        pageNum: 1,
        pageSize: 6,
        name: null,
        categoryId: null,
      };
    },
    /**
     * 搜索科目
     */
    search() {
      this.queryMap.pageNum = 1;
      this.getCourseList();
    },
    /**
     * 打开添加科目弹框
     */
    handleOpenAdd() {
      this.getCourseTree();
      this.handleDialogTitle = "新增科目信息";
      this.isUpdate = false;
      this.handleDialogVisible = true;
    },
    // 图片上传
    handleImgUploadSuccess(response, file, fileList) {
      this.upload.uploadDisabled = true;
      this.handleDialogForm.img = response.msg;
      this.upload.file.push({
        url: response.msg,
      });
    },
    handleRemove(file, fileList) {
      this.upload.uploadDisabled = fileList.length >= this.limitCount;
      this.upload.file = fileList;
      this.handleDialogForm.img = [];
      console.log(this.upload.file);
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    async handleOpenEdit(id) {
      this.getCourseTree();
      this.handleDialogTitle = "编辑科目信息";
      this.isUpdate = true;
      let res = await getCourse(id);
      let { code, data, msg } = res;
      if (code === 200) {
        this.handleDialogForm = data;
        this.upload.file.push({
          url: data.img,
        });
        const array = [];
        array.push(data.oneCategoryId);
        array.push(data.twoCategoryId);
        this.handleDialogForm.categoryKeys = array;
        this.handleDialogVisible = true;
      } else {
        this.msgInfo("获取科目信息失败！" + msg);
      }
    },
    //关闭添加弹框
    handleClose() {
      this.handleDialogVisible = false;
      this.$refs.handleDialogFormRef.clearValidate();
      this.handleDialogForm = {};
      this.upload.file = [];
    },

    /**
     * 删除科目
     */
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该科目, 是否继续?",
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
        let res = await delCourse(id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("科目删除成功");
          this.getCourseList();
        } else {
          this.msgInfo("科目删除失败" + msg);
        }
      }
    },
    //更新、新增角色
    handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          let id = this.handleDialogForm.id;
          if (this.isUpdate) {
            let res = await updateCourse(id, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("角色信息更新成功");
              this.getCourseList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("科目信息更新失败：" + msg);
            }
          } else {
            let res = await addCourse(this.handleDialogForm);
            let { code, msg, data } = res;
            if (code === 200) {
              let msg = "";
              if (data === 1) {
                msg = "科目添加成功";
              } else {
                msg = "科目重复，不能重复添加";
              }
              this.msgSuccess(msg);
              this.getCourseList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgInfo("科目添加失败！" + msg);
            }
          }
        }
      });
    },
    /**
     * 移入回收站
     */
    async handleChangeState(row) {
      const text = row.status === 1 ? "已移入回收站" : "已从回收站中恢复";
      row.status == 1 ? (row.status = 0) : (row.status = 1);
      let res = await changCourseStatus(row.id, row);
      let { code, msg } = res;
      if (code === 200) {
        this.msgSuccess(text);
        this.btnDisabled = false;
        this.btnLoading = false;
        this.getCourseList();
        this.handleDialogVisible = false;
        this.handleDialogForm = {};
      } else {
        this.msgError("操作失败：" + msg);
      }
    },
    /**
     * 加载科目列表
     */
    async getCourseList() {
      this.loading = true;
      let res = await getListCourse(this.queryMap);
      let { code, data, msg } = res;
      if (code === 200) {
        this.total = data.total;
        this.courseData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取科目列表失败:" + msg);
      }
    },
    /**
     * 加载科目类别
     */
    async getCourseTree() {
      let res = await getListCategory({
        type: "course",
      });
      let { data, code, msg } = res;
      if (code === 200) {
        this.cateories = data.rows;
      } else {
        this.msgInfo("获取科目类别失败" + msg);
      }
    },
    handleChange(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
    },
    handleRemove(file, fileList) {
      this.upload.uploadDisabled = fileList.length >= this.limitCount;
      this.upload.file = fileList;
      this.handleDialogForm.logo = [];
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getCourseList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current;
      this.getCourseList();
    },
    handleSuccess(response, file, fileList) {
      this.handleDialogForm.imageUrl = response.msg;
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    //编辑
    editHandleSuccess(response, file, fileList) {
      this.handleDialogForm.imageUrl = response.msg;
    },
    /**
     * 分类搜索改变时
     */
    selectChange() {
      var str = "";
      for (var i = 0; i < this.categorykeys.length; i++) {
        str += this.categorykeys[i] + ",";
      }
      str = str.substring(0, str.length - 1);
      this.queryMap.categorys = str;
    },
  },
  created() {
    this.getCourseList();
    this.getCourseTree();
    this.upload.fileType = ACCEPT_CONFIG.image.join(",");
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
