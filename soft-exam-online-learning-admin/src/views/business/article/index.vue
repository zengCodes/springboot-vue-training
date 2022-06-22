<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>学习管理</el-breadcrumb-item>
      <el-breadcrumb-item>文章列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 文章列表区域 -->
    <el-card class="article-card" v-if="!openEdit">
      <el-row :gutter="22">
        <el-col :span="12">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-refresh-right"
            @click="getArticleList"
            >刷新</el-button
          >
          <el-button
            size="small"
            icon="el-icon-document-add"
            type="warning"
            @click="handleEdit(null)"
            >新建文章</el-button
          >
        </el-col>
      </el-row>
      <el-row :gutter="22">
        <el-col :span="12">
          <div class="article-status-menu">
            <span>分类栏目</span>
            <span @click="selectStatus('all')" :class="isActive('all')"
              >全部</span
            >
            <span @click="selectStatus('public')" :class="isActive('public')">
              公开
            </span>
            <span @click="selectStatus('secret')" :class="isActive('secret')">
              私密
            </span>
            <span @click="selectStatus('draft')" :class="isActive('draft')">
              草稿箱
            </span>
            <span @click="selectStatus('delete')" :class="isActive('delete')">
              回收站
            </span>
          </div>
        </el-col>
      </el-row>
      <el-table
        size="mini"
        v-loading="loading"
        border
        stripe
        :data="articleData"
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column prop="articleCover" label="文章封面" align="center">
          <template slot-scope="scope">
            <el-image class="article-cover" :src="scope.row.articleCover" />
            <i
              v-if="scope.row.status == 1"
              class="iconfont icon-yanjing_xianshi article-status-icon"
            />
            <i
              v-if="scope.row.status == 2"
              class="iconfont icon-yanjing_yincang article-status-icon"
            />
            <i
              v-if="scope.row.status == 3"
              class="iconfont el-icon-mycaogaoxiang article-status-icon"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="articleTitle"
          label="文章标题"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="categoryName"
          label="分类"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="tagList"
          label="标签"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="viewsCount"
          label="浏览量"
          align="center"
        ></el-table-column>
        <el-table-column prop="likeCount" label="点赞量" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.likeCount">
              {{ scope.row.likeCount }}
            </span>
            <span v-else>0</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100">
          <template slot-scope="scope">
            <el-tag>
              {{ scope.row.sort }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="发表时间"
          width="130"
          align="center"
        >
          <template slot-scope="scope">
            <i class="el-icon-time" style="margin-right: 5px" />
            {{ scope.row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="mini"
              icon="el-icon-edit"
              @click="handleEdit(scope.row.id)"
              v-hasPermission="['business:paper:edit']"
              >编辑文章</el-button
            >
            <el-button
              style="margin-left: 10px"
              type="text"
              size="mini"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 文章编辑区域 -->
    <el-card class="box-card" v-else>
      <el-row :gutter="22">
        <el-col :span="19">
          <el-input
            v-model="article.articleTitle"
            size="small"
            placeholder="输入文章标题"
          />
        </el-col>
        <el-col :span="2">
          <el-button
            type="danger"
            size="medium"
            class="save-btn"
            @click="saveArticleDraft"
            v-if="article.id == null || article.status == 3"
          >
            保存草稿
          </el-button>
        </el-col>
        <el-col :span="2">
          <el-button
            type="danger"
            size="medium"
            @click="handleRelease"
            style="margin-left: 10px"
          >
            {{ isUpdate ? "修改文章" : "发布文章" }}
          </el-button>
        </el-col>
        <el-col :span="2">
          <el-button
            type="danger"
            size="medium"
            class="save-btn"
            @click="toBackHome"
          >
            返回列表
          </el-button>
        </el-col>
      </el-row>
      <!-- 文章编辑区域 -->
      <el-row :gutter="22">
        <mavon-editor
          ref="editorMd"
          v-model="article.articleContent"
          @imgAdd="uploadArticleImg"
          class="my-edit-down"
        />
      </el-row>
    </el-card>
    <!-- 发布文章弹出框 -->
    <el-dialog
      :title="handleDialogTitle"
      :visible.sync="handleDialogVisible"
      width="45%"
      height="500"
      append-to-body
      custom-class="paper-dialog"
      @close="handleClose"
    >
      <!-- 表单 -->
      <el-form
        :model="article"
        :label-position="labelPosition"
        :rules="handleDialogFormRules"
        ref="handleDialogFormRef"
        label-width="80px"
      >
        <el-row :gutter="22">
          <el-col :span="22">
            <!-- 文章分类 -->
            <el-form-item label="文章分类">
              <el-tag
                type="success"
                v-show="article.categoryName"
                style="margin: 0 1rem 0 0"
                :closable="true"
                @close="removeCategory"
              >
                {{ article.categoryName }}
              </el-tag>
              <!-- 分类选项 -->
              <el-popover
                placement="bottom-start"
                width="460"
                trigger="click"
                v-if="!article.categoryName"
              >
                <div class="popover-title">分类</div>
                <!-- 搜索框 -->
                <el-autocomplete
                  style="width: 100%"
                  v-model="categoryName"
                  :fetch-suggestions="searchCategories"
                  placeholder="请输入分类名搜索，enter可添加自定义分类"
                  :trigger-on-focus="false"
                  @keyup.enter.native="saveCategory"
                  @select="handleSelectCategories"
                >
                  <template slot-scope="{ item }">
                    <div>{{ item.value }}</div>
                  </template>
                </el-autocomplete>
                <!-- 分类 -->
                <div class="popover-container">
                  <div
                    v-for="item of categoryList"
                    :key="item.id"
                    class="category-item"
                    @click="addCategory(item)"
                  >
                    {{ item.value }}
                  </div>
                </div>
                <el-button type="success" plain slot="reference" size="small">
                  添加分类
                </el-button>
              </el-popover>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <!-- 文章标签 -->
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="22">
            <el-form-item label="文章封面" prop="articleCover">
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
                :on-success="handleImgUploadSuccess"
              >
                <i class="el-icon-plus" v-if="!upload.uploadDisabled"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="置顶" prop="isTop">
              <el-switch
                v-model="article.isTop"
                active-color="#13ce66"
                inactive-color="#F4F4F5"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="发布形式" prop="status">
              <el-radio-group v-model="article.status">
                <el-radio :label="1">公开</el-radio>
                <el-radio :label="0">私密</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="22">
          <el-col :span="11">
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="article.sort"
                :min="1"
                label="排序"
                size="small"
              ></el-input-number>
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
import { Loading } from "element-ui";
let loading;
import { getToken } from "@/utils/auth";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import * as imageConversion from "image-conversion";
import {
  getListArticle,
  addArticle,
  getArticle,
  updateArticleStatus,
  updateArticle,
  delArticle,
} from "@/api/business/study/article";
import { uploadImg } from "@/api/business/resource";
import { getListCategory } from "@/api/business/category";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      // 文章列表
      articleData: [],
      tagList: [],
      categoryList: [],
      // 选中
      activeStatus: false,
      // 是否处于文章编辑
      isUpdate: false,
      // 打开编辑器
      openEdit: false,
      // 标签
      tagName: null,
      // 标签list
      tabList: [],
      queryMap: {
        pageNum: 1,
        pageSize: 6,
      },
      // 上传图片
      upload: {
        dataType: {
          type: "Article",
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
      // 弹出框
      handleDialogVisible: false,
      handleDialogTitle: "",
      handleDialogFormRules: {},
      labelPosition: "right",
      // 文章
      article: {
        id: null,
        status: null,
        isTop: undefined,
        articleTitle: "",
        // 分类
        categoryName: null,
        // 标签
        tagList: [],
        articleContent: "",
        articleCover: "",
        sort: undefined,
      },
      fileList: [],
      categoryName: null,
    };
  },
  methods: {
    resetArticle() {
      this.article = {
        id: null,
        status: null,
        isTop: undefined,
        articleTitle: "",
        // 分类
        categoryName: null,
        // 标签
        tagList: [],
        articleContent: "",
        articleCover: "",
        sort: undefined,
      };
    },
    // 返回列表
    toBackHome() {
      this.handleDialogVisible = false;
      this.openEdit = false;
      this.resetArticle();
    },
    // 选择状态
    selectStatus(status) {
      switch (status) {
        case "all":
          this.isDelete = 0;
          this.status = null;
          break;
        case "public":
          this.isDelete = 0;
          this.status = 1;
          break;
        case "secret":
          this.isDelete = 0;
          this.status = 2;
          break;
        case "draft":
          this.isDelete = 0;
          this.status = 3;
          break;
        case "delete":
          this.isDelete = 1;
          this.status = null;
          break;
      }
      this.activeStatus = status;
    },
    // 显示列表图片
    showDefaultImg(row) {
      return row.articleCover;
    },
    // 保存草稿
    saveArticleDraft() {
      if (this.article.articleTitle.trim() == "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() == "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      this.article.status = 3;
    },
    // 发布文章
    handleRelease() {
      // 校验文章
      if (this.article.articleTitle.trim() == "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() == "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      // 打开弹出框
      this.handleDialogTitle = "发布文章";
      this.handleDialogVisible = true;
    },
    // 关闭弹出框
    handleClose() {
      this.handleDialogVisible = false;
      // this.$refs.handleDialogFormRef.clearValidate()
      // this.resetArticle()
    },
    // 文章提交
    async handleDialogSubmit() {
      if (this.isUpdate) {
        const id = this.article.id;
        updateArticle(id, this.article)
          .then((res) => {
            let { code, data } = res;
            if (code === 200 && data === 1) {
              this.msgSuccess("文章信息修改成功");
              this.handleDialogVisible = false;
              this.openEdit = false;
              this.resetArticle();
            } else {
              this.msgError("文章信息修改成功");
            }
          })
          .catch((err) => {});
      } else {
        addArticle(this.article)
          .then((res) => {
            let { code, data } = res;
            if (code === 200 && data === 1) {
              this.msgSuccess("文章信息新增成功");
              this.handleDialogVisible = false;
              this.openEdit = false;
              this.resetArticle();
            } else {
              this.msgError("文章信息新增成功");
            }
          })
          .catch((err) => {});
      }
    },
    // 添加标签
    handleAddTag(item) {},
    // 选择标签
    handleSelectTag(item) {
      this.addTag({
        name: item.value,
      });
    },
    // 分类搜索
    handleSelectCategories(item) {
      this.addCategory({
        name: item.value,
        id: item.id,
      });
    },
    // 搜索标签
    async searchCategories(keywords, callback) {
      let list = [];
      let data = await this.getCategoryList(keywords);
      if (data === undefined || data?.length === 0) {
        list.push({
          id: "-1",
          value: "无匹配结果",
        });
      } else {
        list = data.map((item) => {
          return {
            value: `${item.name}`,
            id: `${item.id}`,
          };
        });
      }
      callback(list);
    },
    saveTag() {
      if (this.tagName.trim() != "") {
        this.addTag({
          name: this.tagName,
        });
        this.tagName = "";
      }
    },
    saveCategory() {
      if (this.categoryName.trim() != "") {
        this.addCategory({
          name: this.categoryName,
        });
        this.categoryName = "";
      }
    },
    addTag(item) {
      if (this.article.tagList.indexOf(item.name) == -1) {
        this.article.tagList.push(item.name);
      }
    },
    addCategory(item) {
      this.article.categoryName = item.name;
    },
    // 清除标签
    removeCategory() {
      this.article.categoryName = null;
    },
    tagClass(item) {},
    // 排序
    articleSort() {},
    // 编辑
    async handleEdit(id) {
      this.isUpdate = true;
      if (id != null) {
        let res = await getArticle(id);
        this.article = res?.data;
        this.upload.file.push({
          url: res?.data?.articleCover,
        });
      } else {
        this.isUpdate = false;
      }
      this.openEdit = true;
    },
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该文章信息, 是否继续?",
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
        delArticle(id)
          .then((res) => {
            let { code, msg } = res;
            if (code === 200) {
              this.msgSuccess("文章信息删除成功");
              this.getArticleList();
            } else {
              this.msgInfo("文章信息删除失败：" + msg);
            }
          })
          .catch((err) => {
            console.log(err);
          });
      }
    },
    handleImgUploadSuccess(response, file, fileList) {
      this.upload.uploadDisabled = true;
      this.article.articleCover = response.msg;
      this.upload.file.push({
        url: response.msg,
      });
    },

    handleImgUploadChange(file, fileList) {
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
      this.upload.uploadDisabled = fileList.length >= this.upload.limit;
    },
    //删除图片时
    handleRemove(file, fileList) {
      if (file.id) {
        console.log("删除了已被上传过的图片");
        console.log(file.id);
        this.upload.file.push(file.id);
      }
      this.upload.file = fileList;
      this.upload.uploadDisabled = fileList.length >= this.upload.limit;
    },
    //文件超出个数限制时
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    // 图片上传
    uploadArticleImg(pos, file) {
      var formData = new FormData();
      formData.append("type", "Article");
      if (file.size / 1024 < ACCEPT_CONFIG.UPLOAD_SIZE) {
        formData.append("file", file);
        uploadImg(formData)
          .then((res) => {
            let img = res.msg;
            this.$refs.editorMd.$img2Url(pos, img);
          })
          .catch((err) => {});
      } else {
        // 压缩到200KB,这里的200就是要压缩的大小,可自定义
        imageConversion
          .compressAccurately(file, ACCEPT_CONFIG.UPLOAD_SIZE)
          .then((res) => {
            formData.append(
              "file",
              new window.File([res], file.name, { type: file.type })
            );
            uploadImg(formData)
              .then((res) => {
                let img = res.msg;
                this.$refs.editorMd.$img2Url(pos, img);
              })
              .catch((err) => {});
          });
      }
    },
    // 获取文章列表
    getArticleList() {
      this.loading = true;
      getListArticle(this.queryMap).then((res) => {
        let { code, data, msg } = res;
        if (code === 200) {
          this.total = data.total;
          this.articleData = data.rows;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        } else {
          this.msgError("获取文章列表失败:" + msg);
        }
      });
    },
    // 获取文章类别
    async getCategoryList(keywords) {
      let list = [];
      await getListCategory({
        pageNum: 1,
        pageSize: 100,
        type: keywords,
      })
        .then((res) => {
          let { code, data } = res;
          if (code === 200) {
            list = data.rows;
          } else {
            list = [];
          }
        })
        .catch((err) => {});
      return list;
    },
  },
  created() {
    this.getArticleList();
    this.upload.fileType = ACCEPT_CONFIG.image.join(",");
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
::v-deep .my-edit-down {
  margin-top: 20px;
  height: calc(100vh - 210px);
}
.article-status-menu {
  font-size: 14px;
  margin-top: 40px;
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
::v-deep .article {
  .article-cover {
    position: relative;
    width: 100%;
    height: 90px;
    border-radius: 4px;
  }
  .article-cover::after {
    content: "";
    background: rgba(0, 0, 0, 0.3);
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
  }
  .article-status-icon {
    color: #fff;
    font-size: 14px;
    position: absolute;
    right: 20px;
    bottom: 10px;
  }
}
</style>
