<template>
  <div id="category">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>课程管理</el-breadcrumb-item>
      <el-breadcrumb-item>章节列表</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="box-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-button
            size="small"
            type="warning"
            icon="el-icon-circle-plus-outline"
            @click="handleOpenAdd"
            v-hasPermission="'business:chapter:add'"
            >添加</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格部分 -->
      <el-table
        :data="chapterTree"
        style="width: 100%; margin-top: 20px"
        height="500"
        v-loading="loading"
        border
        lazy
        row-key="id"
        :load="loadChildren"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column
          prop="title"
          label="课程标题"
          min-width="300"
          show-overflow-tooltip
        >
        </el-table-column>
        <el-table-column
          prop="sort"
          label="排序"
          min-width="180"
          align="center"
        >
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="modifiedTime"
          label="创建时间"
          align="center"
        ></el-table-column>
        <el-table-column label="操作" align="center">
          <!-- 操作 -->
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.parentId != 0"
              v-hasPermission="'business:chapter:edit'"
              type="primary"
              size="mini"
              icon="el-icon-edit"
              @click="handleOpenEdit(scope.row.id)"
            ></el-button>
            <el-button
              v-if="scope.row.parentId != 0"
              type="danger"
              size="mini"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.id)"
              v-hasPermission="'system:chapter:del'"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 10px"
        background
        :show-header="true"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryMap.pageNum"
        :page-sizes="[5, 10, 15, 20, 25, 30]"
        :page-size="queryMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>
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
            <el-form-item label="章节标题" prop="title">
              <el-input
                type="textarea"
                size="small"
                v-model="handleDialogForm.title"
                placeholder="请输入章节标题"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="封面" prop="coverImg">
              <el-upload
                :disabled="uploadImg.uploadDisabled"
                ref="uploadImg"
                :data="uploadImg.dataType"
                :action="uploadImg.api"
                :limit="uploadImg.limit"
                :headers="uploadImg.headers"
                :multiple="uploadImg.multiple"
                list-type="picture-card"
                :file-list="uploadImg.file"
                accept="image/gif,image/jpeg,image/jpg,image/bmp,image/png"
                :auto-upload="uploadImg.auto"
                :on-success="handleImgUploadSuccess"
              >
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="视频资源" prop="video">
              <el-upload
                :disabled="uploadVideo.uploadDisabled"
                ref="uploadVideo"
                :data="uploadVideo.dataType"
                :action="uploadVideo.api"
                :limit="uploadVideo.limit"
                :headers="uploadVideo.headers"
                :multiple="uploadVideo.multiple"
                list-type="picture-card"
                :file-list="uploadVideo.file"
                :accept="uploadVideo.fileType"
                :auto-upload="uploadVideo.auto"
                :on-success="handleVideoUploadSuccess"
              >
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="所属课程" prop="lesson">
              <el-select
                v-model="handleDialogForm.lesson"
                clearable
                placeholder="请选择分类"
                size="small"
              >
                <el-option
                  v-for="item in lessonList"
                  :key="item.name"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :gutter="11">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="handleDialogForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
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
  </div>
</template>

<script>
import {
  getChapterTree,
  getChapter,
  addChapter,
  updateChapter,
  delChapter,
  updateStatus,
} from "@/api/business/chapter";
import { getToken } from "@/utils/auth";

import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import { getListLesson } from "@/api/business/lesson/lesson";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      // 是否更新
      isUpdate: false,
      handleDialogTitle: "",
      handleDialogVisible: false,
      handleDialogForm: {}, //添加表单
      handleDialogFormRoles: {
        title: [
          { required: true, message: "请输入章节标题", trigger: "blur" },
          {
            min: 2,
            max: 10,
            message: "长度在 2 到 10 个字符",
            trigger: "blur",
          },
        ],
        lesson: [{ required: true, message: "请选择课程", trigger: "blur" }],
        sort: [{ required: true, message: "请输入排序号", trigger: "blur" }],
        status: [
          {
            required: true,
            message: "请选择状态",
            trigger: "blur",
          },
        ],
      },
      total: 0,
      queryMap: { pageNum: 1, pageSize: 5 },
      queryLessonMap: { pageNum: 1, pageSize: 100 },
      chapterTree: [],
      lessonList: [],
      uploadVideo: {
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
        fileType: "",
        headers: { Authorization: "Bearer " + getToken() },
      },
      uploadImg: {
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
        fileType: "",
        headers: { Authorization: "Bearer " + getToken() },
      },
      props: {
        stripe: false,
        border: true,
        showHeader: true,
        showSummary: false,
        showRowHover: true,
        showIndex: false,
        treeType: true,
        isFold: true,
        expandType: false,
        selectionType: false,
      },
      columns: [
        {
          label: "分类名称",
          prop: "title",
          align: "center",
        },
        {
          label: "排序",
          prop: "sort",
          align: "center",
        },
        {
          label: "创建时间",
          prop: "createTime",
          align: "center",
        },
        {
          label: "修改时间",
          prop: "modifiedTime",
          align: "center",
        },
        {
          label: "操作",
          type: "template",
          template: "operation",
          align: "center",
        },
      ],
    };
  },
  methods: {
    /**
     * 打开添加科目弹框
     */
    handleOpenAdd() {
      this.getLessonList();
      this.handleDialogTitle = "新增章节信息";
      this.isUpdate = false;
      this.handleDialogVisible = true;
    },
    loadChildren(tree, treeNode, resolve) {
      console.log(tree, treeNode, resolve);
    },
    async handleOpenEdit(id) {
      this.getLessonList();
      this.handleDialogTitle = "编辑章节信息";
      this.isUpdate = true;
      let res = await getChapter(id);
      let { code, data, msg } = res;
      if (code === 200) {
        this.handleDialogForm = data;
        this.uploadImg.file.push({
          url: data.coverImg,
        });
        this.handleDialogVisible = true;
      } else {
        this.msgInfo("获取章节信息失败！" + msg);
      }
    },
    getUploadFile(file) {
      this.handleDialogForm.video = file;
    },
    //关闭添加弹框
    handleClose() {
      this.handleDialogVisible = false;
      this.$refs.handleDialogFormRef.clearValidate();
      this.handleDialogForm = {};
      this.uploadImg.file = [];
      this.uploadVideo.file = [];
    },

    handleImgUploadSuccess(response, file, fileList) {
      this.uploadImg.uploadDisabled = true;
      this.handleDialogForm.coverImg = response.msg;
      this.uploadImg.file = [response.msg];
    },
    handleVideoUploadSuccess(response, file, fileList) {
      this.uploadVideo.uploadDisabled = true;
      this.handleDialogForm.video = response.msg;
      // this.uploadVideo.file = [process.env.VUE_APP_BASE_API + response.msg];
      this.uploadVideo.file = [response.msg];
    },
    //更新、新增
    async handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          if (this.isUpdate) {
            let id = this.handleDialogForm.id;
            let res = await updateChapter(id, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("章节信息更新成功");
              this.getChapterTreeList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("章节信息更新失败" + msg);
            }
          } else {
            let res = await addChapter(this.handleDialogForm);
            let { code, msg, data } = res;
            if (code === 200 && data === 1) {
              this.msgSuccess("章节信息添加成功");
              this.getChapterTreeList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgInfo("分类信息添加失败！" + msg);
            }
          }
        }
      });
    },
    //删除分类
    async handleDelete(id) {
      const res = await this.$confirm(
        "此操作将永久删除该分类, 是否继续?",
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
        let res = await delChapter(id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("章节数据删除成功");
          this.getChapterTreeList();
        } else {
          this.msgInfo("章节数据删除失败" + msg);
        }
      }
    },
    //加载章节树形数据
    async getChapterTreeList() {
      let res = await getChapterTree();
      let { code, data, msg } = res;
      if (code === 200) {
        this.chapterTree = data;
        this.total = data.total;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取章节列表失败:" + msg);
      }
    },
    async getLessonList() {
      let res = await getListLesson(this.queryLessonMap);
      let { code, data, msg } = res;
      if (code === 200) {
        this.lessonList = data.rows;
      } else {
        this.msgError("获取课程数据错误：" + msg);
      }
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getChapterTreeList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current;
      this.getChapterTreeList();
    },
  },
  created() {
    this.getChapterTreeList();
    this.uploadVideo.fileType = ACCEPT_CONFIG.video.join(",");
    this.uploadImg.fileType = ACCEPT_CONFIG.image.join(",");
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
