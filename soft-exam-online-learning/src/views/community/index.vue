<template>
  <!-- 课程 -->
  <div id="community">
    <div class="_location"></div>
    <!-- 置顶按钮 -->
    <div class="to-top" @click="toTop">
      <i class="el-icon-upload2"></i>
    </div>
    <template v-if="isAdd">
      <div class="discuss">
        <div class="header-input">
          <el-input v-model="discuss.title" placeholder="请输入标题..." />
          <div class="btn">
            <span class="btn-close" @click="closeDis">取消</span>
            <span
              class="btn-add"
              @click="handleSubmit"
              v-text="submitText"
            ></span>
          </div>
        </div>
        <div class="topic">
          <div class="input">
            <el-select
              v-model="discuss.question"
              filterable
              placeholder="请选择题目..."
              :filter-method="searchQuestion"
              @change="changeQuestion"
            >
              <el-option
                v-for="item in questionOptions"
                :key="item.questionId"
                :label="item.name"
                :value="item.questionId"
              >
              </el-option>
            </el-select>
          </div>
        </div>
        <div class="main">
          <mavon-editor
            ref="editorMd"
            v-model="discuss.articleContent"
            @imgAdd="uploadArticleImg"
            class="my-edit-down"
          />
        </div>
      </div>
    </template>
    <template v-if="!isAdd">
      <!-- 头部导航 -->
      <div class="header">
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
          <button class="add-btn" @click="handleAdd">新建讨论</button>
        </div>
      </div>
      <!-- 内容 -->
      <div class="community-main">
        <div class="community-main-left">
          <ul class="z-row">
            <li
              class="z-col"
              v-for="item in currentPageDiscussList"
              :key="item.id"
              @click="toDiscussIndex(item.id)"
            >
              <img src="@/assets/images/lunbo02.jpg" alt="" />
              <div class="title">{{ item.title }}</div>
              <div class="content">
                <div class="content-txt">
                  {{ item.articleContent | filterHtml }}
                </div>
              </div>
              <div class="icon-main">
                <div>
                  <i class="iconfont icon-icon"></i>
                  <span>{{ conversionNumber(item.likeNum) }}</span>
                </div>
                <div>
                  <i class="iconfont icon-yanjing"></i>
                  <span>{{ conversionNumber(item.browseNum) }}</span>
                </div>
                <div>
                  <i class="iconfont icon-pinglun"></i>
                  <span>{{ conversionNumber(item.discussNum) }}</span>
                </div>
              </div>
            </li>
          </ul>
          <div class="question-page">
            <span
              class="add-page-btn"
              @click="loadPage"
              v-if="discussList.length > currentPageDiscussList.length"
              >加载更多</span
            >
          </div>
        </div>
        <div class="community-main-right">
          <ul class="z-row">
            <li class="z-col"></li>
            <li class="z-col"></li>
            <li class="z-col"></li>
            <li class="z-col"></li>
            <li class="z-col"></li>
          </ul>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import * as imageConversion from "image-conversion";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import { uploadImg } from "@/api/business/resource";
import {
  getDiscussList,
  getQuestionData,
  addDiscuss,
  updateDiscuss,
} from "@/api/business/discuss";
import { unitConverter, pagination } from "@/utils/zeng";

export default {
  name: "community",
  data() {
    return {
      // 模糊搜索
      timer: null,
      // 是否在新增讨论
      isAdd: false,
      isUpdate: false,
      activeStatus: "",
      queryDiscussMap: {
        pageNum: 1,
        pageSize: 100,
        parentId: 0,
      },
      // 总的list
      discussList: [],
      // 当前页的list
      currentPageDiscussList: [],
      pageSize: 4,
      pageNum: 1,
      // 问题options
      questionOptions: [],
      currentDiscuss: null,
      discuss: {
        id: "",
        title: "",
        question: null,
        articleContent: null,
        parentId: 0,
        type: undefined,
      },
      handleSubmitFun: null,
    };
  },
  components: {},
  methods: {
    // 取消
    closeDis() {
      this.isAdd = false;
    },
    // 选择状态
    selectStatus(status) {
      switch (status) {
        case "all":
          break;
        case "public":
          break;
        case "secret":
          break;
        case "draft":
          break;
        case "delete":
          break;
      }
      this.activeStatus = status;
    },
    // 问题选择
    changeQuestion(val) {
      let q = this.questionOptions.filter((item) => {
        return item.questionId === val;
      });
      this.discuss.type = q[0].type;
      this.currentDiscuss.question = val;
    },
    // 获取列表
    async getDiscussListData() {
      let res = await getDiscussList(this.queryDiscussMap);
      let { code, data } = res;
      if (code === 200) {
        // for (let i = 0; i < 5; i++) {
        //   this.discussList.push(data.rows[0]);
        // }
        this.discussList = data.rows;
        // console.log(this.discussList);
        // console.log(pagination(1, 4, this.discussList));
        // this.discussList = returnAllPageFunc(this.pageSize, this.discussList);
        // console.log(this.discussList);
        this.currentPageDiscussList = [
          ...pagination(this.pageNum, this.pageSize, this.discussList),
        ];
        // console.log(this.currentPageDiscussList);
        // console.log(this.currentPageDiscussList);
      }
    },
    loadPage() {
      // console.log(this.pageNum, this.discussList.length);
      // console.log(this.pageNum <= this.discussList.length);
      if (this.pageNum <= this.discussList.length) {
        this.pageNum++;
        this.getDiscussListData();
      }
    },
    // 搜索
    searchQuestion(keyword) {
      this.discuss.question = keyword;
      let data = this.discuss.question;
      this.clearTimer();
      if (data && data.length > 0) {
        this.timer = setTimeout(() => {
          this.handleSearch(data);
        }, 500);
      }
    },
    async handleSearch(keyword) {
      let res = await getQuestionData({
        name: keyword,
      });
      let { code, data } = res;
      if (code === 200) {
        this.questionOptions = data;
      }
    },
    clearTimer() {
      if (this.timer) {
        clearTimeout(this.timer);
      }
    },
    // 图片上传
    async uploadArticleImg(pos, file) {
      var formData = new FormData();
      formData.append("type", "Community");
      if (file.size / 1024 < ACCEPT_CONFIG.UPLOAD_SIZE) {
        formData.append("file", file);
        let res = uploadImg(formData);
        let img = process.env.VUE_APP_BASE_API + res.msg;
        this.$refs.editorMd.$img2Url(pos, img);
      } else {
        // 压缩到200KB,这里的200就是要压缩的大小,可自定义
        imageConversion
          .compressAccurately(file, ACCEPT_CONFIG.UPLOAD_SIZE)
          .then((res) => {
            formData.append(
              "file",
              new window.File([res], file.name, { type: file.type })
            );
            let upload = uploadImg(formData);
            let img = process.env.VUE_APP_BASE_API + upload.msg;
            this.$refs.editorMd.$img2Url(pos, img);
          });
      }
    },
    //
    handleAdd() {
      this.isAdd = true;
    },
    // 处理数字数量级
    conversionNumber(number) {
      return unitConverter(number);
    },
    // 提交
    handleSubmit() {
      if (this.discuss.title.trim() == "") {
        this.$message.warning("讨论标题不能为空");
        return false;
      }
      if (
        this.discuss.question == undefined &&
        this.discuss.type == undefined
      ) {
        this.$message.warning("讨论题目不能为空");
        return false;
      }
      if (this.discuss.articleContent.trim() == "") {
        this.$message.warning("讨论内容不能为空");
        return false;
      }
      let msg = "";
      if (this.isUpdate) {
        this.discuss.question = this.currentDiscuss.question;
        this.handleSubmitFun = updateDiscuss;
        msg = "编辑";
      } else {
        this.handleSubmitFun = addDiscuss;
        msg = "添加";
      }
      this.handleCallback(this.discuss, msg);
    },
    async handleCallback(obj, msg) {
      let res = await this.handleSubmitFun.call(this, obj);
      let { code, data } = res;
      if (code === 200 && data === 1) {
        alert(msg + "成功！");
        this.isUpdate = false;
        this.isAdd = false;
      } else {
        alert(msg + "失败！");
      }
    },
    toDiscussIndex(id) {
      this.$router.push({
        path: "discuss",
        query: {
          id: id,
        },
      });
    },
    toTop() {
      document.getElementsByClassName("_location")[0].scrollIntoView(true);
    },
  },
  created() {
    if (this.$route.params?.discuss) {
      let data = JSON.parse(this.$route.params?.discuss);
      this.currentDiscuss = JSON.parse(JSON.stringify(data));
      this.discuss = data;
      this.discuss.question = data.questionName;
      this.isAdd = true;
      this.isUpdate = true;
    }
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    },
    submitText() {
      return this.isUpdate ? "编辑讨论" : "发起讨论";
    },
  },
  filters: {
    filterHtml(value) {
      return value
        ?.replace(/<[^>]*>/g)
        ?.replaceAll("undefined", "")
        ?.replaceAll("&nbsp;", "");
    },
  },
  watch: {
    isAdd: {
      handler(val) {
        if (!val) {
          this.getDiscussListData();
        }
      },
      immediate: true,
    },
  },
};
</script>
<style lang="scss" scoped>
#community {
  width: 100%;
  height: 100%;
}
// 置顶按钮
.to-top {
  background: #fff;
  border-radius: 12px;
  bottom: 200px;
  box-shadow: 0px 9px 18px rgba(0, 0, 0, 0.06);
  color: #999;
  font-size: 30px;
  height: 60px;
  line-height: 60px;
  position: fixed;
  right: 40px;
  text-align: center;
  width: 60px;
  cursor: pointer;
}
.header {
  display: flex;
  justify-content: flex-start;
  background-color: #f7f8f9;
  height: 100px;
  width: 100%;
  margin-left: 100px;
}
.community-main {
  display: flex;
  justify-content: center;
  .z-row {
    width: 100%;
    // height: 100%;
    .z-col {
      cursor: pointer;
      box-shadow: 0 1px 2px rgba(grey, 0.8), 0 2px 8px rgba(grey, 0.8);
      display: inline-block;
      width: 100%;
      height: 160px;
      background-color: #fff;
      border-radius: 10px;
      margin-bottom: 10px;
      position: relative;
      img {
        position: absolute;
        top: 50%;
        left: 10px;
        transform: translateY(-50%);
        margin-left: 10px;
        width: 250px;
        height: 120px;
        border-radius: 10px;
      }
      .title {
        font-size: 16px;
        line-height: 22px;
        font-weight: 500;
        position: absolute;
        top: 25px;
        left: 280px;
      }
      .content {
        position: absolute;
        top: 60px;
        left: 280px;
        width: 55%;
        display: flex;
        -webkit-box-align: center;
        align-items: center;
        margin-top: 8px;
        overflow: hidden;
        .content-txt {
          width: 85%;
          font-size: 12px;
          line-height: 24px;
          color: #000;
          word-break: break-word;
          align-self: stretch;
          -webkit-line-clamp: 1;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
      .icon-main {
        position: absolute;
        top: 110px;
        left: 280px;
        width: 55%;
        display: flex;
        -webkit-box-align: center;
        align-items: center;
        margin-top: 8px;
        div {
          flex: 0 0 15%;
          .iconfont {
            font-size: 13px;
            padding: 5px;
          }
          span {
            font-size: 13px;
          }
        }
      }
    }
  }
}
.community-main-left {
  margin: 10px 10px;
  flex: 0 0 40%;
  width: 40%;
  height: 80%;
}
.community-main-right {
  margin: 10px 50px;
  flex: 0 0 20%;
  width: 20%;
}
//   分页
.question-page {
  width: 100%;
  height: 50px;
  margin-bottom: 20px;
  .page-item {
    margin-left: 30px;
    li {
      float: left;
      margin: 0px 0px 0px 20px;
      .el-button {
        background-color: #fff;
        color: #409eff;
        width: 65px;
      }
    }
    .activite-item .el-button {
      color: #fff !important;
      background-color: #409eff !important;
    }
  }
  .add-page-btn {
    border: none;
    line-height: 20px;
    outline: none;
    user-select: none;
    text-decoration: none;
    -webkit-box-align: center;
    align-items: center;
    -webkit-box-pack: center;
    justify-content: center;
    transition-property: color, box-shadow, background-color, opacity;
    transition-duration: 0.3s;
    overflow: hidden;
    cursor: pointer;
    opacity: 1;
    font-size: 14px;
    padding: 6px 12px;
    display: block;
    width: 208px;
    border-radius: 5px;
    background: #eaecef;
    color: #575758;
    margin: 40px auto 10px;
  }
  .add-page-btn:hover {
    background-color: #dee0e4;
  }
}

.add-btn {
  height: 35px;
  text-align: center;
  line-height: 20px;
  cursor: pointer;
  opacity: 1;
  background-color: #007aff;
  color: #fff;
  font-size: 14px;
  border-width: initial;
  border-style: none;
  border-color: initial;
  border-image: initial;
  outline: none;
  text-decoration: none;
  overflow: hidden;
  padding: 6px 12px;
  border-radius: 16px;
}
.article-status-menu {
  width: 50%;
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
// 话题
.topic {
  z-index: 1;
  height: 30px;
  width: 100%;
  position: relative;
  .input {
    float: left;
    margin-left: 20px;
  }
  .topic-btn {
    float: left;
    span {
      cursor: pointer;
      display: inline-block;
      height: 35px;
      line-height: 35px;
      width: 200px;
      text-align: center;
      font-size: 16px;
      font-weight: bold;
      background-color: #f2f2f3;
      border-radius: 10px;
    }
  }
}
// 发起讨论
::v-deep .my-edit-down {
  margin-top: 20px;
  height: calc(100vh - 260px);
}
::v-deep .discuss {
  width: 100%;
  height: 100%;
  margin: 0 auto;
  .header-input {
    height: 30px;
    width: 100%;
    display: flex;
    -webkit-box-align: center;
    align-items: center;
    height: 60px;
    padding: 0px 15px;
    .el-input {
      width: 80%;
    }
    input {
      font-size: 16px;
      font-weight: bold;
      flex: 1 1 0px;
      padding: 10px 0px;
      border-width: initial;
      border-style: none;
      border-color: initial;
      border-image: initial;
      outline: none;
      background: transparent;
    }
    .btn {
      width: 200px;
      height: 30px;
      margin-right: 100px;
      span {
        cursor: pointer;
        display: inline-block;
        text-align: center;
        font-size: 14px;
        border-radius: 5px;
      }
      .btn-close {
        padding: 6px 15px;
        color: #000;
        background-color: #fff;
        border: 1px solid #ccc;
        margin-right: 30px;
      }
      .btn-add {
        padding: 6px 15px;
        background-color: #007aff;
        color: #fff;
      }
    }
  }
  .main {
    width: 100%;
  }
}
</style>
