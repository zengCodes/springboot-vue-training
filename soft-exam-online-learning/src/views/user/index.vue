<template>
  <div class="orderList">
    <div class="header-main">
      <div class="user-avatar">
        <img :src="userForm.avatar" alt="" />
      </div>
      <div class="user-account">
        <span>{{ userForm.userName }}</span>
      </div>
    </div>
    <div class="main">
      <div class="main-left">
        <dl class="tab-list">
          <dd index="0">个人中心</dd>
          <dd index="1">我的订单</dd>
        </dl>
      </div>
      <div class="main-right">
        <el-card shadow="hover" v-if="currentIndex === '0'">
          <el-tabs
            v-model="currentSelect"
            @tab-click="handleSelect"
            type="card"
          >
            <el-tab-pane label="个人信息" name="info">
              <el-row :gutter="11" style="text-align: center">
                <el-col :span="5">
                  <el-avatar
                    :src="userForm.avatar"
                    class="el-dropdown-avatar"
                    :size="100"
                  ></el-avatar>
                </el-col>
              </el-row>
              <el-divider />
              <el-row :gutter="11" style="text-align: left">
                <el-col :span="11">
                  <span style="line-height: 35px"
                    >账号：{{ userForm.userName }}</span
                  >
                  <br />
                  <span style="line-height: 35px"
                    >姓名：{{ userForm.nickName }}</span
                  ><br />
                  <span style="line-height: 35px"
                    >用户邮箱：{{ userForm.email }}</span
                  ><br />
                  <span style="line-height: 35px"
                    >注册时间：{{ userForm.createTime }}</span
                  ><br />
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane label="个人资料修改" name="update">
              <el-form
                :model="userForm"
                ref="form"
                label-width="100px"
                v-loading="formLoading"
                :rules="rules"
              >
                <el-row :gutter="12">
                  <el-col :span="12">
                    <el-form-item label="用户头像：" prop="avatar">
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
                        accept="image/gif,image/jpeg,image/jpg,image/bmp,image/png"
                        :auto-upload="upload.auto"
                        :on-success="handleImgUploadSuccess"
                        :on-remove="handleRemove"
                        :on-exceed="handleExceed"
                      >
                        <i class="el-icon-plus"></i>
                      </el-upload>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="22">
                  <el-col :span="22">
                    <el-form-item label="真实姓名：" prop="realName" required>
                      <el-input v-model="userForm.nickName"></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="22">
                  <el-col :span="22">
                    <el-form-item label="邮箱：" prop="email" required>
                      <el-input v-model="userForm.email"></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="22">
                  <el-col :span="22">
                    <el-form-item label="年龄：" prop="age">
                      <el-input v-model="userForm.age"></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="22">
                  <el-col :span="8">
                    <el-form-item label="性别：" prop="sex">
                      <el-select
                        v-model="userForm.sex"
                        placeholder="性别"
                        clearable
                      >
                        <el-option
                          v-for="item in sexEnum"
                          :key="item.key"
                          :value="item.key"
                          :label="item.value"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="22">
                  <el-col :span="8">
                    <el-form-item label="出生日期：" prop="birth">
                      <el-date-picker
                        v-model="userForm.birth"
                        type="date"
                        placeholder="选择日期"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="22">
                  <el-col :span="22">
                    <el-form-item label="手机：" prop="phone">
                      <el-input v-model="userForm.phone"></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="22">
                  <el-col :span="8">
                    <el-button
                      type="primary"
                      v-hasPermission="'system:user:edit'"
                      @click="submitForm"
                      size="small"
                      >更新</el-button
                    >
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
        <el-card shadow="hover" v-if="currentIndex === '1'">
          <el-tabs
            v-model="currentSelect"
            @tab-click="handleSelect"
            type="card"
          >
            <el-tab-pane
              v-for="item in orderType"
              :label="item.label"
              :name="item.name"
              :key="item.id"
            >
              <div
                class="table-layout"
                v-for="order in orderStatusList"
                :key="order.id"
              >
                <el-row :gutter="24" class="row-title">
                  <el-col :span="10">订单详情</el-col>
                  <el-col :span="4">金额</el-col>
                  <el-col :span="5">状态</el-col>
                  <el-col :span="5">操作</el-col>
                </el-row>
                <el-row class="row-main">
                  <el-row :gutter="24" class="main-title">
                    <el-col :span="10"
                      >{{ order.createTime }} 订单号：{{
                        order.orderNo
                      }}</el-col
                    >
                    <el-col :span="4">￥{{ order.totalFee }}</el-col>
                    <el-col :span="5">{{ getStatusText(order.status) }}</el-col>
                    <el-col :span="5">
                      <i
                        class="el-icon-delete"
                        @click="handleDelete(order.id)"
                      ></i>
                    </el-col>
                  </el-row>
                  <el-row :gutter="24" class="row-info">
                    <el-col :span="6">
                      <div class="img">
                        {{ order.courseTitle }}(讲师{{ order.teacherName }})
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <span class="info-title">{{ order.account }}</span>
                      <span class="info-price">￥{{ order.totalFee }}</span>
                    </el-col>
                    <el-col :span="6"></el-col>
                    <el-col :span="6" v-if="order.status === 0">
                      <span class="pay-btn" @click="toPay(order.orderNo)"
                        >立即支付</span
                      >
                    </el-col>
                  </el-row>
                </el-row>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>
<script>
import { getToken } from "@/utils/auth";
import { getOrderStatusText } from "@/utils/zeng";
import { updateUser } from "@/api/system/user";
import { getOrderList, delOrder } from "@/api/business/order";
import { mapState, mapGetters } from "vuex";
export default {
  data() {
    return {
      userForm: null,
      currentSelect: undefined,
      formLoading: false,
      rules: {
        username: [
          { required: true, message: "请输入真实姓名", trigger: "blur" },
        ],
        email: [
          { required: true, message: "请输入格式正确的邮箱", trigger: "blur" },
        ],
        phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
        age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
        sex: [{ required: true, message: "请选择性别", trigger: "change" }],
        birth: [
          { required: true, message: "请选择出生日期", trigger: "change" },
        ],
      },
      upload: {
        dataType: {
          type: "UserAvatar",
        },
        // 文件个数超过后隐藏加号
        uploadDisabled: false,
        // 是否多传
        multiple: false,
        //  上传api
        api: process.env.VUE_APP_BASE_API + "/business/resource/image",
        file: [],
        // 是否自动
        auto: true,
        // 文件个数
        limit: 1,
        headers: { Authorization: "Bearer " + getToken() },
      },
      // 当前点击index
      currentIndex: undefined,
      // 订单list
      queryOrderMap: {
        pageNum: 1,
        pageSize: 100,
        account: undefined,
      },
      currentOrderStatus: 0,
      orderType: [
        {
          name: "all",
          label: "全部订单",
        },
        {
          name: "wait",
          label: "待支付",
        },
        {
          name: "already",
          label: "已支付",
        },
        {
          name: "cancel",
          label: "已取消",
        },
      ],
      orderList: [],
    };
  },
  methods: {
    submitForm() {
      updateUser(this.userForm).then((res) => {
        let { code } = res;
        if (code === 200) {
          this.$message.success("修改成功！");
        }
      });
    },
    handleImgUploadSuccess(response, file, fileList) {
      this.upload.uploadDisabled = true;
      this.userForm.avatar = response.msg;
      this.upload.file.push({
        url: process.env.VUE_APP_BASE_API + response.msg,
      });
    },
    handleRemove(file, fileList) {
      this.upload.uploadDisabled = fileList.length >= this.limitCount;
      this.upload.file = fileList;
      this.userForm.avatar = [];
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    handleSelect(tab, e) {
      console.log(tab, e);
    },
    getStatusText(status) {
      return getOrderStatusText(status);
    },
    toPay(number) {
      // 跳转到支付页面
      this.$router.replace({
        path: "pay",
        query: {
          orderNo: number,
        },
      });
    },
    uploadSuccess() {},
    async getOrderListData() {
      let res = await getOrderList(this.queryOrderMap);
      let { code, data } = res;
      if (code === 200) {
        this.orderList = data.rows;
      }
    },
    /**
     * 删除
     */
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该订单, 是否继续?",
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
        let res = await delOrder(id);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("订单删除成功");
        } else {
          this.msgInfo("订单删除失败:" + msg);
        }
        this.getOrderListData();
      }
    },
  },
  created() {
    this.userForm = this.userData.user;
    this.upload.file.push({
      url: this.userForm.avatar,
    });
    this.queryOrderMap.account = this.userForm.userName;
    this.currentSelect = "all";
    this.currentIndex = "0";
    this.getOrderListData();
  },
  mounted() {
    let _this = this;
    document.body.getElementsByClassName("tab-list")[0].addEventListener(
      "click",
      function (e) {
        _this.currentIndex = e.target.getAttribute("index");
      },
      false
    );
  },
  computed: {
    ...mapGetters("enumItem", ["enumFormat"]),
    ...mapState("enumItem", {
      sexEnum: (state) => state.user.sexEnum,
      levelEnum: (state) => state.user.levelEnum,
    }),
    ...mapState({
      userData: (state) => state.user,
    }),
    orderStatusList() {
      if (this.currentOrderStatus === -1) {
        return this.orderList;
      } else {
        return this.orderList.filter(
          (item) => item.status == this.currentOrderStatus
        );
      }
    },
  },
  watch: {
    currentSelect: {
      handler(val) {
        switch (val) {
          case "all":
            this.currentOrderStatus = -1;
            break;
          case "wait":
            this.currentOrderStatus = 0;
            break;
          case "already":
            this.currentOrderStatus = 1;
            break;
          case "cancel":
            this.currentOrderStatus = 2;
            break;
        }
      },
      immediate: true,
    },
    currentIndex: {
      handler(val) {
        switch (val) {
          case "0":
            this.currentSelect = "info";
            break;
          case "1":
            this.currentSelect = "all";
            break;
        }
      },
      immediate: true,
    },
  },
};
</script>
<style lang="scss" scoped>
.header-main {
  background: url("../../assets/images/order/xpc_top.png") no-repeat center;
  background-size: 100% 100%;
  height: 146px;
  display: flex;
  align-items: center;

  .user-avatar {
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-left: 100px;
    img {
      display: inline-block;
      width: 100px;
      height: 100px;
      border-radius: 50%;
    }
  }
  .user-account {
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-left: 20px;
    span {
      font-size: 22px;
      font-weight: 700;
    }
  }
}
::v-deep.main {
  width: 80%;
  margin: 10px auto;
  display: flex;
  .main-left {
    margin-top: 20px;
    height: 500px;
    width: 15%;
    border-radius: 10px;
    background-color: #ffffff;
    dd {
      cursor: pointer;
      font-size: 15px;
      line-height: 40px;
      height: 40px;
      border: 1px solid rgba(grey, 0.2);
      &:hover {
        color: #2590d7;
        border-color: rgba(#2590d7, 0.2);
      }
    }
  }
  .main-right {
    width: 80%;
    margin-top: 20px;
    margin-left: 10px;
    .table-layout {
      margin-bottom: 20px;
      .el-col {
        height: 40px;
        line-height: 40px;
        font-size: 14px;
        text-align: center;
      }
      .row-title {
        .el-col {
          background: #f9f9f9;
        }
      }
      .row-main {
        border: 1px solid #e8e8e8;
        box-sizing: border-box;
        display: block;
        margin-top: 20px;
        .main-title {
          .el-col {
            background: #f9f9f9;
          }
          .el-icon-delete {
            cursor: pointer;
          }
        }
        .row-info {
          height: 130px;
          .img {
            padding: 5px;
            background-color: #4dc0fa;
            display: inline-block;
            vertical-align: top;
            width: 200px;
            height: 120px;
            line-height: 118px;
            color: #fff;
            border-radius: 8px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }
          .info-title {
            color: #337ab7;
          }
          .info-price {
            display: block;
            font-size: 14px;
            color: #ee491f;
            margin-top: 8px;
          }
          .pay-btn {
            display: inline-block;
            vertical-align: middle;
            height: 36px;
            line-height: 36px;
            width: 96px;
            margin-bottom: 24px;
            color: #fff;
            text-align: center;
            background: #2590d7;
            border-radius: 4px;
            outline: none;
            cursor: pointer;
          }
        }
      }
    }
  }
}
</style>
