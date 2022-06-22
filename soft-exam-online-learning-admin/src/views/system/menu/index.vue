<template>
  <div id="roles" v-loading="loading">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>菜单列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 卡片主体 -->
    <el-card class="box-card">
      <div class="block">
        <!-- 节点过滤 -->
        <el-row :gutter="20">
          <el-col :span="16">
            <div class="grid-content bg-purple">
              <el-input
                size="small"
                placeholder="输入关键字进行过滤"
                v-model="filterText"
                clearable
              ></el-input>
            </div>
          </el-col>
          <el-col :span="2">
            <div class="grid-content bg-purple-light">
              <el-button
                size="small"
                type="primary"
                icon="el-icon-plus"
                style="margin-left: 20px"
                @click="openAdd"
                >父级</el-button
              >
            </div>
          </el-col>
          <el-col :span="2">
            <el-button
              size="small"
              v-hasPermission="'menu:export'"
              style="margin-left: 20px"
              icon="el-icon-download"
              @click="downExcel"
              >导出</el-button
            >
          </el-col>
        </el-row>

        <p>菜单权限树</p>
        <el-tree
          v-loading="loading"
          :data="treeMenuData"
          accordion
          :auto-expand-parent="false"
          node-key="menuId"
          show-checkbox
          :default-expanded-keys="open"
          :expand-on-click-node="false"
          :render-content="renderContent"
          :props="defaultProps"
          highlight-current
          :filter-node-method="filterNode"
          ref="tree"
        ></el-tree>
      </div>
    </el-card>
    <!-- 节点弹出框 -->
    <el-dialog
      :title="handleTitle"
      :visible.sync="handleDialogVisible"
      @close="handleClose"
      width="50%"
      custom-class="paper-dialog"
    >
      <el-form
        size="mini"
        ref="handleFormRef"
        :model="handleForm"
        label-width="80px"
        :rules="handleFormRules"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="父级节点" prop="parentName">
              <TreeSelect
                :treeData="treeSelectProps.menuOptions"
                :defaultProps="treeSelectProps.defaultProps"
                :nodeKey="treeSelectProps.treeNodeKey"
                :checkedKeys="treeSelectProps.treeDefaultCheckedKeys"
                :isAddState="treeSelectProps.isAddState"
                @popoverHide="popoverHide"
              ></TreeSelect>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="当前节点名称"
              prop="menuName"
              label-width="110px"
            >
              <el-input v-model="handleForm.menuName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="类型" prop="menuType">
              <!-- M目录 C菜单 F按钮 -->
              <template>
                <el-radio v-model="handleForm.menuType" label="M"
                  >目录</el-radio
                >
                <el-radio v-model="handleForm.menuType" label="C"
                  >菜单</el-radio
                >
                <el-radio v-model="handleForm.menuType" label="F"
                  >按钮</el-radio
                >
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="handleForm.path"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码" prop="perms" label-width="110px">
              <el-input v-model="handleForm.perms"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24" v-if="menuType === 'C'">
          <el-col :span="12">
            <el-form-item label="组件地址" prop="component">
              <el-input v-model="handleForm.component"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="handleForm.icon"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否隐藏" prop="visible">
              <!-- 0显示 1隐藏 -->
              <template>
                <el-radio v-model="handleForm.visible" label="0">显示</el-radio>
                <el-radio v-model="handleForm.visible" label="1">隐藏</el-radio>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否展开" prop="open">
              <template>
                <el-radio v-model="handleForm.open" label="1">展开</el-radio>
                <el-radio v-model="handleForm.open" label="0">关闭</el-radio>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number
                v-model="handleForm.orderNum"
                :min="1"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="handleClose">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          @click="handleSubmit"
          :loading="btnLoading"
          :disabled="btnDisabled"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMenuList,
  getEditMenu,
  updateMenu,
  delMenu,
  addMenu,
} from "@/api/system/menu";
import TreeSelect from "@/components/TreeSelect";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: false,
      open: [], //展开节点
      currentDialog: 0,
      filterText: "", //节点过滤文本
      handleDialogVisible: false, //新增节点弹出框
      handleTitle: "",
      treeMenuData: [],
      handleForm: {
        parentId: "",
        menuName: "",
        path: "",
        menuType: "",
        orderNum: "",
        visible: "",
        open: "",
        perms: "",
      }, //添加请求表单数据
      handleFormRules: {
        menuName: [
          { required: true, message: "节点名称不能为空", trigger: "blur" },
          {
            min: 3,
            max: 10,
            message: "长度在 3 到 10 个字符",
            trigger: "blur",
          },
        ],
        visible: [
          { required: true, message: "节点状态不能为空", trigger: "blur" },
        ],
        orderNum: [
          { required: true, message: "排序不能为空", trigger: "blur" },
        ],
        menuType: [
          { required: true, message: "类型不能为空", trigger: "blur" },
        ],
      }, //添加表单验证规则
      pNode: {}, //父节点
      defaultProps: {
        children: "children",
        label: "menuName",
      },
      treeSelectProps: {
        defaultProps: {
          children: "children",
          label: "menuName",
        },
        treeNodeKey: "id",
        treeDefaultCheckedKeys: [],
        isAddState: "1",
        menuOptions: [],
      },
    };
  },
  components: { TreeSelect },
  created() {
    this.getMenuTree();
    this.getTreeSelectList();
  },
  methods: {
    popoverHide(checkedIds, checkedData) {
      console.log(checkedIds);
      console.log(checkedData);
      this.handleForm.parentId = checkedIds;
    },
    /**
     * 加载菜单表格
     */
    downExcel() {
      var $this = this;
      const res = axios
        .request({
          url: "api/system/menu/excel",
          method: "post",
          responseType: "blob",
        })
        .then((res) => {
          if (res.headers["content-type"] === "application/json") {
            return $this.$message.error(
              "Subject does not have permission [menu:export]"
            );
          }
          const data = res.data;
          let url = window.URL.createObjectURL(data); // 将二进制文件转化为可访问的url
          const a = document.createElement("a");
          document.body.appendChild(a);
          a.href = url;
          a.download = "菜单列表.xls";
          a.click();
          window.URL.revokeObjectURL(url);
        });
    },
    //点击编辑节点
    async handleEdit(row) {
      this.editTitle = "编辑节点：【" + row.menuName + "】";
      let res = await getEditMenu(row.menuId);
      let { code, data, msg } = res;
      if (code === 200) {
        this.handleForm = data;
        this.currentDialog = 0;
        this.handleDialogVisible = true;
      } else {
        this.msgInfo("获取编辑节点信息错误！" + msg);
      }
    },
    //过滤节点
    filterNode(value, data) {
      if (!value) return true;
      return data.menuName.indexOf(value) !== -1;
    },
    //关闭添加
    handleClose() {
      this.$refs.handleFormRef.clearValidate();
      this.handleDialogVisible = false;
      this.handleForm = {};
    },
    //加载菜单树
    async getMenuTree() {
      this.loading = true;
      let res = await getMenuList();
      let { code, data } = res;
      if (code === 200) {
        this.treeMenuData = data;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    },
    //打开添加框
    openAdd(data) {
      this.currentDialog = 1;
      this.handleTitle = "添加父级节点 ：当前【" + data.menuName + "】";
      this.handleDialogVisible = true;
      this.handleForm.parentId = data.menuId;
      this.pNode = data;
    },
    //添加最高父级节点
    openAdd() {
      this.currentDialog = 1;
      this.handleTitle = "添加父级节点";
      this.handleDialogVisible = true;
      this.handleForm.parentId = 0;
    },
    //点击删除按钮
    async delNode(node, data) {
      const res = await this.$confirm(
        "此操作将永久删除该节点, 是否继续?",
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
        let res = await delMenu(node.data.menuId);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("节点删除成功");
          this.getMenuTree();
        } else {
          this.msgInfo("节点删除失败" + msg);
        }
      }
    },
    //发送添加节点请求
    handleSubmit() {
      this.$refs.handleFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnLoading = true;
          this.btnDisabled = true;
          if (this.currentDialog === 0) {
            let res = await updateMenu(this.handleForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("节点信息更新");
              this.btnLoading = false;
              this.btnDisabled = false;
              this.handleDialogVisible = {};
              this.getMenuTree();
              this.handleDialogVisible = false;
            } else {
              this.msgInfo("更新菜单失败" + msg);
            }
          } else {
            let res = await addMenu(this.handleForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("节点添加成功");
              this.btnLoading = false;
              this.btnDisabled = false;
              this.handleDialogVisible = {};
              this.getMenuTree();
              this.handleDialogVisible = false;
            } else {
              this.msgInfo("添加失败！" + msg);
            }
          }
        }
      });
    },
    renderContent(h, { node, data, store }) {
      return (
        <span class="custom-tree-node">
          <span>
            <i class={data.icon}></i>&nbsp;&nbsp;&nbsp;{node.label}
            {node.data.menuType == "M" ? (
              <el-tag style="margin-left:20px;" effect="plain" size="mini">
                目录
              </el-tag>
            ) : node.data.menuType == "C" ? (
              <el-tag style="margin-left:20px;" effect="plain" size="mini">
                菜单
              </el-tag>
            ) : (
              <el-tag
                style="margin-left:20px;"
                type="warning"
                effect="plain"
                size="mini"
              >
                权限 [{node.data.perms}]
              </el-tag>
            )}
          </span>
          <span>
            <el-button
              size="mini"
              type="text"
              on-click={() => {
                this.handleEdit(data);
                return false;
              }}
            >
              <i class="el-icon-edit"></i>&nbsp;编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              on-click={() => {
                this.openAdd(data);
              }}
            >
              <i class="el-icon-plus"></i>&nbsp;增加
            </el-button>

            <el-button
              size="mini"
              type="text"
              on-click={() => this.delNode(node, data)}
            >
              <i class="el-icon-delete"></i>&nbsp;删除
            </el-button>
          </span>
        </span>
      );
    },
    /** 查询菜单下拉树结构 */
    getTreeSelectList() {
      getMenuList().then((response) => {
        this.treeSelectProps.menuOptions = this.handleArrayTree(response.data);
      });
    },
  },
  computed: {
    menuType() {
      return this.handleForm.menuType;
    },
  },
  watch: {
    filterText: {
      handler(val) {
        this.$refs.tree.filter(val);
      },
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/common.scss";

::v-deep .custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
