<template>
  <div id="Logs">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>日志管理</el-breadcrumb-item>
      <el-breadcrumb-item>系统日志</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="box-card">
      <el-form
        :inline="true"
        size="mini"
        :model="queryMap"
        class="demo-form-inline"
      >
        <el-form-item label="系统模块" prop="title">
          <el-input
            v-model="queryMap.title"
            placeholder="请输入系统模块"
            clearable
            size="small"
            @keyup.enter.native="search"
          />
        </el-form-item>
        <el-form-item label="操作人员" prop="operName">
          <el-input
            v-model="queryMap.operName"
            placeholder="请输入操作人员"
            clearable
            size="small"
            @keyup.enter.native="search"
          />
        </el-form-item>
        <el-form-item label="类型" prop="businessType">
          <el-select
            v-model="queryMap.businessType"
            placeholder="操作类型"
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
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryMap.status"
            placeholder="操作状态"
            clearable
            size="small"
          >
            <el-option
              v-for="dict in statusOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
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
        <el-form-item>
          <el-button type="primary" @click="search" icon="el-icon-search"
            >查询</el-button
          >
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <template>
        <el-table
          size="mini"
          border
          stripe
          :data="LogData"
          style="width: 100%"
          height="480"
        >
          <el-table-column label="日志编号" align="center" prop="operId" />
          <el-table-column label="系统模块" align="center" prop="title" />
          <el-table-column
            label="操作类型"
            align="center"
            prop="businessType"
            :formatter="typeFormat"
          />
          <el-table-column
            label="请求方式"
            align="center"
            prop="requestMethod"
          />
          <el-table-column label="操作人员" align="center" prop="operName" />
          <el-table-column
            label="主机"
            align="center"
            prop="operIp"
            width="130"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="操作地点"
            align="center"
            prop="operLocation"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="操作状态"
            align="center"
            prop="status"
            :formatter="statusFormat"
          />
          <el-table-column
            label="操作日期"
            align="center"
            prop="operTime"
            width="180"
          >
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.operTime) }}</span>
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
                icon="el-icon-view"
                @click="handleOpenView(scope.row)"
                v-hasPermission="['monitor:operlog:query']"
                >详细</el-button
              ><el-button
                type="text"
                size="mini"
                v-hasPermission="'log:delete'"
                icon="el-icon-delete"
                @click="del(scope.row.operId)"
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
        :current-page="queryMap.pageNum"
        :page-sizes="[10, 15, 20]"
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
          ref="handleDialogFormRef"
        >
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="操作模块">
                {{ handleDialogForm.title }} /
                {{ typeFormat(handleDialogForm) }}
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="登录信息">
                {{ handleDialogForm.operName }} /
                {{ handleDialogForm.operIp }} /
                {{ handleDialogForm.operLocation }}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="请求地址">
                {{ handleDialogForm.operUrl }}
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="请求方式">
                {{ handleDialogForm.requestMethod }}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="22">
              <el-form-item label="操作方法">
                {{ handleDialogForm.method }}
              </el-form-item>
            </el-col>
            <el-col :span="22">
              <el-form-item label="请求参数">
                {{ handleDialogForm.operParam }}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="22">
              <el-form-item label="返回参数">
                {{ handleDialogForm.jsonResult }}
              </el-form-item>
            </el-col>
            <el-col :span="22">
              <el-form-item label="操作状态：">
                <div v-if="handleDialogForm.status === 0">正常</div>
                <div v-else-if="handleDialogForm.status === 1">失败</div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="12">
              <el-form-item label="操作时间：">{{
                parseTime(handleDialogForm.operTime)
              }}</el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item
                label="异常信息："
                v-if="handleDialogForm.status === 1"
                >{{ handleDialogForm.errorMsg }}</el-form-item
              >
            </el-col>
          </el-row>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import {
  getListOpenLog,
  delOpenLog,
  cleanOpenLog,
  exportOpenLog,
} from '@/api/monitor/operlog'
export default {
  data() {
    return {
      labelPosition: 'right',
      LogData: [],
      total: 0, //总共多少条数据
      queryMap: {
        pageNum: 1,
        pageSize: 10,
        businessType: null,
        operName: null,
        status: null,
      }, //查询对象
      dateRange: null,
      statusOptions: [],
      typeOptions: [],
      handleDialogForm: {},
      handleDialogVisible: false,
      handleDialogTitle: '',
    }
  },
  methods: {
    //搜索
    search() {
      this.queryMap.pageNum = 1
      this.getLogList()
    },
    //关闭弹出框
    handleClose() {
      this.handleDialogVisible = false
      this.handleDialogForm = {}
    },
    //加载系统日志列表
    async getLogList() {
      getListOpenLog(this.queryMap)
        .then(res => {
          let { code, msg, rows, total } = res
          if (code === 200) {
            this.total = total
            this.LogData = rows
          } else {
            this.msgError('获取系统日志列表错误：' + msg)
          }
        })
        .catch(err => {})
    },
    //删除系统日志
    async del(id) {
      let res = await this.$confirm(
        '此操作将永久删除该系统日志, 是否继续?',
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
        delOpenLog(id)
          .then(res => {
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('日志删除成功')
              this.getLogList()
            } else {
              this.msgInfo('日志删除失败' + msg)
            }
          })
          .catch(err => {})
      }
    },
    /**
     * 修改用户信息
     */
    handleOpenView(row) {
      this.handleDialogTitle = '日志信息'
      this.handleDialogForm = row
      this.handleDialogVisible = true
    },

    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize
      this.getLogList()
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current
      this.getLogList()
    },
    // 操作日志状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 操作日志类型字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.businessType)
    },
  },
  created() {
    this.getLogList()
    this.getDicts('sys_oper_type').then(response => {
      this.typeOptions = response.data
    })
    this.getDicts('sys_common_status').then(response => {
      this.statusOptions = response.data
    })
  },
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/common.scss';
.el-tooltip__popper {
  max-width: 400px;
}
</style>
