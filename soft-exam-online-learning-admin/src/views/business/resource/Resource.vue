<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>资源管理</el-breadcrumb-item>
      <el-breadcrumb-item>资源列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-row :gutter="6">
        <el-col :span="5">
          <el-cascader
            size="small"
            v-model="queryResourceMap.fileType"
            clearable
            :options="fileTypeOption"
            :show-all-levels="false"
            placeholder="请选择文件类型查询"
            @change="fileChange"
          />
        </el-col>
        <el-col :span="8">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            @click="searchResource"
            >查询</el-button
          >
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
            @click="getResourceList"
            >刷新</el-button
          >
        </el-col>
      </el-row>
      <!-- 表格区域 -->

      <el-table
        size="mini"
        v-loading="loading"
        border
        stripe
        :data="resourceData"
        style="width: 100%; margin-top: 20px"
        height="500"
      >
        <el-table-column
          prop="serialNum"
          label="ID"
          align="center"
          min-width="200"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="name"
          width="100"
          label="文件名"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="size"
          width="100"
          label="文件大小"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="relativePath"
          label="location"
          align="center"
          min-width="200"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="uniqueIdentifier"
          label="identifier"
          align="center"
          min-width="200"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="uploadTime"
          min-width="200"
          label="上传时间"
          align="center"
        ></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-download"
              :loading="btnLoading"
              :disabled="btnDisabled"
              @click="handleDownload(scope.row)"
              >下载</el-button
            >
            <el-button
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
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
        :current-page="queryResourceMap.pageNum"
        :page-sizes="[6, 10, 15, 20]"
        :page-size="queryResourceMap.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>
    <!-- 文件上传弹出框 -->
    <el-dialog
      title="文件上传"
      :visible.sync="uploadVisibleVid"
      width="50%"
      height="700"
      append-to-body
      custom-class="paper-dialog "
      @close="handlerUploadClose"
    >
      <UploadBigFile class="uploadSlot"></UploadBigFile>
    </el-dialog>
  </div>
</template>

<script>
import UploadBigFile from "@/components/upload/Upload.vue";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import { getListResource, delResource } from "@/api/business/resource";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      loadingOverLay: null,
      total: 0, //总共多少条数据
      resourceData: [], //表格数据
      // 搜索条件
      fileTypeOption: [],
      queryResourceMap: {
        pageNum: 1,
        pageSize: 6,
        fileType: null,
      },
      // 上传文件
      uploadVisibleVid: false,
    };
  },
  components: { UploadBigFile },
  methods: {
    // 上传文件
    handleUpload() {
      this.uploadVisibleVid = true;
    },
    handlerUploadClose() {
      this.uploadVisibleVid = false;
    },
    //重置查询表单
    resetForm() {
      this.queryResourceMap = {
        pageNum: 1,
        pageSize: 6,
        fileType: null,
      };
    },
    /**
     * 搜索科目
     */
    searchResource() {
      this.queryResourceMap.pageNum = 1;
      this.getResourceList();
    },
    fileChange(val) {
      if (val) {
        this.queryResourceMap.fileType = val[1]?.slice(-1)[0];
      }
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryResourceMap.pageSize = newSize;
      this.getResourceList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryResourceMap.pageNum = current;
      this.getResourceList();
    },
    // 获取文件类型
    getAllFileType() {
      let images = ACCEPT_CONFIG.image;
      let video = ACCEPT_CONFIG.video;
      let document = ACCEPT_CONFIG.document;
      let arr1 = [],
        arr2 = [],
        arr3 = [];
      images.forEach((ele) => {
        arr1.push({
          value: ele,
          label: ele,
        });
      });
      this.fileTypeOption.push({
        value: "images",
        label: "images",
        children: arr1,
      });
      video.forEach((ele) => {
        arr2.push({
          value: ele,
          label: ele,
        });
      });
      this.fileTypeOption.push({
        value: "video",
        label: "video",
        children: arr2,
      });
      document.forEach((ele) => {
        arr3.push({
          value: ele,
          label: ele,
        });
      });
      this.fileTypeOption.push({
        value: "document",
        label: "document",
        children: arr3,
      });
    },
    /**
     * 删除文件
     */
    async handleDelete(row) {
      var res = await this.$confirm(
        "此操作将永久删除该文件, 是否继续?",
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
        let res = await delResource(row);
        let { code, msg } = res;
        if (code === 200) {
          this.msgSuccess("文件删除成功");
          this.getResourceList();
        } else {
          this.msgInfo("文件删除失败:" + msg);
        }
      }
    },
    /**
     * 加载文件资源列表
     */
    async getResourceList() {
      this.loading = true;
      let res = await getListResource(this.queryResourceMap);
      let { code, msg, data } = res;
      if (code === 200) {
        this.total = data.total;
        this.resourceData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取文件资源列表失败:" + msg);
      }
    },

    async handleDownload(row) {
      this.loadingOverLay = this.$loading({
        lock: true,
        text: "文件生成中",
        spinner: "el-icon-loading",
        background: "rgba(0,0,0,0.7)",
      });
      this.downloadResource(row.id);
      this.loadingOverLay.close();
    },
  },
  created() {
    this.getResourceList();
    this.getAllFileType();
  },
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
