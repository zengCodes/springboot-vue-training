<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>资源管理</el-breadcrumb-item>
      <el-breadcrumb-item>相册列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="12">
        <el-col :span="6">
          <template v-if="isAlbums">
            <el-button
              size="small"
              type="success"
              icon="el-icon-upload"
              @click="handleUpload"
            >
              上传<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>

            <el-button
              size="small"
              icon="el-icon-refresh"
              @click="getAlbumsList"
              >刷新</el-button
            >
          </template>
          <template v-else>
            <el-button
              size="small"
              type="success"
              icon="el-icon-upload"
              @click="backAlbums"
            >
              返回相册<el-icon class="el-icon-back"><arrow-down /></el-icon>
            </el-button>
            <br /><br />
            <el-checkbox
              :indeterminate="isIndeterminate"
              v-model="checkAll"
              @change="handleCheckAllChange"
            >
              全选
            </el-checkbox>
          </template>
        </el-col>
      </el-row>
      <el-row :gutter="12">
        <el-col :span="5" v-if="isAlbums">
          <div class="article-status-menu">
            <span>相册状态</span>
            <span @click="selectStatus('use')" :class="isActive('use')"
              >使用中</span
            >
            <span @click="selectStatus('delete')" :class="isActive('delete')"
              >回收站</span
            >
          </div>
        </el-col>
      </el-row>
      <!-- 相册列表 -->
      <el-row
        class="album-container"
        :gutter="12"
        v-loading="loading"
        v-if="isAlbums"
      >
        <!-- 空状态 -->
        <el-empty v-if="albumList.length === 0" description="暂无相册" />
        <el-col v-for="item of albumList" :key="item.id" :md="6">
          <div class="album-item" @click="getAlbumsPhoto(item)">
            <!-- 相册操作 -->
            <div class="album-opreation">
              <el-dropdown @command="handleAlbumsCommand">
                <i class="el-icon-more" style="color: #fff" />
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    :command="'update' + JSON.stringify(item)"
                    v-if="item.flag == 0"
                  >
                    <i class="el-icon-edit" />编辑
                  </el-dropdown-item>
                  <el-dropdown-item
                    :command="'regain' + JSON.stringify(item)"
                    v-if="item.flag == 1"
                  >
                    <i class="el-icon-finished" />恢复
                  </el-dropdown-item>
                  <el-dropdown-item
                    :command="'delete' + item.id"
                    v-if="item.flag == 0"
                  >
                    <i class="el-icon-delete" />删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
            <div class="album-photo-count">
              <div>{{ item.imgList.length }}</div>
            </div>
            <el-image
              fit="cover"
              class="album-cover"
              :src="item.imgList[item.imgList.length - 1].location"
            />
            <div class="album-name">{{ item.albumName }}</div>
          </div>
        </el-col>
      </el-row>
      <!-- 相册相片 -->
      <el-row
        class="photo-container"
        :gutter="20"
        v-loading="loading"
        v-if="!isAlbums"
      >
        <!-- 空状态 -->
        <el-empty v-if="photoList.length == 0" description="暂无照片" />
        <el-checkbox-group
          v-model="selectPhotoIdList"
          @change="handleCheckedPhotoChange"
        >
          <el-col :md="5" v-for="item of photoList" :key="item.id">
            <el-checkbox :label="item.id">
              <div class="photo-item">
                <!-- 照片操作 -->
                <div class="photo-opreation">
                  <el-dropdown @command="handlePhotoCommand">
                    <i class="el-icon-more" style="color: #fff" />
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item :command="'delete' + item.id">
                        <i class="el-icon-delete" />删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
                <el-image fit="cover" class="photo-img" :src="item.location" />
              </div>
            </el-checkbox>
          </el-col>
        </el-checkbox-group>
      </el-row>
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 10px"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>
    <!-- 文件上传弹出框 -->
    <el-dialog
      :title="handleDialogTitle"
      :visible.sync="uploadVisible"
      width="50%"
      height="700"
      append-to-body
      custom-class="paper-dialog "
      @close="handleClose"
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
            <el-form-item label="相册名称" prop="albumName">
              <el-input size="small" v-model="handleDialogForm.albumName" />
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="相册描述" prop="albumDesc">
              <el-input size="small" v-model="handleDialogForm.albumDesc" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="相册类型" prop="albumType">
              <el-select
                v-model="handleDialogForm.albumType"
                placeholder="请选择相册类型"
                size="small"
              >
                <el-option
                  v-for="item in resourceTypeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="相册相片">
              <el-upload
                :disabled="upload.uploadDisabled"
                ref="uploadImg"
                :data="upload.dataType"
                :action="upload.api"
                :limit="upload.limit"
                :name="upload.name"
                :headers="upload.headers"
                :multiple="upload.multiple"
                list-type="picture-card"
                :file-list="upload.file"
                accept="image/gif,image/jpeg,image/jpg,image/bmp,image/png"
                :on-change="handleImgUploadChange"
                :auto-upload="upload.auto"
                :on-remove="handleRemove"
                :on-success="handleImgUploadSuccess"
                :on-exceed="handleExceed"
              >
                <i class="el-icon-plus" v-if="!upload.uploadDisabled"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="dialog-footer">
        <div class="footer-button">
          <el-button @click="handleClose" size="small">取 消</el-button>
          <el-button
            size="small"
            type="primary"
            @click="handleDialogSubmit"
            :disabled="btnDisabled"
            :loading="btnLoading"
            >确 定</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAlbumsType } from "@/utils/zeng";
import { getToken } from "@/utils/auth";
import {
  getAlbumImage,
  addType,
  deleteAlbum,
  updateAlbum,
  updateStatus,
} from "@/api/business/resource";
import { getListCategory } from "@/api/business/category";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      labelPosition: "right",
      loading: true,
      // 是否是相册
      isAlbums: true,
      total: 0, //总共多少条数据
      albumList: [], //表格数据
      fileList: [],
      handleImgUploadChange: null,
      queryParams: {
        pageNum: 1,
        pageSize: 100,
        flag: undefined,
      },
      queryMap: { pageNum: 1, pageSize: 100 },
      upload: {
        dataType: {
          type: "Albums",
        },
        // 文件个数超过后隐藏加号
        uploadDisabled: false,
        // 是否多传
        multiple: true,
        //  上传api
        api: "",
        file: [],
        // 是否自动
        auto: false,
        // 文件个数
        limit: 5,
        name: "file",
        fileType: "",
        headers: { Authorization: "Bearer " + getToken() },
      },
      handleDialogForm: {
        albumName: null,
        albumDesc: null,
      },
      handleDialogFormRules: {
        albumName: [
          { required: true, message: "请输入相册名称", trigger: "blur" },
          {
            min: 3,
            max: 10,
            message: "长度在 3 到 10 个字符",
            trigger: "blur",
          },
        ],
        albumDesc: [
          { required: true, message: "请输入相册描述", trigger: "blur" },
        ],
        albumType: [
          { required: true, message: "请选择相册类型", trigger: "blur" },
        ],
        files: [{ required: true, message: "请上传相册照片", trigger: "blur" }],
      },
      // 上传文件
      uploadVisible: false,
      // 是否更新
      isUpdate: false,
      resourceTypeList: [],
      // 相片列表
      photoList: [],
      // 已选
      photoIdList: [],
      // 批量删除
      selectPhotoIdList: [],
      checkAll: false,
      isIndeterminate: false,
      handleDialogTitle: "",
    };
  },
  methods: {
    // 选择状态
    selectStatus(status) {
      switch (status) {
        case "use":
          this.queryParams.flag = 0;
          break;
        case "delete":
          this.queryParams.flag = 1;
          break;
      }
      this.activeStatus = status;
      this.getAlbumsList();
    },
    // 返回相册
    backAlbums() {
      this.isAlbums = true;
    },
    // 上传相册
    handleUpload() {
      this.isUpdate = false;
      this.upload.auto = false;
      this.handleImgUploadChange = this.handleImgUploadChange;
      this.upload.api = process.env.VUE_APP_BASE_API + "/business/resource/add";
      this.handleDialogTitle = "新增相册";
      this.uploadVisible = true;
    },
    // 关闭
    handleClose() {
      this.uploadVisible = false;
      this.handleDialogForm = {};
      this.upload.file = [];
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryParams.pageSize = newSize;
      this.getAlbumsList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryParams.pageNum = current;
      this.getAlbumsList();
    },
    async handleAlbumsCommand(command) {
      const type = command.substring(0, 6);
      const data = JSON.parse(command.substring(6));
      const imgList = data.imgList;
      switch (type) {
        case "update":
          this.isUpdate = true;
          this.upload.auto = true;
          this.upload.api =
            process.env.VUE_APP_BASE_API + "/business/resource/file";
          imgList.map((item) => {
            this.upload.file.push({
              url: item.location,
            });
          });
          this.handleDialogTitle = "修改相册";
          this.getAlbumsList();
          this.handleDialogForm = data;
          this.uploadVisible = true;
          break;
        case "regain":
          var res1 = await this.$confirm(
            "是否恢复该相册及其相片, 是否继续?",
            "提示",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            }
          ).catch(() => {
            this.$message({
              type: "info",
              message: "已取消",
            });
          });
          if (res1 === "confirm") {
            this.handleRegain(data);
          }
          break;
        case "delete":
          var res2 = await this.$confirm(
            "是否删除该相册及其相片, 是否继续?",
            "提示",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            }
          ).catch(() => {
            this.$message({
              type: "info",
              message: "已取消",
            });
          });
          if (res2 === "confirm") {
            this.handleDelete(data);
          }
          break;
        default:
          break;
      }
    },
    handlePhotoCommand(command) {
      const type = command.substring(0, 6);
      const data = JSON.parse(command.substring(6));
      switch (type) {
        case "update":
          break;
        case "regain":
          break;
        case "delete":
          var res3 = this.$confirm("是否删除该相片, 是否继续?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }).catch(() => {
            this.$message({
              type: "info",
              message: "已取消",
            });
          });
          if (res3 === "confirm") {
            this.handleDeletePhoto(data);
          }
          break;
        default:
          break;
      }
    },
    handleImgUploadSuccess(response, file, fileList) {
      // this.uploadDisabled = true;
      console.log(response.msg);
      console.log(file);
      // this.upload.file = fileList;
      this.fileList.push(response.msg);
    },
    handleUploadChange(file, fileList) {
      const isType = file.type === "image/jpeg" || "image/png";
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isType) {
        this.$message.error("上传图片只能是 JPG 格式!");
        fileList.pop();
      }
      if (!isLt5M) {
        this.$message.error("上传图片大小不能超过 5MB!");
        fileList.pop();
      }
      this.upload.file.push(file);
      this.handleDialogForm.files = fileList;
      this.upload.hideUpload = fileList.length >= this.upload.limit;
    },
    //删除图片时
    handleRemove(file, fileList) {
      if (file.id) {
        console.log("删除了已被上传过的图片");
        this.upload.file.push(file.id);
      }
      this.upload.file = fileList;
      this.upload.hideUpload = fileList.length >= this.upload.limit;
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 5 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    // 相册新建
    handleDialogSubmit() {
      let id = this.handleDialogForm.id;
      this.$refs.handleDialogFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        } else {
          if (this.isUpdate) {
            this.handleDialogForm.imgList.map((item, index) => {
              item.location = this.fileList[index];
            });
            console.log(this.handleDialogForm);

            let res = await updateAlbum(id, this.handleDialogForm);
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("相册信息更新成功");
              this.getAlbumsList();
              this.btnDisabled = false;
              this.btnLoading = false;
              this.uploadVisible = false;
              this.handleDialogForm = {};
              this.upload.file = [];
            } else {
              this.msgError("相册信息更新失败" + msg);
            }
          } else {
            if (this.upload.file.length > 0) {
              let formData = new FormData();
              for (let i in this.upload.file) {
                formData.append("files", this.upload.file[i].raw);
              }
              Object.keys(this.handleDialogForm).forEach((item) => {
                formData.append(item, this.handleDialogForm[item]);
              });
              addType(formData).then((res) => {
                let { code, msg } = res;
                if (code === 200) {
                  this.msgSuccess("相册新增成功");
                  this.getAlbumsList();
                  this.btnDisabled = false;
                  this.btnLoading = false;
                  this.uploadVisible = false;
                  this.handleDialogForm = {};
                  this.upload.file = [];
                } else {
                  this.msgError("相册新增失败" + msg);
                }
              });
            } else {
              this.$message.warning(`请先上传图片文件`);
            }
          }
        }
      });
    },
    // 查看相册列表
    async getAlbumsList() {
      this.loading = true;
      let res = await getAlbumImage(this.queryParams);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.albumList = data.rows;
        this.albumList.forEach((item) => {
          item.imgList.map((i) => {
            i.location = i.location;
          });
        });
        this.isAlbums = true;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取相册列表失败:" + msg);
      }
    },
    // 选择相册查看相片列表
    getAlbumsPhoto(item) {
      this.photoList = item.imgList;
      this.isAlbums = false;
    },
    // 全选
    handleCheckAllChange(val) {
      this.selectPhotoIdList = val ? this.photoIdList : [];
      this.isIndeterminate = false;
    },
    // 批量选择
    handleCheckedPhotoChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.photoIdList.length;
      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.photoIdList.length;
    },
    // 删除
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该相册及其相片, 是否继续?",
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
        let res = await deleteAlbum(parseInt(id));
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("相册删除成功");
          this.getAlbumsList();
        } else {
          this.msgInfo("相册删除失败：" + msg);
        }
      }
    },
    // 恢复
    async handleRegain(data) {
      let id = data.id;
      data.flag === 0 ? (data.flag = 1) : (data.flag = 0);
      let res = await updateStatus(id, data);
      let { code } = res;
      if (code == 200) {
        this.msgSuccess("已从回收站恢复！");
        this.getAlbumsList();
      }
    },
    // 删除相片
    async handleDeletePhoto(id) {
      let res = await deleteAlbum(parseInt(id));
      let { code, msg } = res;
      if (code === 200) {
        this.msgSuccess("相册删除成功");
        this.getAlbumsList();
      } else {
        this.msgInfo("相册删除失败：" + msg);
      }
    },
    //加载分类数据
    async getCategoryList() {
      let res = await getListCategory({
        type: "resource",
      });
      let { code, data, msg } = res;
      if (code === 200) {
        let resourceTypeList = data.rows.map((item) => {
          return {
            name: item.name,
            id: item.id,
          };
        });
        this.resourceTypeList = resourceTypeList;
      } else {
        this.msgError("获取分类列表失败:" + msg);
      }
    },
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    },
  },
  created() {
    this.selectStatus("use");
    this.getAlbumsList();
    this.handleImgUploadChange = () => {};
    this.picTypeOptions = getAlbumsType();
    this.getCategoryList();
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
// 相册
.album-container {
  margin-top: 30px;
}
.album-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}
.album-cover::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}
.album-photo-count {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1.5rem;
  z-index: 1000;
  position: absolute;
  left: 0;
  right: 0;
  padding: 0 0.5rem;
  bottom: 2.6rem;
  color: #fff;
}
.album-name {
  text-align: center;
  margin-top: 0.5rem;
}
.album-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}
.album-opreation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}

// 相片列表
::v-deep .photo-container {
  margin-top: 30px;
  .el-checkbox__input {
    position: absolute;
    top: 10px;
    left: 20px;
  }
}
.all-check {
  display: inline-flex;
  align-items: center;
  margin-right: 1rem;
}
.check-count {
  margin-left: 1rem;
  font-size: 12px;
}
.photo-count {
  font-size: 12px;
  margin-left: 0.5rem;
}
.photo-item {
  width: 100%;
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}
.photo-img {
  width: 100%;
  height: 7rem;
  border-radius: 4px;
}
.photo-name {
  font-size: 14px;
  margin-top: 0.3rem;
  text-align: center;
}
.photo-opreation {
  position: absolute;
  z-index: 1000;
  top: 0.3rem;
  right: 0.5rem;
}
.album-check {
  display: flex;
  align-items: center;
}
.article-status-menu {
  font-size: 14px;
  margin-top: 10px;
  margin-bottom: 20px;
  color: #999;
}
.article-status-menu span {
  margin-right: 24px;
}
.status {
  cursor: pointer;
}
.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}
</style>
