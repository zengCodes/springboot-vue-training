<template>
  <div id="users">
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户列表</el-breadcrumb-item>
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
        <el-form-item label="学员账号" prop="userName">
          <el-input
            size="small"
            @keyup.enter.native="searchUser"
            @clear="searchUser"
            clearable
            v-model="queryMap.userName"
            placeholder="请输入学员账号查询"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            @keyup.enter.native="searchUser"
            clearable
            @clear="searchUser"
            v-model="queryMap.email"
            placeholder="请输入邮箱查询"
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="queryMap.sex" label="0">男</el-radio>
          <el-radio v-model="queryMap.sex" label="1">女</el-radio>
          <el-radio v-model="queryMap.sex" label="2">未知</el-radio>
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
          <el-button @click="reset" icon="el-icon-refresh" size="small"
            >重置</el-button
          >
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table
        :data="userList"
        size="mini"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column
          label="编号"
          prop="userId"
          min-width="50"
          sortable
          align="center"
        ></el-table-column>
        <el-table-column
          prop="userName"
          label="登录账号"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="sex"
          :formatter="showSex"
          label="性别"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag size="small" type="success" v-if="scope.row.sex === '0'"
              >帅哥</el-tag
            >
            <el-tag
              size="small"
              type="warning"
              v-else-if="scope.row.sex === '1'"
              >美女</el-tag
            >
            <el-tag size="small" type="Info" v-else-if="scope.row.sex === '2'"
              >未知</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="phone"
          label="手机号"
          min-width="80"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="email"
          label="邮箱"
          min-width="80"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          min-width="80"
          sortable
          align="center"
        ></el-table-column>
        <el-table-column
          prop="status"
          label="启用状态"
          min-width="80"
          align="center"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="0"
              inactive-value="1"
              @change="changUserStatus(scope.row)"
            ></el-switch>
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
              <el-form-item label="学员账号" prop="userName">
                <el-input
                  placeholder="请输入学员账号"
                  v-model="handleDialogForm.userName"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="11" v-show="!isUpdate">
              <el-form-item label="登录密码" prop="password">
                <el-input
                  placeholder="请输入登录密码"
                  v-model="handleDialogForm.password"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="真实姓名" prop="nickName">
                <el-input
                  placeholder="请输入真实姓名"
                  v-model="handleDialogForm.nickName"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="22">
              <el-form-item label="用户头像" prop="coverImg">
                <el-upload
                  :disabled="upload.uploadDisabled"
                  ref="uploadImg"
                  :data="upload.dataType"
                  :action="upload.api"
                  :limit="upload.limit"
                  :headers="upload.headers"
                  :multiple="upload.multiple"
                  list-type="picture-card"
                  :file-list="upload.file"
                  :accept="upload.fileType"
                  :auto-upload="upload.auto"
                  :on-remove="handleRemove"
                  :on-exceed="handleExceed"
                  :on-success="handleImgUploadSuccess"
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="handleDialogForm.sex" size="small">
                  <el-radio label="0">帅哥</el-radio>
                  <el-radio label="1">美女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="角色" prop="roleIds">
                <el-select
                  v-model="handleDialogForm.roleIds"
                  multiple
                  placeholder="请选择"
                  size="small"
                >
                  <el-option
                    v-for="item in roleOptions"
                    :key="item.roleId"
                    :label="item.roleName"
                    :value="item.roleId"
                    :disabled="item.status == 1"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="邮箱" prop="email">
                <el-input
                  placeholder="请输入学员邮箱"
                  v-model="handleDialogForm.email"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="22">
            <el-col :span="11">
              <el-form-item label="手机号码" prop="phone">
                <el-input
                  placeholder="请输入学员手机号码"
                  v-model="handleDialogForm.phone"
                  size="small"
                ></el-input>
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
import { getToken } from "@/utils/auth";

import {
  getListUser,
  delUser,
  addUser,
  getUser,
  updateUser,
  exportUser,
  resetUserPwd,
  changeUserStatus,
  importTemplate,
} from "@/api/system/user";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";

import { getListRole } from "@/api/system/role";
export default {
  name: "user",
  data() {
    const checkEmail = (rule, value, callback) => {
      const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
      if (!value) {
        return callback(new Error("邮箱不能为空"));
      }
      setTimeout(() => {
        if (mailReg.test(value)) {
          callback();
        } else {
          callback(new Error("请输入正确的邮箱格式"));
        }
      }, 100);
    };
    const checkPhone = (rule, value, callback) => {
      const phoneReg = /^1[34578]\d{9}$$/;
      if (!value) {
        return callback(new Error("电话号码不能为空"));
      }
      setTimeout(() => {
        if (!Number.isInteger(+value)) {
          callback(new Error("请输入数字值"));
        } else {
          if (phoneReg.test(value)) {
            callback();
          } else {
            callback(new Error("电话号码格式不正确"));
          }
        }
      }, 100);
    };
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      total: 0,
      handleDialogVisible: false, //添加对话框,
      labelPosition: "right", //lable对齐方式
      //查询对象
      queryMap: {
        pageNum: 1,
        pageSize: 6,
        userName: null,
        sex: null,
      },
      userList: [],
      handleDialogForm: {
        roleIds: null,
      }, //表单
      initPassword: undefined,
      handleDialogFormRules: {
        userName: [
          { required: true, message: "请输入学员账号", trigger: "blur" },
          {
            min: 3,
            max: 10,
            message: "长度在 3 到 10 个字符",
            trigger: "blur",
          },
        ],
        nickName: [
          { required: true, message: "请输入真实姓名", trigger: "blur" },
        ],
        roleIds: [
          { required: true, message: "请选择角色类型", trigger: "blur" },
        ],
        sex: [{ required: true, message: "请选择性别", trigger: "blur" }],
        email: [{ required: true, validator: checkEmail, trigger: "blur" }],
        phone: [
          {
            required: true,
            validate: checkPhone,
            trigger: "blur",
          },
        ],
      }, //添加表单验证规则
      upload: {
        dataType: {
          type: "UserAvatar",
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
        fileType: "",
        headers: { Authorization: "Bearer " + getToken() },
      },
      roleOptions: [], //角色
      roleValue: null,
      currentRole: [],
      isUpdate: false,
      handleDialogTitle: "",
    };
  },
  methods: {
    /**
     * 重置
     */
    reset() {
      this.queryMap = {
        pageNum: 1,
        pageSize: 6,
        userName: undefined,
        sex: undefined,
      };
    },
    /**
     * 加载用户列表
     */
    async getUserList() {
      this.loading = true;
      let res = await getListUser(this.queryMap);
      let { code, rows, msg, total } = res;
      if (code === 200) {
        this.total = total;
        this.userList = rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取用户列表失败" + msg);
      }
    },
    /**
     * 删除用户
     */
    async handleDelete(id) {
      const res = await this.$confirm(
        "此操作将永久删除该用户, 是否继续?",
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
        delUser(id)
          .then((res) => {
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("学员删除成功");
              this.getUserList();
            } else {
              this.msgInfo("学员删除失败" + msg);
            }
          })
          .catch((err) => {});
      }
    },
    /**
     * 新增、更新试卷
     */
    handleDialogSubmit() {
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          if (this.isUpdate) {
            let res = await updateUser(this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("学员信息更新成功");
              this.getUserList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("学员信息更新失败" + msg);
            }
          } else {
            let res = await addUser(this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("学员信息新增成功");
              this.getUserList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.handleDialogVisible = false;
              this.handleDialogForm = {};
            } else {
              this.msgError("学员信息新增失败" + msg);
            }
          }
        }
      });
    },
    //关闭弹出框
    handleClose() {
      this.handleDialogVisible = false;
      this.$refs.handleDialogFormRef.clearValidate();
      this.handleDialogForm = {};
      this.upload.file = [];
    },
    /**
     * 搜索用户
     */
    searchUser() {
      this.queryMap.pageNum = 1;
      this.getUserList();
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    handleRemove(file, fileList) {
      this.upload.uploadDisabled = fileList.length >= this.limitCount;
      this.upload.file = fileList;
      this.handleDialogForm.avatar = [];
    },
    handleImgUploadSuccess(response, file, fileList) {
      this.upload.uploadDisabled = true;
      this.handleDialogForm.avatar = response.msg;
      this.upload.file.push({
        url: response.msg,
      });
    },
    /**
     * 打开添加试卷弹框
     */
    async handleHandAdd() {
      this.handleDialogTitle = "新增学员信息";
      this.isUpdate = false;
      let res = getListRole({
        pageNum: 1,
        pageSize: 100,
      });
      this.roleOptions = res.rows;
      this.handleDialogForm.password = this.initPassword;
      this.handleDialogVisible = true;
    },
    /**
     * 修改用户信息
     */
    async handleOpenEdit(row) {
      this.handleDialogTitle = "更新学员信息";
      this.isUpdate = true;
      let res = await getUser(row.userId);
      let { code, data, roleIds, roles } = res;
      if (code === 200) {
        this.handleDialogForm = data;
        this.upload.file.push({
          url: data.avatar,
        });
        this.handleDialogForm.roleIds = roleIds;
        this.roleOptions = roles;
        this.handleDialogVisible = true;
      }
    },
    /**
     *  改变页码
     */
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getUserList();
    },
    /**
     * 翻页
     */
    handleCurrentChange(current) {
      this.queryMap.pageNum = current;
      this.getUserList();
    },
    /**
     * 禁用启用用户
     */
    async changUserStatus(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$confirm(
        '确认要"' + text + '""' + row.userName + '"用户吗?',
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(function () {
          return changeUserStatus(row.userId, row.status);
        })
        .then(() => {
          this.msgSuccess(text + "成功");
        })
        .catch(function () {
          row.status = row.status === "0" ? "1" : "0";
        });
    },
    /**
     * 显示用户性别
     */
    showSex(row, column) {
      return parseInt(row.sex) === 0 ? "帅哥" : row.sex === 1 ? "美女" : "未知";
    },
  },
  created() {
    this.getUserList();
    this.getConfigKey("sys.user.initPassword").then((response) => {
      this.initPassword = response.msg;
    });
    this.upload.fileType = ACCEPT_CONFIG.image.join(",");
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
