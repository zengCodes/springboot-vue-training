<template>
  <div id="question">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>题库管理</el-breadcrumb-item>
      <el-breadcrumb-item>题库列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="12">
        <el-col :span="5">
          <div class="article-status-menu">
            <span>题目分类</span>
            <span
              @click="selectStatus('choiceQuestion')"
              :class="isActive('choiceQuestion')"
              >选择题</span
            >
            <span
              @click="selectStatus('answerQuestion')"
              :class="isActive('answerQuestion')"
              >简答题</span
            >
          </div>
        </el-col>
        <el-col :span="4">
          <el-select
            size="small"
            v-model="queryQuestionMap.paperId"
            clearable
            placeholder="请选择试卷查询"
          >
            <el-option
              v-for="item in paperList"
              :key="item.id"
              :label="item.time + '-' + item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input
            size="small"
            clearable
            v-model="queryQuestionMap.name"
            placeholder="请输入试题名称查询"
            @clear="search"
            class="input-with-select"
          ></el-input>
        </el-col>
        <el-col :span="8">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-search"
            @click="search"
            >查找</el-button
          >
          <el-button
            size="mini"
            icon="el-icon-refresh-right"
            type="warning"
            @click="resetForm"
            >重置</el-button
          >
          <el-button
            size="mini"
            type="success"
            icon="el-icon-circle-plus-outline"
            @click="handleHandAdd"
            v-hasPermission="'choiceQuestion:add'"
            >添加</el-button
          >
          <el-button size="mini" icon="el-icon-refresh" @click="getQuestionList"
            >刷新</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <template v-if="currentType === 0">
        <el-table
          size="mini"
          v-loading="loading"
          border
          stripe
          :data="questionData"
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column
            prop="name"
            label="题目内容"
            mini-width="180"
          ></el-table-column>
          <el-table-column
            prop="selectA"
            label="选项A"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="selectB"
            label="选项B"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="selectC"
            label="选项C"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="selectD"
            label="选项D"
            align="center"
          ></el-table-column>
          <el-table-column label="正确答案" align="center">
            <template slot-scope="scope">
              <el-tag
                type="success"
                size="medium"
                closable
                v-text="scope.row.correctAnswer"
              ></el-tag>
            </template>
          </el-table-column>
          <el-table-column label="分数" align="center">
            <template slot-scope="scope">
              <el-tag
                type="success"
                size="medium"
                closable
                v-text="scope.row.score"
              ></el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="paperName"
            label="所属试卷"
            min-width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            align="center"
            min-width="120"
          ></el-table-column>
          <el-table-column label="操作" min-width="100">
            <template slot-scope="scope">
              <!-- 给操作员提供的编辑和回收-->
              <el-button
                v-hasPermission="'choiceQuestion:edit'"
                type="text"
                size="mini"
                icon="el-icon-edit"
                @click="handleOpenEdit(scope.row.id)"
                >编辑</el-button
              >
              <el-button
                v-hasPermission="'choiceQuestion:delete'"
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
      <template v-if="currentType === 1">
        <el-table
          size="mini"
          v-loading="loading"
          border
          stripe
          :data="questionData"
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column
            prop="name"
            label="题目内容"
            width="180"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column label="正确答案" width="180">
            <template slot-scope="scope">
              <el-tag
                type="success"
                size="medium"
                closable
                v-text="scope.row.correctAnswer"
              ></el-tag>
            </template>
          </el-table-column>
          <el-table-column label="分数" width="100">
            <template slot-scope="scope">
              <el-tag
                type="success"
                size="medium"
                closable
                v-text="scope.row.score"
              ></el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="paperName"
            label="所属试卷"
            min-width="180"
          ></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <!-- 给操作员提供的编辑和回收-->
              <el-button
                v-hasPermission="'choiceQuestion:edit'"
                type="text"
                size="mini"
                icon="el-icon-edit"
                @click="handleOpenEdit(scope.row.id)"
                >编辑</el-button
              >
              <el-button
                v-hasPermission="'choiceQuestion:delete'"
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
        :current-page="queryQuestionMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryQuestionMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
      <!-- 题目添加弹出框 -->
      <el-dialog
        :title="handleDialogTitle"
        :visible.sync="handleDialogVisible"
        width="50%"
        @close="handleClose"
        custom-class="paper-dialog"
      >
        <div class="scroll-main" v-if="currentType === 0">
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
                <el-form-item label="题干内容" prop="name">
                  <WangEditor
                    ref="wangEditor"
                    @changeContent="changeContent"
                    :placeholder="'请输入题干内容'"
                    :upload="upload"
                    :content="handleDialogForm.name"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="11">
                <el-form-item label="难度系数" prop="rate">
                  <el-rate
                    size="small"
                    v-model="handleDialogForm.rate"
                    show-score
                    text-color="#ff9900"
                    score-template="{value}"
                  >
                  </el-rate>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item label="分数" prop="score">
                  <el-input-number
                    v-model="handleDialogForm.score"
                    :min="1"
                    :max="100"
                    label="分数"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="11">
                <el-form-item label="试题类型" prop="typeId">
                  <el-select
                    v-model="handleDialogForm.typeId"
                    clearable
                    placeholder="请选择所属类型"
                    @change="typeChange"
                    size="small"
                  >
                    <el-option
                      v-for="item in typeList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item label="所属试卷" prop="paperId">
                  <el-select
                    v-model="handleDialogForm.paperId"
                    clearable
                    placeholder="请选择所属试卷"
                    size="small"
                  >
                    <el-option
                      v-for="item in paperList"
                      :key="item.id"
                      :label="item.time + '-' + item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="12">
                <el-form-item label="选项" required v-if="!isUpdate">
                  <template v-for="item in subject">
                    <el-form-item
                      :label="item.label"
                      :key="item.label"
                      label-width="40px"
                      class="question-item-label"
                    >
                      <el-input
                        size="small"
                        v-model="item.label"
                        style="width: 50px"
                      />
                      <el-input
                        size="small"
                        v-model="item.content"
                        @change="
                          selectChange($event, item.label, 'handleDialogForm')
                        "
                        class="subject-input"
                      />
                    </el-form-item>
                  </template>
                </el-form-item>
                <el-form-item label="选项" required v-if="isUpdate">
                  <el-form-item
                    label="A"
                    label-width="40px"
                    class="question-item-label"
                    prop="selectA"
                  >
                    <el-input value="A" style="width: 50px" disabled />
                    <el-input
                      v-model="handleDialogForm.selectA"
                      class="subject-input"
                    />
                  </el-form-item>
                  <el-form-item
                    label="B"
                    label-width="40px"
                    class="question-item-label"
                    prop="selectB"
                  >
                    <el-input value="B" style="width: 50px" disabled />
                    <el-input
                      v-model="handleDialogForm.selectB"
                      class="subject-input"
                    />
                  </el-form-item>
                  <el-form-item
                    label="C"
                    label-width="40px"
                    class="question-item-label"
                    prop="selectC"
                  >
                    <el-input value="C" style="width: 50px" disabled />
                    <el-input
                      v-model="handleDialogForm.selectC"
                      class="subject-input"
                    />
                  </el-form-item>
                  <el-form-item
                    label="D"
                    label-width="40px"
                    class="question-item-label"
                    prop="selectD"
                  >
                    <el-input value="D" style="width: 50px" disabled />
                    <el-input
                      v-model="handleDialogForm.selectD"
                      class="subject-input"
                    />
                  </el-form-item>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="11">
                <div class="grid-content bg-purple-light">
                  <el-form-item label="正确答案" prop="correctAnswer">
                    <el-radio-group v-model="handleDialogForm.correctAnswer">
                      <el-radio
                        v-for="item in subject"
                        :key="item.label"
                        :label="item.label"
                        >{{ item.label }}</el-radio
                      >
                    </el-radio-group>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="11">
                <el-col :span="10">
                  <el-form-item label="排序" prop="sort">
                    <el-input-number
                      size="small"
                      v-model="handleDialogForm.sort"
                      :min="1"
                      label="排序"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
              </el-col>
            </el-row>
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
          </el-form>
        </div>
        <div v-else>
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
                <el-form-item label="题干内容" prop="name">
                  <WangEditor
                    ref="wangEditor"
                    @changeContent="changeContent"
                    :placeholder="'请输入题干内容'"
                    :upload="upload"
                    :content="handleDialogForm.name"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="11">
                <el-form-item label="难度系数" prop="rate">
                  <el-rate
                    v-model="handleDialogForm.rate"
                    show-score
                    text-color="#ff9900"
                    score-template="{value}"
                  >
                  </el-rate>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="11">
                <el-form-item label="试题类型" prop="typeId">
                  <el-select
                    size="small"
                    v-model="handleDialogForm.typeId"
                    clearable
                    placeholder="请选择所属类型"
                    @change="typeChange"
                  >
                    <el-option
                      v-for="item in typeList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item label="排序" prop="sort">
                  <el-input-number
                    v-model="handleDialogForm.sort"
                    :min="1"
                    :max="10"
                    label="排序"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="11">
                <el-form-item label="所属试卷" prop="paperId">
                  <el-select
                    size="small"
                    v-model="handleDialogForm.paperId"
                    clearable
                    placeholder="请选择所属试卷"
                  >
                    <el-option
                      v-for="item in paperList"
                      :key="item.id"
                      :label="item.time + '-' + item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item label="分数" prop="score">
                  <el-input-number
                    v-model="handleDialogForm.score"
                    :min="1"
                    :max="100"
                    label="分数"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="22">
              <el-col :span="22">
                <div class="grid-content bg-purple-light">
                  <el-form-item label="正确答案" prop="correctAnswer">
                    <el-input
                      size="small"
                      type="textarea"
                      v-model="handleDialogForm.correctAnswer"
                    ></el-input>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <div class="footer-button">
              <el-button size="small" @click="handleClose">取 消</el-button>
              <el-button
                size="small"
                type="primary"
                @click="handleDialogSubmit"
                :disabled="btnDisabled"
                :loading="btnLoading"
                >确 定</el-button
              >
            </div>
          </span>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import {
  getListChoice,
  getChoice,
  updateChoice,
  addChoice,
  delChoice,
} from "@/api/business/question/choice";
import {
  getListAnswer,
  getAnswer,
  updateAnswer,
  addAnswer,
  delAnswer,
} from "@/api/business/question/answer";
import { getListPaper } from "@/api/business/paper";
import { getListCategory } from "@/api/business/category";
import WangEditor from "@/components/Editor/edit";
import { getToken } from "@/utils/auth";

export default {
  components: {
    WangEditor,
  },
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      paperList: [], // 试卷选择
      typeList: [], // 试卷类型
      subject: [
        {
          label: "A",
          content: "",
        },
        {
          label: "B",
          content: "",
        },
        {
          label: "C",
          content: "",
        },
        {
          label: "D",
          content: "",
        },
      ],
      handleDialogVisible: false, //添加弹框是否显示
      total: 0, //总共多少条数据
      questionData: [], //表格数据
      queryPaperMap: {
        pageNum: 1,
        pageSize: 6,
        courseId: null,
      }, //查询对象
      queryQuestionMap: {
        pageNum: 1,
        pageSize: 6,
        paper: null,
        name: null,
      },
      queryTypeMap: {
        pageNum: 1,
        pageSize: 100,
        type: "question",
      },
      handleDialogForm: {}, //添加表单数据
      limitCount: 1,
      dialogVisible: false,
      handleDialogFormRoles: {
        name: [{ required: true, message: "请输入题干内容", trigger: "blur" }],
        paperId: [
          { required: true, message: "请选择所属试卷", trigger: "blur" },
        ],
        rate: [{ required: true, message: "请选择难度系数", trigger: "blur" }],
        typeId: [
          { required: true, message: "请选择试题类型", trigger: "blur" },
        ],
        selectA: [
          { required: true, message: "请输入选项A内容", trigger: "blur" },
        ],
        selectB: [
          { required: true, message: "请输入选项B内容", trigger: "blur" },
        ],
        selectC: [
          { required: true, message: "请输入选项C内容", trigger: "blur" },
        ],
        selectD: [
          { required: true, message: "请输入选项D内容", trigger: "blur" },
        ],
        correctAnswer: [
          { required: true, message: "请输入正确答案", trigger: "blur" },
        ],
        score: [{ required: true, message: "请输入试题分数", trigger: "blur" }],
        sort: [
          { required: true, message: "请输入试题排序信息", trigger: "blur" },
        ],
      }, //添加验证
      upload: {
        dataType: {
          type: "Paper",
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
        headers: { Authorization: "Bearer " + getToken() },
      },
      paper: null, //搜索试卷
      typeId: null,
      // 表单标题
      handleDialogTitle: "",
      // 是否处于更新
      isUpdate: false,
      activeStatus: "",
      currentType: 0,
      // 查询函数
      selectFun: null,
      // 修改函数
      updateFun: null,
      // 编辑函数
      editFun: null,
      // 新增函数
      addFun: null,
      // 删除函数
      delFun: null,
    };
  },
  methods: {
    // 选择状态
    selectStatus(status) {
      switch (status) {
        case "choiceQuestion":
          this.currentType = 0;
          this.selectFun = getListChoice;
          this.updateFun = updateChoice;
          this.editFun = getChoice;
          this.addFun = addChoice;
          this.delFun = delChoice;
          break;
        case "answerQuestion":
          this.currentType = 1;
          this.selectFun = getListAnswer;
          this.updateFun = updateAnswer;
          this.editFun = getAnswer;
          this.addFun = addAnswer;
          this.delFun = delAnswer;
          break;
      }
      this.activeStatus = status;
      this.getQuestionList();
    },
    //重置查询表单
    resetForm() {
      this.queryQuestionMap = {
        pageNum: 1,
        pageSize: 6,
        paperId: null,
        name: null,
      };
    },
    changeContent(val) {
      this.handleDialogForm.name = val;
    },
    /**
     * 打开添加题目弹框
     */
    openAdd() {
      this.getPaperList();
      this.handleDialogVisible = true;
    },
    /**
     * 搜索科目
     */
    search() {
      this.queryQuestionMap.pageNum = 1;
      this.getQuestionList();
    },
    typeChange(val) {
      this.typeId = val;
    },
    selectChange(e, val, param) {
      if (param == "handleDialogForm") {
        switch (val) {
          case "A":
            this.handleDialogForm.selectA = e;
            break;
          case "B":
            this.handleDialogForm.selectB = e;
            break;
          case "C":
            this.handleDialogForm.selectC = e;
            break;
          case "D":
            this.handleDialogForm.selectD = e;
            break;
          default:
            break;
        }
      } else {
        switch (val) {
          case "A":
            this.editRuleForm.selectA = e;
            break;
          case "B":
            this.editRuleForm.selectB = e;
            break;
          case "C":
            this.editRuleForm.selectC = e;
            break;
          case "D":
            this.editRuleForm.selectD = e;
            break;
          default:
            break;
        }
      }
    },
    /**
     * 打开添加弹框
     */
    handleHandAdd() {
      this.handleDialogTitle = "添加题目信息";
      this.isUpdate = false;
      this.handleDialogVisible = true;
    },
    // 提交
    handleDialogSubmit() {
      let id = this.handleDialogForm.id;
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          if (this.isUpdate) {
            this.updateFun
              .call(this, id, this.handleDialogForm)
              .then((response) => {
                let { code, msg } = response;
                if (code === 200) {
                  this.msgSuccess("题目信息更新成功");
                  this.getQuestionList();
                  this.btnDisabled = false;
                  this.btnLoading = false;
                  this.handleDialogVisible = false;
                  this.handleDialogForm = {};
                } else {
                  this.msgError("题目信息更新失败" + msg);
                }
              });
          } else {
            let res = await this.addFun.call(this, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("题目信息新增成功");
              this.getQuestionList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("题目信息新增失败" + msg);
            }
          }
        }
      });
    },
    //打开修改
    async handleOpenEdit(id) {
      this.handleDialogTitle = "更新题目信息";
      this.isUpdate = true;
      let res = await this.editFun.call(this, id);
      let { code, data } = res;
      if (code === 200) {
        this.handleDialogForm = data;
        this.handleDialogVisible = true;
      }
    },
    /**
     * 删除
     */
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该试题, 是否继续?",
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
        let res = await this.delFun.call(this, id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("题目删除成功");
          this.getQuestionList();
        } else {
          this.msgInfo("题目删除失败：" + msg);
        }
      }
    },
    /**
     * 加载试题列表
     */
    async getQuestionList() {
      this.loading = true;
      let res = await this.selectFun.call(this, this.queryQuestionMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.questionData = data.rows;
        this.questionData.map((item) => {
          item.name = item.name
            .replace(/<[^>]*>/g)
            .replaceAll("undefined", "")
            .replaceAll("&nbsp;", "");
        });
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgInfo("获取题目数据错误：" + msg);
      }
    },
    /**
     * 加载试卷
     */
    async getPaperList() {
      let res = await getListPaper(this.queryPaperMap);
      let { code, data } = res;
      if (code === 200) {
        this.paperList = data.rows;
      }
    },
    //加载分类数据
    async getCategoryList() {
      let res = await getListCategory(this.queryTypeMap);
      let { code, data, msg } = res;
      if (code === 200) {
        let typeList = data.rows.map((item) => {
          return {
            name: item.name,
            id: item.id,
          };
        });
        this.typeList = typeList;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取分类列表失败:" + msg);
      }
    },

    //关闭弹出框
    handleClose() {
      this.handleDialogVisible = false;
      this.$refs.handleDialogFormRef.clearValidate();
      this.handleDialogForm = {};
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
      this.queryQuestionMap.pageSize = newSize;
      this.getQuestionList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryQuestionMap.pageNum = current;
      this.getQuestionList();
    },
    handleSuccess(response, file, fileList) {
      this.handleDialogForm.imageUrl = response.msg;
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    },
  },
  created() {
    this.selectStatus("choiceQuestion");
    this.getQuestionList();
    this.getPaperList();
    this.getCategoryList();
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
.subject-input {
  margin-left: 15px;
  width: 70%;
}
.article-status-menu {
  font-size: 14px;
  margin-top: 10px;
  margin-bottom: 20px;
  color: #999;
}
.article-status-menu span {
  margin-right: 24px;
}
.status {
  cursor: pointer;
}
.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}
::v-deep .article {
  .article-cover {
    position: relative;
    width: 100%;
    height: 90px;
    border-radius: 4px;
  }
  .article-cover::after {
    content: "";
    background: rgba(0, 0, 0, 0.3);
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
  }
  .article-status-icon {
    color: #fff;
    font-size: 14px;
    position: absolute;
    right: 20px;
    bottom: 10px;
  }
}
</style>
