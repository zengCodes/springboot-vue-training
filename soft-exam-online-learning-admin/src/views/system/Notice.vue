<template>
  <div id="users">
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>公告列表</el-breadcrumb-item>
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
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input
            size="small"
            @keyup.enter.native="searchNotice"
            clearable
            clearable
            v-model="queryMap.noticeTitle"
            placeholder="请输入公告标题"
          ></el-input>
        </el-form-item>
        <el-form-item label="操作人员" prop="createBy">
          <el-input
            @keyup.enter.native="searchNotice"
            clearable
            clearable
            v-model="queryMap.createBy"
            placeholder="请输入操作人员"
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-select
            v-model="queryMap.noticeType"
            placeholder="公告类型"
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
        <el-form-item style="margin-left: 50px">
          <el-button
            type="primary"
            @click="searchNotice"
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
          <el-button @click="reset" icon="el-icon-refresh" size="small"
            >重置</el-button
          >
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table
        :data="noticeList"
        size="mini"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column
          label="公告标题"
          align="center"
          prop="noticeTitle"
          :show-overflow-tooltip="true"
        ></el-table-column>
        <el-table-column
          label="公告类型"
          align="center"
          prop="noticeType"
          :formatter="typeFormat"
          width="100"
        >
        </el-table-column>
        <el-table-column
          label="状态"
          align="center"
          prop="status"
          :formatter="statusFormat"
          width="100"
        ></el-table-column>
        <el-table-column
          label="创建者"
          align="center"
          prop="createBy"
          width="100"
        ></el-table-column>
        <el-table-column
          label="创建时间"
          align="center"
          prop="createTime"
          min-width="150"
        >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" min-width="100">
          <template slot-scope="scope">
            <el-button
              v-hasPermission="['system:notice:edit']"
              size="small"
              type="primary"
              icon="el-icon-edit-outline"
              @click="handleOpenEdit(scope.row)"
            ></el-button>
            <el-button
              v-hasPermission="['system:notice:remove']"
              type="danger"
              size="small"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.noticeId)"
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
              <el-form-item label="公告标题" prop="noticeTitle">
                <el-input
                  placeholder="请输入公告标题"
                  v-model="handleDialogForm.noticeTitle"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="公告类型" prop="noticeType">
                <el-select
                  v-model="handleDialogForm.noticeType"
                  placeholder="请选择公告类型"
                  size="small"
                >
                  <el-option
                    v-for="dict in typeOptions"
                    :key="dict.dictValue"
                    :label="dict.dictLabel"
                    :value="dict.dictValue"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="22">
              <el-form-item label="内容" prop="noticeContent">
                <editor
                  v-model="handleDialogForm.noticeContent"
                  :min-height="192"
                />
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
  getListNotice,
  getNotice,
  delNotice,
  addNotice,
  updateNotice,
} from '@/api/system/notice'
import Editor from '@/components/Editor'

export default {
  name: 'notice',
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      total: 0,
      handleDialogVisible: false, //添加对话框,
      labelPosition: 'right', //lable对齐方式
      typeOptions: [],
      statusOptions: [],
      //查询对象
      queryMap: {
        pageNum: 1,
        pageSize: 6,
        noticeTitle: null,
        createBy: null,
        status: null,
      },
      noticeList: [],
      handleDialogForm: {}, //表单
      handleDialogFormRules: {
        noticeTitle: [
          { required: true, message: '公告标题不能为空', trigger: 'blur' },
        ],
        noticeType: [
          { required: true, message: '公告类型不能为空', trigger: 'change' },
        ],
      }, //添加表单验证规则
      isUpdate: false,
      handleDialogTitle: '',
    }
  },
  components: {
    Editor,
  },
  methods: {
    /**
     * 重置
     */
    reset() {
      this.queryMap = {
        pageNum: 1,
        pageSize: 6,
        noticeTitle: null,
        createBy: null,
        status: null,
      }
    },
    /**
     * 加载用户列表
     */
    async getNoticeList() {
      this.loading = true
      let res = await getListNotice(this.queryMap)
      let { code, rows, msg, total } = res
      if (code === 200) {
        this.total = total
        this.noticeList = rows
        setTimeout(() => {
          this.loading = false
        }, 300)
      } else {
        this.msgError('获取公告列表失败' + msg)
      }
    },
    /**
     * 删除
     */
    async handleDelete(id) {
      const res = await this.$confirm(
        '此操作将永久删除该公告, 是否继续?',
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
        let res = await delNotice(id)
        let { code, msg } = res
        if (code === 200) {
          this.msgSuccess('公告类型删除成功')
          this.getNoticeList()
        } else {
          this.msgInfo('公告类型删除失败' + msg)
        }
      }
    },
    /**
     * 新增、更新试卷
     */
    async handleDialogSubmit() {
      let id = this.handleDialogForm.noticeId
      this.$refs.handleDialogFormRef.validate(async valid => {
        if (!valid) {
          return
        } else {
          this.btnDisabled = true
          this.btnLoading = true
          if (this.isUpdate) {
            let res = await updateNotice(id, this.handleDialogForm)
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('公告信息更新成功')
              this.getNoticeList()
              this.btnDisabled = false
              this.btnLoading = false
              this.handleDialogVisible = false
              this.handleDialogForm = {}
            } else {
              this.msgError('公告信息更新失败' + msg)
            }
          } else {
            let res = await addNotice(this.handleDialogForm)
            let { code, msg } = res
            if (code === 200) {
              this.msgSuccess('公告信息新增成功')
              this.getNoticeList()
              this.btnDisabled = false
              this.btnLoading = false
              this.handleDialogVisible = false
              this.handleDialogForm = {}
            } else {
              this.msgError('学员信息新增失败' + msg)
            }
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
    searchNotice() {
      this.queryMap.pageNum = 1
      this.getNoticeList()
    },
    /**
     * 打开添加弹框
     */
    handleHandAdd() {
      this.handleDialogTitle = '新增公告信息'
      this.isUpdate = false
      this.handleDialogVisible = true
    },
    /**
     * 修改用户信息
     */
    async handleOpenEdit(row) {
      this.handleDialogTitle = '更新公告信息'
      this.isUpdate = true
      let res = await getNotice(row.noticeId)
      let { code, data } = res
      if (code === 200) {
        this.handleDialogForm = data
        this.handleDialogVisible = true
      }
    },
    /**
     *  改变页码
     */
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize
      this.getNoticeList()
    },
    /**
     * 翻页
     */
    handleCurrentChange(current) {
      this.queryMap.pageNum = current
      this.getNoticeList()
    },
    // 公告状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 公告状态字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.noticeType)
    },
  },
  created() {
    this.getNoticeList()
    this.getDicts('sys_notice_status').then(response => {
      this.statusOptions = response.data
    })
    this.getDicts('sys_notice_type').then(response => {
      this.typeOptions = response.data
    })
  },
}
</script>
<style lang="scss" scoped>
@import '@/assets/styles/common.scss';
</style>
