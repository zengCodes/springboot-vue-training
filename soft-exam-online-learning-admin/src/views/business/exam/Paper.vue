<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>试卷管理</el-breadcrumb-item>
      <el-breadcrumb-item>试卷列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="6">
        <el-col :span="5">
          <el-select
            size="small"
            v-model="courseId"
            clearable
            placeholder="请选择科目查询"
            @change="courseChange"
          >
            <el-option
              v-for="item in courseOption"
              :key="item.id"
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
          <el-dropdown class="add-menu">
            <el-button
              size="small"
              type="success"
              icon="el-icon-circle-plus-outline"
            >
              添加<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <el-button
                  size="small"
                  type="text"
                  icon="el-icon-edit"
                  v-hasPermission="'business:paper:add'"
                  @click="handleHandAdd"
                  >手动添加</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item>
                <el-button
                  size="small"
                  type="text"
                  icon="el-icon-upload"
                  v-hasPermission="'business:paper:add'"
                  @click="handleWordAdd('morning')"
                  >word文档导入上午题</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item>
                <el-button
                  size="small"
                  type="text"
                  icon="el-icon-upload"
                  v-hasPermission="'business:paper:add'"
                  @click="handleWordAdd('afternoon')"
                  >word文档导入下午题</el-button
                >
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="small" icon="el-icon-refresh" @click="getPaperList"
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
        :data="paperData"
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column
          prop="id"
          label="试卷id"
          align="center"
          min-width="100"
        >
        </el-table-column>
        <el-table-column
          prop="name"
          label="试卷名称"
          align="center"
          min-width="100"
        ></el-table-column>
        <el-table-column
          prop="user"
          label="创建者"
          align="center"
          min-width="100"
        ></el-table-column>
        <el-table-column
          prop="course"
          label="所属科目"
          min-width="110"
          align="center"
        ></el-table-column>
        <el-table-column label="试卷总分" align="center">
          <template slot-scope="scope">
            <el-tag
              type="success"
              size="mini"
              closable
              v-text="scope.row.score"
            ></el-tag>
          </template>
        </el-table-column>
        <el-table-column label="考试时长限制" align="center" min-width="110">
          <template slot-scope="scope">
            <el-tag
              type="success"
              size="mini"
              closable
              v-text="scope.row.suggestTime + '分钟'"
            ></el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          align="center"
          label="是否开放"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              @change="changPaperStatus(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column
          prop="time"
          label="考试时间"
          align="center"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          min-width="150"
          align="center"
          sortable
        ></el-table-column>
        <el-table-column label="操作" min-width="180" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="mini"
              icon="el-icon-edit"
              @click="handleOpenEdit(scope.row.id)"
              >编辑试卷</el-button
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
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 10px"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryPaperMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryPaperMap.pageSize"
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
        :rules="handleDialogFormRoles"
        ref="handleDialogFormRef"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="创建人" prop="user">
              <el-input
                size="small"
                v-model="handleDialogForm.user"
                disabled
                placeholder="请输入创建人"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="试卷名称" prop="name">
              <el-input
                placeholder="请输入试卷名称"
                size="small"
                v-model="handleDialogForm.name"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属科目" prop="courseId">
              <el-select
                v-model="handleDialogForm.courseId"
                clearable
                placeholder="请选择科目"
                @change="courseChange"
                size="small"
              >
                <el-option
                  v-for="item in courseOption"
                  :key="item.id"
                  :label="item.time + '-' + item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <div class="grid-content bg-purple-light">
              <el-form-item label="总分" prop="score">
                <el-input-number
                  v-model="handleDialogForm.score"
                  :min="1"
                  :max="100"
                  label="总分"
                  size="small"
                ></el-input-number>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="考试时间" prop="time">
                <el-date-picker
                  v-model="handleDialogForm.time"
                  type="date"
                  placeholder="请选择考试时间"
                  value-format="yyyy年MM月dd日"
                  size="small"
                ></el-date-picker>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="试题数目" prop="questionCount">
                <el-input-number
                  v-model="handleDialogForm.questionCount"
                  :min="0"
                  :max="100"
                  label="试题数目"
                  size="small"
                ></el-input-number>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item
                label="考试时长（分钟）"
                prop="suggestTime"
                label-width="120"
              >
                <el-input-number
                  v-model="handleDialogForm.suggestTime"
                  :min="60"
                  label="考试时长"
                  size="small"
                ></el-input-number>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24" v-if="isUpdate">
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
      <div class="dialog-footer">
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
    </el-dialog>
    <!-- 图片预览 -->
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>
    <!-- word文档导入弹窗 -->
    <el-dialog
      :title="exportWordTitle"
      width="45%"
      @close="handleClose"
      :visible.sync="addWordImportVisible"
      custom-class="paper-dialog"
    >
      <div class="import-tip">
        用word文档导入试题，需要提前<el-link
          type="primary"
          @click="downloadTemplate"
          >下载Word模板</el-link
        >,再把数据填充导入,（请严格按照格式填充数据）
      </div>
      <el-upload
        ref="importWord"
        action="#"
        :multiple="false"
        accept=".doc, .docx"
        list-type="picture-card"
        :file-list="wordFileList"
        :auto-upload="false"
        :limit="1"
        :on-success="handleImportSuccess"
        :on-preview="handleImportPreview"
        :on-remove="handleImportRemove"
        :on-change="handleImportWord"
      >
        <i slot="default" class="el-icon-plus"></i>
        <div slot="file" slot-scope="{ file }">
          <i class="iconfont icon-word"></i>
          <span class="el-upload-list__item-actions">
            <span
              v-if="isUpload"
              class="el-upload-list__item-delete"
              @click="handleImportRemove(file)"
            >
              <i class="el-icon-delete"></i>
            </span>
          </span>
        </div>
      </el-upload>

      <div class="dialog-footer">
        <div class="footer-button">
          <el-button
            size="small"
            :type="importantSuccess ? 'success' : 'primary'"
            @click="importSubmit"
            :disabled="dialogDisabled"
            :loading="dialogLoading"
            v-html="importantSuccess ? '点 击 预 览' : '确 定 导 入'"
          ></el-button>
        </div>
      </div>
      <el-dialog
        width="50%"
        height="700"
        title="预览"
        :visible.sync="previewWordVisible"
        append-to-body
        custom-class="word-dialog"
      >
        <div ref="wordRef" class="word-main"></div>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import { Loading } from "element-ui";
let loading;
import {
  getListPaper,
  getPaper,
  importMorningPracticeWord,
  importAfternoonPracticeWord,
  downloadTemplate,
  updatePaperStatus,
  addPaper,
  updatePaper,
  delPaper,
} from "@/api/business/paper";
import { getCourseList } from "@/api/business/course";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      courseOption: [], //科目选择
      handleDialogVisible: false, //添加弹框是否显示
      // word文档导入弹窗
      addWordImportVisible: false,
      total: 0, //总共多少条数据
      paperData: [], //表格数据
      queryMap: {
        pageNum: 1,
        pageSize: 100,
        paperId: null,
      },
      queryPaperMap: {
        pageNum: 1,
        pageSize: 6,
        courseId: null,
      }, //查询对象
      queryCourseMap: {
        pageNum: 1,
        pageSize: 6,
      },
      handle: "",
      handleDialogForm: {}, //表单数据
      uploadDisabled: false,
      limitCount: 1,
      dialogImageUrl: "",
      dialogVisible: false,
      handleDialogFormRoles: {
        name: [
          { required: true, message: "请输入试卷名称", trigger: "blur" },
          {
            min: 2,
            max: 10,
            message: "长度在 2 到 10 个字符",
            trigger: "blur",
          },
        ],
        score: [{ required: true, message: "请输入试卷总分", trigger: "blur" }],
        time: [{ required: true, message: "请选择考试时间", trigger: "blur" }],
        courseId: [
          { required: true, message: "请选择考试科目", trigger: "change" },
        ],
        suggestTime: [
          { required: true, message: "请输入考试时长", trigger: "blur" },
        ],
      }, //添加验证
      courseId: "", //搜索科目
      addType: null,
      addCallback: null,
      // 确定导入
      dialogLoading: false,
      dialogDisabled: false,
      // 是否更新
      isUpdate: false,
      // 是否选择文件
      isUpload: false,
      // 是否导入成功
      importantSuccess: false,
      // word文档文件
      wordFileList: [],
      // 当前导入word文件
      currentWordData: null,
      // 预览word
      previewWordVisible: false,
      // 标题
      handleDialogTitle: "",
      exportWordTitle: "",
    };
  },
  methods: {
    //重置查询表单
    resetForm() {
      this.courseId = null;
      this.queryPaperMap = {
        pageNum: 1,
        pageSize: 6,
        courseId: null,
      };
    },
    handleWordAdd(type) {
      if (type === "morning") {
        this.exportWordTitle = "word导入上午题试卷";
      } else {
        this.exportWordTitle = "word导入下午题试卷";
      }
      this.addWordImportVisible = true;
      this.addType = type;
    },
    handleImportSuccess() {},
    handleImportPreview() {},
    handleImportRemove() {},
    handleImportWord(file, fileList) {
      this.isUpload = true;
      this.wordFileList.push(file.raw);
    },
    /**
     * 打开添加试卷弹框
     */
    handleHandAdd() {
      this.handleDialogTitle = "手动添加试卷";
      this.isUpdate = false;
      this.handleDialogForm.user = this.$store.state.user.name;
      this.handleDialogVisible = true;
    },
    /**
     * 搜索科目
     */
    search() {
      this.queryPaperMap.pageNum = 1;
      this.getPaperList();
    },
    courseChange(val) {
      this.queryPaperMap.courseId = val;
    },
    // 查看试卷
    showPaper() {
      this.$router.push({
        path: "/practice",
      });
      this.$store.dispatch("setPaperData", this.handleDialogForm);
    },
    handleChange(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
    },
    handleRemove(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
      this.handleDialogForm.imageUrl = "";
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryPaperMap.pageSize = newSize;
      this.getPaperList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryPaperMap.pageNum = current;
      this.getPaperList();
    },
    //关闭弹出框
    handleClose() {
      if (!this.isUpdate) {
        this.$nextTick(() => {
          if (this.$refs.hasOwnProperty("handleDialogFormRef")) {
            this.$refs.handleDialogFormRef.clearValidate();
          }
          if (this.$refs.hasOwnProperty("importWord")) {
            this.$refs.importWord.clearFiles();
          }
          this.addWordImportVisible = false;
        });
      }
      this.handleDialogVisible = false;
      this.handleDialogForm = {};
      this.$refs?.handleDialogFormRef.clearValidate();
    },
    // 开放试卷选择器
    async changPaperStatus(row) {
      let res = await updatePaperStatus(row.id, row.status);
      let { code, msg } = res;
      if (code === 200) {
        this.msgSuccess("更新状态成功");
      } else {
        row.status = !row.status;
        this.msgInfo("更新状态失败:" + msg);
      }
    },
    /**
     * 删除试卷
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
        let res = await delPaper(id);
        let { code, msg, data } = res;
        if (code === 200) {
          if (data === 1) {
            this.msgSuccess("试卷删除成功");
            this.getPaperList();
          } else {
            this.msgWarning("试卷下含有试题，不能删除！");
          }
        } else {
          this.msgInfo("试卷删除失败：" + msg);
        }
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
            let res = await updatePaper(id, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("试卷信息更新成功");
              this.getPaperList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("试卷信息更新失败" + msg);
            }
          } else {
            let res = await addPaper(this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("试卷信息新增成功");
              this.getPaperList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("试卷信息新增失败" + msg);
            }
          }
        }
      });
    },
    /**
     * 编辑试卷
     */
    async handleOpenEdit(id) {
      this.handleDialogTitle = "更新试卷信息";
      this.isUpdate = true;
      let res = await getPaper(id);
      let { code, data } = res;
      if (code === 200) {
        this.handleDialogForm = data;
        const array = [];
        array.push(data.oneCategoryId);
        array.push(data.twoCategoryId);
        array.push(data.threeCategoryId);
        this.handleDialogForm.categoryKeys = array;
        this.handleDialogVisible = true;
      }
    },
    /**
     * 加载试卷列表
     */
    async getPaperList() {
      this.loading = true;
      let res = await getListPaper(this.queryPaperMap);
      let { code, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.paperData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    },
    /**
     * 查询科目
     */
    async getCourses() {
      let res = await getCourseList(this.queryCourseMap);
      let { code, data, msg } = res;
      if (code === 200) {
        this.courseOption = data.rows;
      } else {
        this.msgError("获取科目失败:" + msg);
      }
    },

    // 下载word模板
    async downloadTemplate() {
      let res = await downloadTemplate({
        type: this.addType,
      });
      let blob = new Blob([res], {
        type: "application/msword;charset=utf-8",
      });
      let objectUrl = URL.createObjectURL(blob);
      window.location.href = objectUrl;
    },
    //  确定导入
    async importSubmit() {
      if (this.importantSuccess) {
        loading = Loading.service({
          //开始loading加载动画
          lock: true,
          text: "试卷加载中...",
          background: "rgba(0, 0, 0, 0)",
        });
        setTimeout(() => {
          loading.close();
        }, 500);
        setTimeout(() => {
          this.$router.push({
            path: "/practice",
          });
          console.log(this.currentWordData);
          this.$store.dispatch("setPaperData", this.currentWordData);
        }, 1000);
      } else {
        this.dialogLoading = true;
        this.dialogDisabled = true;
        const res = await this.$confirm("是否导入该试卷?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }).catch(() => {
          this.$message({
            type: "info",
            message: "已取消导入",
          });
        });
        if (res === "confirm") {
          let formData = new FormData();
          formData.append("wordTemplate", this.wordFileList[0]);
          let res = await this.addCallback.call(this, formData);
          let { data, code, msg } = res;
          if (code === 200) {
            this.msgSuccess("试卷导入成功！");
            this.currentWordData = data;
            this.importantSuccess = true;
            this.dialogLoading = false;
            this.dialogDisabled = false;
          } else {
            this.msgError("导入试卷出错：" + msg);
          }
        }
      }
    },
    /**
     * 所属科目改变时
     */
    selectChange() {
      var str = "";
      for (var i = 0; i < this.courseId.length; i++) {
        str += this.courseId[i] + ",";
      }
      str = str.substring(0, str.length - 1);
      this.queryPaperMap.courseId = str;
    },
  },
  watch: {
    addType: {
      handler(val) {
        switch (val) {
          case "morning":
            this.addCallback = importMorningPracticeWord;
            break;
          case "afternoon":
            this.addCallback = importAfternoonPracticeWord;
            break;
          default:
            this.addCallback = null;
            break;
        }
      },
    },
  },
  created() {
    this.getPaperList();
    this.getCourses();
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
