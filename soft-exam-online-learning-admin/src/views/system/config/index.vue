<template>
  <div id="users">
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>配置列表</el-breadcrumb-item>
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
        <el-form-item label="参数名称" prop="configName">
          <el-input
            v-model="queryMap.configName"
            placeholder="请输入参数名称"
            clearable
            size="small"
            @keyup.enter.native="sea"
          />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input
            v-model="queryMap.configKey"
            placeholder="请输入参数键名"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-select
            v-model="queryMap.configType"
            placeholder="系统内置"
            clearable
            size="small"
          >
            <el-option
              v-for="dict in typeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            size="small"
            style="width: 240px"
            value-format="yyyy-MM-dd"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item style="margin-left: 50px">
          <el-button
            type="primary"
            @click="searchUser"
            size="small"
            icon="el-icon-search"
            >查询</el-button
          >
          <el-button
            type="success"
            icon="el-icon-plus"
            @click="handleHandAdd"
            v-hasPermission="['system:user:add']"
            size="small"
            >添加</el-button
          >
          <el-button
            @click="handleRefreshCache"
            icon="el-icon-refresh"
            size="small"
            >刷新缓存</el-button
          >
          <el-button
            size="small"
            @click="handleExport"
            icon="el-icon-download"
            :loading="exportLoading"
            v-hasPermission="['system:user:export']"
            >导出</el-button
          >
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table
        :data="configList"
        size="mini"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column label="参数主键" align="center" prop="configId" />
        <el-table-column
          label="参数名称"
          align="center"
          prop="configName"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="参数键名"
          align="center"
          prop="configKey"
          :show-overflow-tooltip="true"
        />
        <el-table-column label="参数键值" align="center" prop="configValue" />
        <el-table-column
          label="系统内置"
          align="center"
          prop="configType"
          :formatter="typeFormat"
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
        <el-table-column label="操作" align="center" min-width="100">
          <template slot-scope="scope">
            <el-button
              v-hasPermission="['system:user:edit']"
              size="small"
              type="primary"
              icon="el-icon-edit-outline"
              @click="handleOpenEdit(scope.row)"
            ></el-button>

            <el-button
              v-hasPermission="['system:user:remove']"
              type="danger"
              size="small"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.userId)"
            ></el-button>
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

      <!-- 添加、新增对话框 -->
      <el-dialog
        :title="handleDialogTitle"
        @close="handleClose"
        :visible.sync="handleDialogVisible"
        width="50%"
        custom-class="paper-dialog"
      >
        <!-- 表单 -->
        <el-form
          :model="handleDialogForm"
          :label-position="labelPosition"
          :rules="handleDialogFormRules"
          ref="handleDialogFormRef"
          label-width="80px"
        >
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="参数名称" prop="configName">
                <el-input
                  v-model="handleDialogForm.configName"
                  placeholder="请输入参数名称"
                  size="small"
                />
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="参数键名" prop="configKey">
                <el-input
                  v-model="handleDialogForm.configKey"
                  placeholder="请输入参数键名"
                  size="small"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="参数键值" prop="configValue">
                <el-input
                  v-model="handleDialogForm.configValue"
                  placeholder="请输入参数键值"
                  size="small"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="系统内置" prop="configType">
                <el-radio-group v-model="handleDialogForm.configType">
                  <el-radio
                    v-for="dict in typeOptions"
                    :key="dict.dictValue"
                    :label="dict.dictValue"
                    >{{ dict.dictLabel }}</el-radio
                  >
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="22">
              <el-form-item label="备注" prop="remark">
                <el-input
                  v-model="handleDialogForm.remark"
                  type="textarea"
                  placeholder="请输入内容"
                  size="small"
                />
              </el-form-item>
            </el-col>
          </el-row>
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
import {
  getListConfig,
  getConfig,
  getConfigKey,
  addConfig,
  updateConfig,
  delConfig,
  refreshCache,
  exportConfig,
} from '@/api/system/config'
export default {
  name: 'user',
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      exportLoading: false,
      total: 0,
      handleDialogVisible: false, //添加对话框,
      labelPosition: 'right', //lable对齐方式
      //查询对象
      queryMap: {
        pageNum: 1,
        pageSize: 6,
        configName: undefined,
        configKey: undefined,
        configType: undefined,
      },
      dateRange: null,
      configList: [],
      typeOptions: [],

      handleDialogForm: {}, //表单
      handleDialogFormRules: {
        configName: [
          { required: true, message: '参数名称不能为空', trigger: 'blur' },
        ],
        configKey: [
          { required: true, message: '参数键名不能为空', trigger: 'blur' },
        ],
        configValue: [
          { required: true, message: '参数键值不能为空', trigger: 'blur' },
        ],
      }, //添加表单验证规则
      isUpdate: false,
      handleDialogTitle: '',
    }
  },
  methods: {
    /**
     * 重置
     */
    reset() {
      this.queryMap = {
        pageNum: 1,
        pageSize: 6,
        configName: undefined,
        configKey: undefined,
        configType: undefined,
      }
    },
    /** 刷新缓存按钮操作 */
    handleRefreshCache() {
      refreshCache().then(() => {
        this.msgSuccess('刷新成功')
      })
    },
    // 参数系统内置字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.configType)
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryMap
      this.$confirm('是否确认导出所有参数数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.exportLoading = true
          return exportConfig(queryParams)
        })
        .then(response => {
          this.download(response.msg)
          this.exportLoading = false
        })
        .catch(() => {})
    },
    /**
     * 加载用户列表
     */
    async getConfigList() {
      this.loading = true
      getListConfig(this.queryMap).then(res => {
        let { code, rows, msg, total } = res
        if (code === 200) {
          this.total = total
          this.configList = rows
          setTimeout(() => {
            this.loading = false
          }, 300)
        } else {
          this.msgError('获参数配置列表失败' + msg)
        }
      })
    },
    /**
     * 删除用户
     */
    async handleDelete(id) {
      const res = await this.$confirm(
        '此操作将永久删除该配置, 是否继续?',
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
        delConfig(id)
          .then(res => {
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('配置删除成功')
              this.getConfigList()
            } else {
              this.msgInfo('配置删除失败' + msg)
            }
          })
          .catch(err => {})
      }
    },
    /**
     * 新增、更新试卷
     */
    async handleDialogSubmit() {
      let id = this.handleDialogForm.configId
      this.$refs.handleDialogFormRef.validate(async valid => {
        if (!valid) {
          return
        } else {
          this.btnDisabled = true
          this.btnLoading = true
          if (this.isUpdate) {
            updateConfig(id, this.handleDialogForm).then(response => {
              let { code, msg } = response
              if (code === 200) {
                this.msgSuccess('配置信息更新成功')
                this.getConfigList()
                this.btnDisabled = false
                this.btnLoading = false
                this.handleDialogVisible = false
                this.handleDialogForm = {}
              } else {
                this.msgError('配置信息更新失败' + msg)
              }
            })
          } else {
            addConfig(this.handleDialogForm).then(res => {
              let { code, msg } = response
              if (code === 200) {
                this.msgSuccess('配置信息新增成功')
                this.getConfigList()
                this.btnDisabled = false
                this.btnLoading = false
                this.handleDialogVisible = false
                this.handleDialogForm = {}
              } else {
                this.msgError('配置信息新增失败' + msg)
              }
            })
          }
        }
      })
    },
    //关闭弹出框
    handleClose() {
      this.handleDialogVisible = false
      this.$refs.handleDialogFormRef.clearValidate()
      this.handleDialogForm = {}
    },
    /**
     * 搜索用户
     */
    searchUser() {
      this.queryMap.pageNum = 1
      this.getConfigList()
    },
    /**
     * 打开添加试卷弹框
     */
    handleHandAdd() {
      this.handleDialogTitle = '新增配置信息'
      this.isUpdate = false
      this.handleDialogVisible = true
    },
    /**
     * 修改用户信息
     */
    handleOpenEdit(row) {
      this.handleDialogTitle = '更新配置信息'
      this.isUpdate = true
      getConfig(row.configId)
        .then(res => {
          let { code, data } = res
          if (code === 200) {
            this.handleDialogForm = data
            this.handleDialogVisible = true
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    /**
     *  改变页码
     */
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize
      this.getConfigList()
    },
    /**
     * 翻页
     */
    handleCurrentChange(current) {
      this.queryMap.pageNum = current
      this.getConfigList()
    },
  },
  created() {
    this.getConfigList()
    this.getDicts('sys_yes_no').then(response => {
      this.typeOptions = response.data
    })
  },
}
</script>
<style lang="scss" scoped>
@import '@/assets/styles/common.scss';
</style>
