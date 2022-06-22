<template>
  <div id="LoginLogs">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>日志管理</el-breadcrumb-item>
      <el-breadcrumb-item>登入日志</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="box-card">
      <el-form
        size="mini"
        :inline="true"
        :model="queryMap"
        class="demo-form-inline"
      >
        <el-form-item label="登录地址" prop="ipaddr">
          <el-input
            v-model="queryMap.ipaddr"
            placeholder="请输入登录地址"
            clearable
            style="width: 240px"
            size="small"
            @keyup.enter.native="search"
          />
        </el-form-item>
        <el-form-item label="用户名称" prop="userName">
          <el-input
            v-model="queryMap.userName"
            placeholder="请输入用户名称"
            clearable
            size="small"
            @keyup.enter.native="search"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryMap.status"
            placeholder="登录状态"
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
        <el-form-item label="登录时间">
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
          <el-button icon="el-icon-search" @click="search" type="primary"
            >查询</el-button
          >
        </el-form-item>
      </el-form>
      <!-- 表格区域 -->
      <template>
        <el-table
          border
          stripe
          size="mini"
          :data="LoginLogData"
          style="width: 100%; margin-top: 20px"
          height="500"
        >
          <el-table-column label="访问编号" align="center" prop="infoId" />
          <el-table-column label="用户名称" align="center" prop="userName" />
          <el-table-column
            label="登录地址"
            align="center"
            prop="ipaddr"
            width="130"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="登录地点"
            align="center"
            prop="loginLocation"
            :show-overflow-tooltip="true"
          />
          <el-table-column label="浏览器" align="center" prop="browser" />
          <el-table-column label="操作系统" align="center" prop="os" />
          <el-table-column
            label="登录状态"
            align="center"
            prop="status"
            :formatter="statusFormat"
          />
          <el-table-column label="操作信息" align="center" prop="msg" />
          <el-table-column
            label="登录日期"
            align="center"
            prop="loginTime"
            width="180"
          >
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.loginTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100px;">
            <template slot-scope="scope">
              <el-button
                v-hasPermission="'loginLog:delete'"
                type="text"
                size="mini"
                icon="el-icon-delete"
                @click="del(scope.row.id)"
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
    </el-card>
  </div>
</template>

<script>
import {
  getListLog,
  delLoginLog,
  cleanLoginLog,
  exportLoginLog,
} from '@/api/monitor/loginLog'
export default {
  data() {
    return {
      LoginLogData: [],
      total: 0, //总共多少条数据
      dateRange: null,
      statusOptions: null,
      queryMap: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        status: null,
        ipaddr: null,
      }, //查询对象
    }
  },
  methods: {
    //搜索
    search() {
      this.queryMap.pageNum = 1
      this.getLoginLogList()
    },

    //加载登入日志列表
    async getLoginLogList() {
      getListLog(this.queryMap)
        .then(res => {
          let { code, rows, msg, total } = res
          if (code === 200) {
            this.total = total
            this.LoginLogData = rows
          } else {
            this.msgError('获取登录日志列表错误：' + msg)
          }
        })
        .catch(err => {})
    },
    //删除登入日志
    async del(id) {
      const res = await this.$confirm(
        '此操作将永久删除该登入日志, 是否继续?',
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
        delLoginLog(id)
          .then(res => {
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('日志删除成功')
              this.getLoginLogList()
            } else {
              this.msgInfo('日志删除失败' + msg)
            }
          })
          .catch(err => {})
      }
    },

    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize
      this.getLoginLogList()
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current
      this.getLoginLogList()
    },
    // 登录状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
  },
  created() {
    this.getLoginLogList()
    this.getDicts('sys_common_status').then(response => {
      this.statusOptions = response.data
    })
  },
}
</script>
