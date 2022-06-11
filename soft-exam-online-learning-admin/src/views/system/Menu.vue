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
                @click="openParentAdd"
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
          :data="data"
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
    <!-- 节点新增弹出框 -->
    <el-dialog
      :title="addTitle"
      :visible.sync="addDialogVisible"
      @close="addClose"
      width="50%"
      custom-class="paper-dialog"
    >
      <span>
        <el-form
          size="mini"
          ref="addFormRef"
          :model="addForm"
          label-width="80px"
          :rules="addFormRules"
        >
          <el-form-item label="节点名称" prop="menuName">
            <el-input v-model="addForm.menuName"></el-input>
          </el-form-item>
          <el-form-item label="路径" prop="path">
            <el-input v-model="addForm.path"></el-input>
          </el-form-item>
          <el-form-item label="权限编码" prop="perms">
            <el-input v-model="addForm.perms"></el-input>
          </el-form-item>
          <el-form-item label="图标" prop="icon">
            <el-input v-model="addForm.icon"></el-input>
          </el-form-item>
          <el-form-item label="是否可用" prop="visible">
            <!-- 0正常 1停用 -->
            <template>
              <el-radio v-model="addForm.visible" label="0">可用</el-radio>
              <el-radio v-model="addForm.visible" label="1">禁用</el-radio>
            </template>
          </el-form-item>
          <el-form-item label="是否展开" prop="open">
            <template>
              <el-radio v-model="addForm.open" label="1">展开</el-radio>
              <el-radio v-model="addForm.open" label="0">关闭</el-radio>
            </template>
          </el-form-item>
          <el-form-item label="排序" prop="orderNum">
            <el-input-number
              v-model="addForm.orderNum"
              :min="1"
              :max="10"
              label="描述文字"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="类型" prop="menuType">
            <!-- M目录 C菜单 F按钮 -->
            <template>
              <el-radio v-model="addForm.menuType" label="M">目录</el-radio>
              <el-radio v-model="addForm.menuType" label="C">菜单</el-radio>
              <el-radio v-model="addForm.menuType" label="F">按钮</el-radio>
            </template>
          </el-form-item>
        </el-form>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false"
          >取 消</el-button
        >
        <el-button
          size="small"
          type="primary"
          @click="addNode"
          :loading="btnLoading"
          :disabled="btnDisabled"
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <!-- 编辑节点弹出框 -->
    <el-dialog
      :title="editTitle"
      :visible.sync="editMenuVisible"
      width="50%"
      custom-class="paper-dialog"
      @close="editClose"
    >
      <span>
        <el-form
          size="mini"
          ref="editFormRef"
          :model="editForm"
          label-width="80px"
          :rules="addFormRules"
        >
          <el-form-item label="节点名称" prop="menuName">
            <el-input v-model="editForm.menuName"></el-input>
          </el-form-item>
          <el-form-item label="路径" prop="path">
            <el-input v-model="editForm.path"></el-input>
          </el-form-item>
          <el-form-item label="权限编码" prop="perms">
            <el-input v-model="editForm.perms"></el-input>
          </el-form-item>
          <el-form-item label="图标">
            <el-input v-model="editForm.icon"></el-input>
          </el-form-item>
          <el-form-item label="是否可用" prop="visible">
            <template>
              <el-radio v-model="editForm.visible" label="0">可用</el-radio>
              <el-radio v-model="editForm.visible" label="1">禁用</el-radio>
            </template>
          </el-form-item>
          <el-form-item label="是否展开" prop="open">
            <template>
              <el-radio v-model="editForm.open" :label="1">展开</el-radio>
              <el-radio v-model="editForm.open" :label="0">关闭</el-radio>
            </template>
          </el-form-item>
          <el-form-item label="排序" prop="orderNum">
            <el-input-number
              v-model="editForm.orderNum"
              :min="1"
              :max="100"
              label="排序"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="类型" prop="menuType">
            <template>
              <el-radio v-model="editForm.menuType" label="M">目录</el-radio>
              <el-radio v-model="editForm.menuType" label="C">菜单</el-radio>
              <el-radio v-model="editForm.menuType" label="F">按钮</el-radio>
            </template>
          </el-form-item>
        </el-form>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="editMenuVisible = false"
          >取 消</el-button
        >
        <el-button
          size="small"
          type="primary"
          @click="updateMenu"
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
export default {
  data() {
    const data = [];
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: false,
      open: [], //展开节点
      filterText: "", //节点过滤文本
      addDialogVisible: false, //新增节点弹出框
      editMenuVisible: false, //编辑节点弹出框
      addTitle: "",
      editTitle: "",
      addForm: {
        parentId: "",
        menuName: "",
        path: "",
        menuType: "",
        orderNum: "",
        visible: "",
        open: "",
        perms: "",
      }, //添加请求表单数据
      editForm: {}, //编辑节点表单数据
      addFormRules: {
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
      data: JSON.parse(JSON.stringify(data)),
      defaultProps: {
        children: "children",
        label: "menuName",
      },
    };
  },
  created() {
    this.getMenuTree();
  },
  methods: {
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
    //更新菜单
    async updateMenu() {
      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnLoading = true;
          this.btnDisabled = true;
          let res = await updateMenu(this.editForm);
          let { code, msg } = res;
          if (code === 200) {
            this.msgSuccess("节点信息更新");
            this.editMenuVisible = false;
            this.btnLoading = false;
            this.btnDisabled = false;
            this.editForm = {};
            this.getMenuTree();
          } else {
            this.msgInfo("更新菜单失败" + msg);
          }
        }
      });
    },
    //点击编辑节点
    async edit(row) {
      this.editTitle = "编辑：【" + row.menuName + "】";
      let res = await getEditMenu(row.menuId);
      let { code, data, msg } = res;
      if (code === 200) {
        this.editForm = data;
        this.editMenuVisible = true;
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
    addClose() {
      this.$refs.addFormRef.clearValidate();
      this.addForm = {};
    },
    editClose() {
      this.$refs.editFormRef.clearValidate();
      this.editForm = {};
    },
    //加载菜单树
    async getMenuTree() {
      this.loading = true;
      let res = await getMenuList();
      let { code, data } = res;
      if (code === 200) {
        this.data = data;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    },
    //打开添加框
    openAdd(data) {
      this.addTitle = "添加节点 ：当前【" + data.menuName + "】";
      this.addDialogVisible = true;
      this.addForm.parentId = data.menuId;
      this.pNode = data;
    },
    //添加最高父级节点
    openParentAdd() {
      this.addTitle = "添加第一父级";
      this.addDialogVisible = true;
      this.addForm.parentId = 0;
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
    async addNode() {
      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnLoading = true;
          this.btnDisabled = true;
          let res = await addMenu(this.addForm);
          let { code, msg } = res;
          if (code === 200) {
            this.addDialogVisible = false;
            this.getMenuTree();
            this.msgSuccess("节点添加成功");
            this.btnLoading = false;
            this.btnDisabled = false;
          } else {
            this.msgInfo("添加失败！" + msg);
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
                this.edit(data);
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
