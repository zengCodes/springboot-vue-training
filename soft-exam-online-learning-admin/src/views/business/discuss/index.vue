<template>
  <div id="course">
    <!-- 面包导航 -->
    <el-breadcrumb
      separator="/"
      style="padding-left: 10px; padding-bottom: 10px; font-size: 12px"
    >
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>学习管理</el-breadcrumb-item>
      <el-breadcrumb-item>讨论社区</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 文章列表区域 -->
    <el-card class="discuss-card">
      <el-row :gutter="22">
        <el-col :span="12">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-refresh-right"
            @click="getAllDiscussList"
            >刷新</el-button
          >
        </el-col>
      </el-row>
      <el-table
        v-loading="loading"
        :data="discussData"
        style="width: 100%; margin-top: 20px"
        height="600"
        border
        lazy
        row-key="id"
        :load="loadChildren"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column
          prop="articleContent"
          label="回复内容"
          min-width="200"
          show-overflow-tooltip
          :formatter="filterHtml"
        />
        <el-table-column
          prop="question.name"
          label="讨论问题"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column
          prop="createBy.userName"
          label="回复账号"
          min-width="100"
          align="center"
        />
        <el-table-column prop="likeNum" label="点赞量" align="center" />
        <el-table-column prop="discussNum" label="回复数" align="center" />
        <el-table-column
          prop="createTime"
          label="创建时间"
          align="center"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="modifiedTime"
          label="创建时间"
          align="center"
          min-width="150"
        ></el-table-column>
        <el-table-column label="操作" align="center">
          <!-- 操作 -->
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.parentId != 0"
              type="danger"
              size="mini"
              icon="el-icon-delete"
              @click="handleDelete(scope.row.id)"
              v-hasPermission="['system:discuss:del']"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getDiscussList, delDiscuss } from "@/api/business/discuss";
export default {
  data() {
    return {
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      // 文章列表
      discussData: [],
      queryDiscussMap: {
        pageNum: 1,
        pageSize: 6,
      },
    };
  },
  methods: {
    filterHtml(row, column, value, index) {
      return value
        ?.replace(/<[^>]*>/g)
        ?.replaceAll("undefined", "")
        ?.replaceAll("&nbsp;", "");
    },
    loadChildren(tree, treeNode, resolve) {
      console.log(tree, treeNode, resolve);
    },
    async handleDelete(id) {
      let res = await this.$confirm(
        "此操作将永久删除该讨论信息, 是否继续?",
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
        delDiscuss(id)
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
    // 获取文章列表
    async getAllDiscussList() {
      this.loading = true;
      let res = await getDiscussList(this.queryDiscussMap);
      let { code, data, msg } = res;
      if (code === 200) {
        this.total = data.total;
        this.discussData = data.rows;
        setTimeout(() => {
          this.loading = false;
        }, 300);
      } else {
        this.msgError("获取讨论列表失败:" + msg);
      }
    },
  },
  created() {
    this.getAllDiscussList();
  },
  computed: {},
};
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common.scss";
</style>
