<template>
  <div id="users">
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>字典列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 用户列表卡片区 -->
    <el-card class="box-card">
      <el-form
        :inline="true"
        ref="form"
        :model="queryMap"
        label-width="70px"
        size="small"
      >
        <el-form-item>
          <el-button
            type="success"
            icon="el-icon-plus"
            @click="handleOpenAdd"
            v-hasPermission="['system:user:add']"
            size="small"
            >添加</el-button
          >
          <el-button @click="handleRefresh" icon="el-icon-refresh" size="small"
            >刷新</el-button
          >
          <el-button
            size="small"
            @click="downExcel"
            icon="el-icon-download"
            v-hasPermission="['system:user:export']"
            >导出</el-button
          >
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table
        :data="dictList"
        size="mini"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典编码" align="center" prop="dictCode" />
        <el-table-column label="字典标签" align="center" prop="dictLabel" />
        <el-table-column label="字典键值" align="center" prop="dictValue" />
        <el-table-column label="字典排序" align="center" prop="dictSort" />
        <el-table-column
          label="状态"
          align="center"
          prop="status"
          :formatter="statusFormat"
        />
        <el-table-column
          label="备注"
          align="center"
          prop="remark"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="创建时间"
          align="center"
          prop="createTime"
          width="180"
        >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleOpenEdit(scope.row)"
              v-hasPermission="['system:dict:edit']"
              >修改</el-button
            >
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermission="['system:dict:remove']"
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
        :current-page="queryMap.pageNum"
        :page-sizes="[6, 10, 20, 30]"
        :page-size="queryMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>

      <!-- 添加对话框 -->
      <el-dialog
        :title="handleDialogTitle"
        @close="handleClose"
        :visible.sync="handleDialogVisible"
        width="50%"
        custom-class="paper-dialog"
      >
        <!-- 表单 -->
        <el-form
          ref="handleDialogFormRef"
          :model="handleDialogForm"
          label-width="80px"
          :rules="handleDialogFormRoles"
          :label-position="labelPosition"
        >
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="字典名称" prop="dictName">
                <el-input
                  placeholder="请输入字典名称"
                  v-model="handleDialogForm.dictName"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="字典类型" prop="dictType">
                <el-input
                  placeholder="请输入字典类型"
                  v-model="handleDialogForm.dictType"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
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
          </el-row>
          <el-row :gutter="22">
            <el-col :span="22">
              <el-form-item label="备注" prop="remark">
                <el-input
                  v-model="handleDialogForm.remark"
                  type="textarea"
                  placeholder="请输入备注内容"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button
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
import {
  getListDict,
  getData,
  delData,
  addData,
  updateData,
  exportData,
} from '@/api/system/dict'
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      total: 0,
      // 添加、编辑对话框
      handleDialogVisible: false,
      // label对齐方式
      labelPosition: 'right',
      //查询对象
      queryMap: {
        pageNum: 1,
        pageSize: 6,
      },
      // 表格数据
      dictList: [],
      //表单
      handleDialogForm: {},
      //表单验证规则
      handleDialogFormRoles: {
        dictName: [
          { required: true, message: '字典名称不能为空', trigger: 'blur' },
        ],
        dictType: [
          { required: true, message: '字典类型不能为空', trigger: 'blur' },
        ],
      },
      // 是否更新
      isUpdate: false,
      handleDialogTitle: '',
      statusOptions: [],
    }
  },
  methods: {
    // 数据状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    //添加
    handleOpenAdd() {
      this.handleDialogTitle = '新增字典数据信息'
      this.isUpdate = false
      this.handleDialogVisible = true
    },
    //编辑
    handleOpenEdit(row) {
      this.handleDialogForm = row
      this.handleDialogTitle = '编辑字典数据信息'
      this.isUpdate = true
      this.handleDialogVisible = true
    },
    /**
     * 加载列表
     */
    async getDictList() {
      this.loading = true
      getListDict(this.queryMap).then(res => {
        let { code, rows, msg, total } = res
        if (code === 200) {
          this.total = total
          this.dictList = rows
          setTimeout(() => {
            this.loading = false
          }, 300)
        } else {
          this.msgError('获取字典列表失败' + msg)
        }
      })
    },
    /**
     * 删除用户
     */
    async handleDelete(id) {
      const res = await this.$confirm(
        '此操作将永久删除该字典数据, 是否继续?',
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
        delDict(id)
          .then(res => {
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('字典数据删除成功')
              this.getListDict()
            } else {
              this.msgInfo('字典数据删除失败' + msg)
            }
          })
          .catch(err => {})
      }
    },
    /**
     * 添加、编辑字典数据
     */
    async handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async valid => {
        if (!valid) {
          return
        } else {
          this.btnLoading = true
          this.btnDisabled = true
          if (!this.isUpdate) {
            updateUser(this.handleDialogForm)
              .then(res => {
                let { code, msg } = res
                if (code === 200) {
                  this.msgSuccess('学员信息更新成功')
                  this.handleDialogVisible = false
                  this.btnLoading = false
                  this.btnDisabled = false
                  this.getUserList()
                } else {
                  this.msgInfo('学员信息更新失败' + msg)
                }
              })
              .catch(err => {
                console.log(err)
              })
          } else {
            addDict(this.handleDialogForm)
              .then(res => {
                let { code, msg } = res
                if (code === 200) {
                  this.handleDialogVisible = false
                  this.getListDict()
                  this.msgSuccess('字典数据添加成功')
                  this.btnLoading = false
                  this.btnDisabled = false
                } else {
                  this.msgInfo('字典数据添加失败！' + msg)
                }
              })
              .catch(err => {
                console.log(err)
              })
          }
        }
      })
    },

    /**
     * 搜索用户
     */
    handleRefresh() {
      this.queryMap.pageNum = 1
      this.getDictList()
    },
    /**
     * 修改用户信息
     */
    edit(row) {
      this.getUserInfo(row)
      this.handleDialogVisible = true
    },
    /**
     *  改变页码
     */
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize
      this.getDictList()
    },
    /**
     * 翻页
     */
    handleCurrentChange(current) {
      this.queryMap.pageNum = current
      this.getDictList()
    },

    //关闭添加弹框
    handleClose() {
      this.handleDialogVisible = false
      this.$refs.handleDialogFormRef.clearValidate()
      this.handleDialogForm = {}
    },
  },
  created() {
    this.getDictList()
    this.getDicts('sys_normal_disable').then(response => {
      this.statusOptions = response.data
    })
  },
}
</script>
<style lang="scss" scoped>
@import '@/assets/styles/common.scss';
</style>
