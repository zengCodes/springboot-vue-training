<template>
  <div id="roles">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>角色列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 卡片主体 -->
    <el-card class="box-card">
      <!-- 上面工具栏 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            clearable
            size="small"
            placeholder="请输入角色名查询"
            v-model="queryMap.roleName"
            class="input-with-select"
            @clear="getRoleList"
          >
            <el-button
              slot="append"
              icon="el-icon-search"
              @click="search"
            ></el-button>
          </el-input>
        </el-col>
        <el-col :span="2">
          <el-button
            size="small"
            v-hasPermission="['system:role:add']"
            type="success"
            icon="el-icon-circle-plus-outline"
            @click="handleOpenAdd"
            >添加</el-button
          >
        </el-col>
        <el-col :span="2">
          <el-button
            size="small"
            v-hasPermission="'role:export'"
            icon="el-icon-download"
            @click="downExcel"
            >导出</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <template>
        <el-table
          :data="roleData"
          size="mini"
          v-loading="loading"
          border
          stripe
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column
            prop="roleId"
            label="ID"
            align="center"
            min-width="100"
          ></el-table-column>
          <el-table-column
            prop="roleName"
            label="角色名"
            min-width="100"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            align="center"
            min-width="100"
          ></el-table-column>
          <el-table-column
            prop="status"
            label="是否禁用"
            align="center"
            min-width="100"
          >
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.status"
                @change="changRoleStatus(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column
            prop="remark"
            label="备注"
            align="center"
          ></el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            min-width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-button
                @click="handleOpenEdit(scope.row)"
                v-hasPermission="'role:edit'"
                type="text"
                icon="el-icon-edit"
                size="small"
                >编辑</el-button
              >
              <el-button
                size="small"
                v-hasPermission="'role:delete'"
                @click="delRole(scope.row.roleId)"
                type="text"
                icon="el-icon-delete"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </template>
      <!-- 分页部分 -->
      <el-pagination
        background
        style="margin-top: 10px"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryMap.pageNum"
        :page-sizes="[8, 16, 32, 64]"
        :page-size="queryMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
      <!-- 添加/编辑弹框 -->
      <el-dialog
        :title="handleDialogTitle"
        :visible.sync="handleDialogVisible"
        width="50%"
        @close="handleClose"
        custom-class="paper-dialog"
      >
        <el-form
          ref="handleDialogFormRef"
          :model="handleDialogForm"
          label-width="80px"
          :rules="handleDialogFormRoles"
        >
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="角色名称" prop="roleName">
                <el-input
                  v-model="handleDialogForm.roleName"
                  placeholder="请输入角色名称"
                  size="small"
                />
              </el-form-item>
            </el-col>
            <el-col :span="11"
              ><el-form-item label="权限字符" prop="roleKey">
                <el-input
                  v-model="handleDialogForm.roleKey"
                  placeholder="请输入权限字符"
                  size="small"
                /> </el-form-item
            ></el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="状态" prop="status">
                <el-radio-group v-model="handleDialogForm.status">
                  <el-radio
                    v-for="dict in statusOptions"
                    :key="dict.dictValue"
                    :label="dict.dictValue"
                    >{{ dict.dictLabel }}</el-radio
                  >
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="角色顺序" prop="roleSort">
                <el-input-number
                  size="small"
                  v-model="handleDialogForm.roleSort"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="菜单权限">
            <el-checkbox
              v-model="menuExpand"
              @change="handleCheckedTreeExpand($event, 'menu')"
              >展开/折叠</el-checkbox
            >
            <el-checkbox
              v-model="menuNodeAll"
              @change="handleCheckedTreeNodeAll($event, 'menu')"
              >全选/全不选</el-checkbox
            >
            <el-checkbox
              v-model="handleDialogForm.menuCheckStrictly"
              @change="handleCheckedTreeConnect($event, 'menu')"
              >父子联动</el-checkbox
            >
            <el-tree
              class="tree-border"
              :data="menuOptions"
              show-checkbox
              ref="menu"
              node-key="id"
              :check-strictly="!handleDialogForm.menuCheckStrictly"
              empty-text="加载中...请稍后"
              :props="defaultProps"
            ></el-tree>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="handleDialogForm.remark"
              type="textarea"
              placeholder="请输入内容"
              size="small"
            ></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button size="small" @click="handleClose">取 消</el-button>
          <el-button
            size="small"
            type="primary"
            @click="handleDialogSubmit"
            :loading="btnLoading"
            :disabled="btnDisabled"
            >确 定</el-button
          >
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import {
  getListRole,
  getRole,
  delRole,
  addRole,
  updateRole,
  exportRole,
  dataScope,
  changeRoleStatus,
} from '@/api/system/role'
import {
  treeSelect as menuTreeSelect,
  roleMenuTreeSelect,
} from '@/api/system/menu'
export default {
  data() {
    return {
      handleDialogTitle: '',
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      total: 0,
      queryMap: { roleName: '', pageNum: 1, pageSize: 8 }, //查询条件
      roleData: [], //角色表格数据
      handleDialogForm: {}, //添加、编辑数据
      handleDialogVisible: false, //添加、编辑弹框的显示
      grantDialogVisible: false, //授权弹出框
      data: [],
      open: [], //展开
      grantId: '',
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      menuOptions: [],
      statusOptions: [],
      handleDialogFormRoles: {
        roleName: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' },
        ],
        roleKey: [
          { required: true, message: '权限字符不能为空', trigger: 'blur' },
        ],
        roleSort: [
          { required: true, message: '角色顺序不能为空', trigger: 'blur' },
        ],
      }, //添加验证规则
      menuExpand: false,
      menuNodeAll: false,
    }
  },
  methods: {
    /**
     * 加载菜单表格
     */
    downExcel() {
      const $this = this
      const res = axios
        .request({
          url: 'api/system/role/excel',
          method: 'post',
          responseType: 'blob',
        })
        .then(res => {
          if (res.headers['content-type'] === 'application/json') {
            return $this.$message.error(
              'Subject does not have permission [role:export]'
            )
          }
          const data = res.data
          let url = window.URL.createObjectURL(data) // 将二进制文件转化为可访问的url
          const a = document.createElement('a')
          document.body.appendChild(a)
          a.href = url
          a.download = '角色列表.xls'
          a.click()
          window.URL.revokeObjectURL(url)
        })
    },

    //搜索
    search() {
      this.queryMap.pageNum = 1
      this.getRoleList()
    },
    // 关闭弹出框
    handleClose() {
      this.handleDialogVisible = false
      this.$refs.handleDialogFormRef.clearValidate()
      this.handleDialogForm = {}
    },
    //添加
    handleOpenAdd() {
      this.handleDialogTitle = '新增角色信息'
      this.isUpdate = false
      this.handleDialogVisible = true
      this.getMenuTreeSelect()
    },
    //编辑
    async handleOpenEdit(row) {
      this.handleDialogTitle = '编辑角色信息'
      this.isUpdate = true
      const roleId = row.roleId
      const roleMenu = this.getRoleMenuTreeSelect(roleId)
      let res = await getRole(roleId)
      this.handleDialogForm = res.data
      this.handleDialogVisible = true
      this.$nextTick(() => {
        roleMenu.then(res => {
          let checkedKeys = res.checkedKeys
          checkedKeys.forEach(v => {
            this.$nextTick(() => {
              this.$refs.menu.setChecked(v, true, false)
            })
          })
        })
      })
    },
    //加载角色列表
    async getRoleList() {
      this.loading = true
      let res = await getListRole(this.queryMap)
      let { code, rows, msg, total } = res
      if (code === 200) {
        this.roleData = rows
        this.total = total
        setTimeout(() => {
          this.loading = false
        }, 300)
      } else {
        this.msgError('获取角色列表失败' + msg)
      }
    },
    //更新角色
    handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async valid => {
        if (!valid) {
          return
        } else {
          this.btnDisabled = true
          this.btnLoading = true
          if (this.isUpdate) {
            this.handleDialogForm.menuIds = this.getMenuAllCheckedKeys()
            let res = await updateRole(this.handleDialogForm)
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('角色信息更新成功')
              this.getRoleList()
              this.btnDisabled = false
              this.btnLoading = false
              this.handleDialogVisible = false
              this.handleDialogForm = {}
            } else {
              this.msgError('角色信息更新失败' + msg)
            }
          } else {
            this.handleDialogForm.menuIds = this.getMenuAllCheckedKeys()
            let res = await addRole(this.handleDialogForm)
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('角色信息新增成功')
              this.getRoleList()
              this.btnDisabled = false
              this.btnLoading = false
              this.handleDialogVisible = false
              this.handleDialogForm = {}
            } else {
              this.msgError('角色信息新增失败' + msg)
            }
          }
        }
      })
    },
    //删除
    async delRole(id) {
      var res = await this.$confirm(
        '此操作将永久删除该角色, 是否继续?',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除',
        })
      })
      if (res === 'confirm') {
        let res = await delRole(id)
        let { code, msg } = res
        if (code === 200) {
          this.msgSuccess('角色删除成功')
          this.getRoleList()
        } else {
          this.msgInfo('角色删除失败：' + msg)
        }
      }
    },
    /**
     * 禁用启用
     */
    async changRoleStatus(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$confirm(
        '确认要"' + text + '""' + row.roleName + '"用户吗?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(function () {
          return changeRoleStatus(row.rid, row.rstatus)
        })
        .then(() => {
          this.msgSuccess(text + '成功')
        })
        .catch(function () {
          row.rstatus = row.rstatus === '0' ? '1' : '0'
        })
    },
    /** 查询菜单树结构 */
    async getMenuTreeSelect() {
      menuTreeSelect().then(response => {
        this.menuOptions = response.data
      })
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menu.getCheckedKeys()
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type == 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : [])
      } else if (type == 'dept') {
        this.$refs.dept.setCheckedNodes(value ? this.deptOptions : [])
      }
    },
    /** 根据角色ID查询菜单树结构 */
    getRoleMenuTreeSelect(roleId) {
      return roleMenuTreeSelect(roleId).then(response => {
        this.menuOptions = response.menus
        return response
      })
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type == 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : [])
      } else if (type == 'dept') {
        this.$refs.dept.setCheckedNodes(value ? this.deptOptions : [])
      }
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize
      this.getRoleList()
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current
      this.getRoleList()
    },
  },
  created() {
    this.getRoleList()
    this.getDicts('sys_normal_disable').then(response => {
      this.statusOptions = response.data
    })
    setTimeout(() => {
      this.loading = false
    }, 500)
  },
}
</script>
<style lang="scss" scoped>
@import '@/assets/styles/common.scss';
</style>
