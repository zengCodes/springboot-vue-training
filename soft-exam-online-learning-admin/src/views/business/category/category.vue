<template>
  <div id="category">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>项目类别管理</el-breadcrumb-item>
      <el-breadcrumb-item>类别列表</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="box-card">
      <el-row :gutter="20">
        <el-col :span="15">
          <div class="article-status-menu">
            <span>分类栏目</span>
            <span @click="selectStatus('course')" :class="isActive('course')"
              >课程分类</span
            >
            <span @click="selectStatus('article')" :class="isActive('article')"
              >文章分类</span
            >
            <span
              @click="selectStatus('question')"
              :class="isActive('question')"
              >题目分类</span
            >
            <span @click="selectStatus('lesson')" :class="isActive('lesson')"
              >前台课程分类</span
            >
            <span
              @click="selectStatus('resource')"
              :class="isActive('resource')"
              >资源分类</span
            >
            <span @click="selectStatus('time')" :class="isActive('time')"
              >时间分类</span
            >
            <span @click="selectStatus('others')" :class="isActive('others')">
              其他分类
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-button
            v-if="currentType === 0"
            size="small"
            type="warning"
            icon="el-icon-circle-plus-outline"
            @click="handleOpenAdd"
            v-hasPermission="['business:category:add']"
            >添加科目类别</el-button
          >
          <el-button
            v-else
            size="small"
            type="warning"
            icon="el-icon-circle-plus-outline"
            @click="handleOpenAdd"
            v-hasPermission="['business:category:add']"
            >添加其他类别</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格部分 -->
      <zk-table
        v-if="currentType === 0"
        v-loading="loading"
        style="margin-top: 10px"
        ref="treeTable"
        :stripe="props.stripe"
        :border="props.border"
        :show-header="props.showHeader"
        :show-summary="props.showSummary"
        :show-row-hover="props.showRowHover"
        :show-index="props.showIndex"
        :tree-type="props.treeType"
        :is-fold="props.isFold"
        :expand-type="props.expandType"
        :selection-type="props.selectionType"
        sum-text="sum"
        :data="categorys"
        :columns="currentColumns"
      >
        <!-- 层级 -->
        <template slot="lev" slot-scope="scope">
          <el-tag v-if="scope.row.lev === 1">一级分类</el-tag>
          <el-tag type="success" v-else-if="scope.row.lev === 2"
            >二级分类</el-tag
          >
          <el-tag type="danger" v-else>三级分类</el-tag>
        </template>
        <!-- 操作 -->
        <template slot="caozuo" slot-scope="scope">
          <el-button
            v-hasPermission="['business:category:edit']"
            type="primary"
            size="mini"
            icon="el-icon-edit"
            @click="handleOpenEdit(scope.row.id)"
          ></el-button>
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="handleDelete(scope.row.id)"
            v-hasPermission="['system:category:del']"
          ></el-button>
        </template>
      </zk-table>
      <zk-table
        v-else
        v-loading="loading"
        style="margin-top: 10px"
        ref="treeOtherTable"
        :stripe="props.stripe"
        :border="props.border"
        :show-header="props.showHeader"
        :show-summary="props.showSummary"
        :show-row-hover="props.showRowHover"
        :show-index="props.showIndex"
        :tree-type="props.treeType"
        :is-fold="props.isFold"
        :expand-type="props.expandType"
        :selection-type="props.selectionType"
        sum-text="sum"
        :data="otherCategoryList"
        :columns="otherColumns"
      >
        <!-- 操作 -->
        <template slot="caozuo" slot-scope="scope">
          <el-button
            v-hasPermission="['business:category:edit']"
            type="primary"
            size="mini"
            icon="el-icon-edit"
            @click="handleOpenEdit(scope.row.id)"
          ></el-button>
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="handleDelete(scope.row.id)"
            v-hasPermission="['system:category:del']"
          ></el-button>
        </template>
      </zk-table>
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
      <!-- 添加、编辑弹出框 -->
      <el-dialog
        :title="handleDialogTitle"
        :visible.sync="handleDialogVisible"
        @close="handleClose"
        width="50%"
        custom-class="paper-dialog"
      >
        <el-form
          :model="handleDialogForm"
          :rules="handleDialogFormRoles"
          ref="handleDialogFormRef"
          label-width="100px"
        >
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="handleDialogForm.name" size="small"></el-input>
          </el-form-item>
          <el-form-item
            label="父级分类"
            prop="parentId"
            v-if="!isUpdate && currentType === 0"
          >
            <el-cascader
              @change="selectParentChange"
              @clear="clearParent"
              :change-on-select="true"
              :options="parentCategorys"
              clearable
              filterable
              style="width: 100%"
              :props="selectProps"
              v-model="pKeys"
              size="small"
            ></el-cascader>
          </el-form-item>
          <el-form-item label="分类类型" prop="type">
            <el-input
              placeholder="请输入分类类型"
              size="small"
              v-model="handleDialogForm.type"
            ></el-input>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              size="small"
              type="textarea"
              v-model="handleDialogForm.remark"
            ></el-input>
          </el-form-item>
          <el-form-item label="排序" prop="csort">
            <el-input-number
              v-model="handleDialogForm.sort"
              :min="1"
              label="排序"
              size="small"
            ></el-input-number>
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
    </el-card>
  </div>
</template>

<script>
import {
  findCategoryList,
  getListCategory,
  getCategory,
  delCategory,
  addCategory,
  updateCategory,
  getParentCategoryTree,
  exportData,
} from "@/api/business/category";
import { getCategoryType } from "@/utils/zeng";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      // 是否更新
      isUpdate: false,
      // 当前分类 0课程分类 1文章分类 2题目分类 3其他
      currentType: 0,
      pKeys: [],
      handleDialogTitle: "",
      handleDialogVisible: false,
      handleDialogForm: { parentId: 0, name: "", remark: "", sort: "" }, //添加表单
      handleDialogFormRoles: {
        name: [
          { required: true, message: "请输入分类名", trigger: "blur" },
          {
            min: 2,
            max: 10,
            message: "长度在 2 到 10 个字符",
            trigger: "blur",
          },
        ],
        remark: [
          { required: true, message: "请输入备注信息", trigger: "blur" },
        ],
        sort: [{ required: true, message: "请输入排序号", trigger: "blur" }],
        type: [
          {
            required: true,
            message: "请输入分类类型,如：科目分类可以写成course...",
            trigger: "blur",
          },
        ],
      },
      activeStatus: "course",
      total: 0,
      queryMap: { pageNum: 1, pageSize: 5 },
      queryTypeMap: {
        pageNum: 1,
        pageSize: 6,
        type: "course",
      },
      categorys: [],
      otherCategoryList: [],
      parentCategorys: [],
      categoryOption: [],
      categoryName: undefined,
      categoryList: [],
      selectProps: {
        expandTrigger: "hover",
        value: "id",
        label: "name",
        children: "children",
      }, //级联选择器配置项
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
      currentColumns: [
        {
          label: "分类名称",
          prop: "name",
        },
        {
          label: "排序",
          prop: "sort",
        },
        {
          label: "创建时间",
          prop: "createTime",
        },
        {
          label: "修改时间",
          prop: "modifiedTime",
        },
        {
          label: "备注",
          prop: "remark",
        },
        {
          label: "层级",
          prop: "lev",
          type: "template",
          template: "lev",
        },
        {
          label: "操作",
          type: "template",
          template: "caozuo",
        },
      ],
      otherColumns: [
        {
          label: "分类名称",
          prop: "name",
        },
        {
          label: "创建时间",
          prop: "createTime",
        },
        {
          label: "修改时间",
          prop: "modifiedTime",
        },
        {
          label: "排序",
          prop: "sort",
        },
        {
          label: "操作",
          type: "template",
          template: "caozuo",
        },
      ],
    };
  },
  methods: {
    // 选择状态
    selectStatus(status) {
      switch (status) {
        case "course":
          this.currentType = 0;
          break;
        case "article":
          this.currentType = 1;
          break;
        case "question":
          this.currentType = 2;
        case "lesson":
          this.currentType = 3;
        case "resource":
          this.currentType = 4;
        case "time":
          this.currentType = 5;
          break;
        case "others":
          this.currentType = 6;
          break;
      }
      this.activeStatus = status;
      this.queryTypeMap.type = status;
      this.getCategoryList();
    },
    /**
     * 打开添加科目弹框
     */
    handleOpenAdd() {
      this.getParentCategoryList();
      this.getCategoryList();
      this.handleDialogTitle = "新增科目信息";
      this.isUpdate = false;
      this.pKeys = [];
      this.handleDialogVisible = true;
    },

    handleOpenEdit(id) {
      this.getParentCategoryList();
      this.handleDialogTitle = "编辑科目信息";
      this.isUpdate = true;
      getCategory(id).then((res) => {
        let { code, data, msg } = res;
        if (code === 200) {
          this.handleDialogForm = data;
          this.handleDialogVisible = true;
        } else {
          this.msgInfo("获取科目信息失败！" + msg);
        }
      });
    },

    //关闭添加弹框
    handleClose() {
      this.handleDialogVisible = false;
      this.$refs.handleDialogFormRef.clearValidate();
      this.handleDialogForm = {};
    },
    //更新、新增
    async handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          if (this.currentType != 0) {
            this.handleDialogForm.parentId = 0;
          }
          if (this.isUpdate) {
            let id = this.handleDialogForm.id;
            updateCategory(id, this.handleDialogForm).then((response) => {
              let { code, msg } = response;
              if (code === 200) {
                this.msgSuccess("分类信息更新成功");
                this.getCategoryList();
                this.btnDisabled = false;
                this.btnLoading = false;
                this.handleDialogVisible = false;
                this.handleDialogForm = {};
              } else {
                this.msgError("分类信息更新失败" + msg);
              }
            });
          } else {
            addCategory(this.handleDialogForm).then((response) => {
              let { code, msg } = response;
              if (code === 200) {
                this.msgSuccess("分类信息添加成功");
                this.getCategoryList();
                this.btnDisabled = false;
                this.btnLoading = false;
                this.handleDialogVisible = false;
                this.handleDialogForm = {};
              } else {
                this.msgInfo("分类信息添加失败！" + msg);
              }
            });
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
        delCategory(id)
          .then((res) => {
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("分类数据删除成功");
              this.getCategoryList();
            } else {
              this.msgInfo("分类数据删除失败" + msg);
            }
          })
          .catch((err) => {});
      }
    },
    //父级分类中改变
    selectParentChange() {
      const len = this.pKeys.length;
      if (len > 0) {
        this.handleDialogForm.parentId = this.pKeys[len - 1];
      } else {
        this.handleDialogForm.parentId = 0;
      }
    },
    // 搜索分类
    searchCategories(keywords, callback) {},
    async saveCategory() {
      // 保存分类
      let res = await addCategory({
        name: this.categoryName,
        type: "others",
        parentId: 0,
      });
      let { code, msg } = res;
      if (code === 200) {
        this.msgSuccess("添加成功");
        this.getCategoryType();
      } else {
        this.msgInfo("分类信息添加失败！" + msg);
      }
    },
    handleSelectCategories(item) {
      console.log(item);
    },
    //加载分类数据
    async getCategoryList() {
      let res = await getListCategory(this.queryTypeMap);
      let { code, data, msg } = res;
      if (code === 200) {
        if (data.rows === null) {
          data.rows = [];
        }
        if (this.currentType === 0) {
          this.categorys = data.rows;
        } else {
          this.otherCategoryList = data.rows;
        }
        this.total = data.total;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取分类列表失败:" + msg);
      }
    },
    //加载父级分类数据
    async getParentCategoryList() {
      let res = await getParentCategoryTree({
        type: "course",
      });
      let { code, data } = res;
      if (code === 200) {
        this.parentCategorys = data;
      } else {
        this.msgInfo("父级分类列表失败:" + msg);
      }
    },
    async getCategoryType() {
      let res = await findCategoryList({
        pageNum: 1,
        pageSize: 100,
        type: "tag",
      });
      let { code, data } = res;
      if (code === 200) {
        this.categoryList = data.rows;
      }
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getCategoryList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current;
      this.getCategoryList();
    },
    clearParent() {
      this.handleDialogForm.parentId = "";
    },
  },
  created() {
    this.getCategoryList();
    this.categoryOption = getCategoryType();
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
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
