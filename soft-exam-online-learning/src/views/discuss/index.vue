<template>
  <div class="discuss">
    <!-- 内容 -->
    <div class="community-main">
      <div class="community-main-left">
        <div class="question-main">
          <span>讨论题目:&nbsp;&nbsp;</span>
          <span>
            {{ currentQuestion.name }}
          </span>
          <button class="edit-btn" @click="handleEditDiscuss">编辑内容</button>
        </div>
        <div class="z-discuss-container">
          <div class="header">
            <span class="header-title">
              <img :src="article.createBy.avatar" alt="" />
              {{ article.title }}
            </span>
            <div class="info">
              <span>{{ article.createBy.userName }}</span>
              <span>发表于七天前</span>
              <div class="circle-dot"></div>
              <span>最近编辑于七天前</span>
            </div>
          </div>
          <article
            class="article-content markdown-body"
            v-html="article.articleContent"
            ref="article"
          />
          <div class="article-copyright">
            <div class="dis-button">
              <div class="icon-main">
                <div>
                  <i class="iconfont icon-icon"></i>
                  <span>{{ conversionNumber(article.likeNum) }}</span>
                </div>
                <div>
                  <i class="iconfont icon-pinglun"></i>
                  <span>回复 {{ conversionNumber(article.discussNum) }}</span>
                </div>
              </div>
              <span class="btn" @click="handleInitiateDis">回复讨论</span>
            </div>
          </div>
        </div>
        <DiscussList
          @handleShowComment="handleShowComment"
          @handleInitiateComment="handleInitiateComment"
          @handleChildDis="handleChildDis"
          :commentChildList="commentChildList"
          :discussChildList="discussChildList"
          :isShowComment="isShowComment"
        ></DiscussList>
      </div>
      <div class="community-main-right">
        <ul class="z-row">
          <li class="z-col">
            <div class="wrap">
              <div class="col-item">参与人数</div>
              <span class="num">{{
                conversionNumber(article.discussNum)
              }}</span>
            </div>
            <div class="wrap">
              <div class="col-item">浏览次数</div>
              <span class="num">17k</span>
            </div>
            <div class="wrap-line"></div>
            <div class="wrap-tag">
              <div class="col-item">相关标签</div>
              <div class="tag-item">
                <span>计算机网络</span>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div
      class="z-edit-main"
      :class="{ 'z-edit-main-full': isFull }"
      v-if="isShowFull"
    >
      <span class="z-header-to-user" v-if="isComment"
        >回复 @{{ username }}</span
      >
      <div class="z-header-btn">
        <span class="btn-close" @click="closeDis">取消</span>
        <span class="full-screen" @click="handleFullScreen">{{
          isFull ? "退出全屏" : "全屏"
        }}</span>
        <span class="btn-add" @click="handleSubmitDis">发起讨论</span>
      </div>
      <mavon-editor
        ref="editorMd"
        v-model="discuss.articleContent"
        @imgAdd="uploadArticleImg"
        class="my-edit-down"
      />
    </div>
  </div>
</template>

<script>
import * as imageConversion from "image-conversion";
import Clipboard from "clipboard";
import { ACCEPT_CONFIG } from "@/utils/fileConfig";
import { unitConverter } from "@/utils/zeng";
import { uploadImg } from "@/api/business/resource";
import {
  getDiscussInfo,
  addDiscuss,
  getDiscussList,
  getQuestionInfo,
} from "@/api/business/discuss";
import DiscussList from "@/components/discuss";
export default {
  name: "discuss",
  components: { DiscussList },
  data() {
    return {
      config: {
        sites: ["qzone", "wechat", "weibo", "qq"],
      },
      article: {
        createBy: {},
      },
      commentList: [],
      count: 0,
      wordNum: "",
      readTime: "",
      articleHref: window.location.href,
      // 全屏
      isShowFull: false,
      isFull: false,
      isComment: false,
      queryDiscussMap: {
        pageNum: 1,
        pageSize: 10,
        parentId: undefined,
      },
      discuss: {
        articleContent: "",
        parentId: undefined,
      },
      discussChildList: [],
      commentChildList: [],
      clipboard: undefined,
      imgList: [],
      isShowComment: false,
      username: undefined,
      currentQuestion: {},
      editDiscuss: {},
      currentIndex: undefined,
    };
  },
  methods: {
    async getArticle(id) {
      const that = this;
      const index = parseInt(id);
      //查询文章
      let res = await getDiscussInfo(index);
      let { code, data } = res;
      if (code === 200) {
        this.getQuestion({
          id: data.question,
          type: data.type,
        });
        this.editDiscuss = data;
        this.markdownToHtml(data);
        this.$nextTick(() => {
          //统计文章字数
          this.wordNum = this.deleteHTMLTag(this.article.articleContent).length;
          //计算阅读时间
          this.readTime = Math.round(this.wordNum / 400) + "分钟";
          //添加代码复制功能
          this.clipboard = new Clipboard(".copy-btn");
          this.clipboard.on("success", () => {
            this.$toast({ type: "success", message: "复制成功" });
          });
          //添加图片预览功能
          const imgList = this.$refs.article.getElementsByTagName("img");
          for (var i = 0; i < imgList.length; i++) {
            this.imgList.push(imgList[i].src);
            imgList[i].style.cssText = "cursor:zoom-in;width:100%;";
            imgList[i].addEventListener("click", function () {
              that.previewImg(imgList[i].src);
            });
          }
        });
      }
    },
    markdownToHtml(article) {
      const MarkdownIt = require("markdown-it");
      const hljs = require("highlight.js");
      const md = new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight: function (str, lang) {
          // 当前时间加随机数生成唯一的id标识
          const codeIndex = parseInt(new Date.now());
          // 复制功能主要使用的是 clipboard.js
          let html = `<button class="copy-btn iconfont iconfuzhi" type="button" data-clipboard-action="copy" data-clipboard-target="#copy${codeIndex}"></button>`;
          const linesLength = str.split(/\n/).length - 1;
          // 生成行号
          let linesNum = '<span aria-hidden="true" class="line-numbers-rows">';
          for (let index = 0; index < linesLength; index++) {
            linesNum = linesNum + "<span></span>";
          }
          linesNum += "</span>";
          if (lang && hljs.getLanguage(lang)) {
            // highlight.js 高亮代码
            const preCode = hljs.highlight(lang, str, true).value;
            html = html + preCode;
            if (linesLength) {
              html += '<b class="name">' + lang + "</b>";
            }
            // 将代码包裹在 textarea 中，由于防止textarea渲染出现问题，这里将 "<" 用 "<" 代替，不影响复制功能
            return `<pre class="hljs"><code>${html}</code>${linesNum}</pre><textarea style="position: absolute;top: -9999px;left: -9999px;z-index: -9999;" id="copy${codeIndex}">${str.replace(
              /<\/textarea>/g,
              "</textarea>"
            )}</textarea>`;
          }
        },
      });
      //将markdown替换为html标签
      article.articleContent = md.render(article.articleContent);
      this.article = article;
    },
    previewImg(img) {
      this.$imagePreview({
        images: this.imgList,
        index: this.imgList.indexOf(img),
      });
    },
    deleteHTMLTag(content) {
      return content
        .replace(/<\/?[^>]*>/g, "")
        .replace(/[|]*\n/, "")
        .replace(/&npsp;/gi, "");
    },
    // 图片上传
    async uploadArticleImg(pos, file) {
      var formData = new FormData();
      formData.append("type", "Community");
      if (file.size / 1024 < ACCEPT_CONFIG.UPLOAD_SIZE) {
        formData.append("file", file);
        let upload = await uploadImg(formData);
        let img = process.env.VUE_APP_BASE_API + upload.msg;
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
    // 取消
    closeDis() {
      this.isShowFull = false;
    },
    // 退出全屏flag
    handleFullScreen() {
      this.isFull = !this.isFull;
    },
    // 打开编辑
    handleInitiateComment(discuss) {
      this.username = discuss.createBy.userName;
      this.discuss.parentId = discuss.id;
      // 默认退出全屏
      this.isFull = false;
      this.isShowFull = true;
      this.isComment = true;
    },
    // 显示评论
    handleShowComment(val) {
      this.queryDiscussMap.parentId = val;
      this.getCommentList();
    },
    // 回复讨论
    handleInitiateDis() {
      this.isShowFull = true;
      console.log(this.article);
      this.discuss.parentId = this.article.id;
    },
    // 获取孩子节点评论
    handleChildDis(val) {
      this.queryDiscussMap.parentId = val;
    },
    // 发起讨论提交
    handleSubmitDis() {
      if (this.discuss.articleContent.trim() == "") {
        this.$message.warning("回复内容不能为空");
        return false;
      }
      console.log(this.discuss);
      this.addDiscussData(this.discuss);
    },
    // 编辑讨论
    handleEditDiscuss() {
      console.log("编辑", this.editDiscuss);
      this.$router.push({
        name: "community",
        params: {
          discuss: JSON.stringify(this.editDiscuss),
        },
      });
    },
    async addDiscussData(list) {
      let res = await addDiscuss(list);
      let { code, data } = res;
      if (code === 200 && data === 1) {
        alert("添加成功！");
        this.isAdd = false;
        this.getChildDiscussList();
        this.getArticle(this.$route.query.id);
        this.isFull = false;
        this.isShowFull = false;
        this.isComment = false;
      } else {
        alert("添加失败！");
      }
    },
    // 查询所有评论
    async getCommentList() {
      let res = await getDiscussList(this.queryDiscussMap);
      let { code, data } = res;
      if (code === 200) {
        this.commentChildList = data.rows;
      }
    },
    // 查询所有回复讨论
    async getChildDiscussList() {
      let res = await getDiscussList(this.queryDiscussMap);
      let { code, data } = res;
      if (code === 200) {
        this.discussChildList = data.rows;
      }
    },
    // 查询当前讨论问题
    async getQuestion(question) {
      let res = await getQuestionInfo(question);
      let { code, data } = res;
      if (code === 200) {
        this.currentQuestion = data;
      }
    },

    conversionNumber(number) {
      return unitConverter(number);
    },
  },
  created() {
    this.queryDiscussMap.parentId = this.$route.query.id;
    this.getArticle(this.$route.query.id);
    this.getChildDiscussList();
  },
  destroyed() {
    this.clipboard?.destroy();
  },
  mounted() {},
  computed: {},
};
</script>

<style lang="scss" scoped>
.header {
  .header-title {
    img {
      width: 40px;
      vertical-align: middle;
      margin-right: 5px;
    }
    width: 100%;
    font-size: 18px;
    font-weight: 500;
    line-height: 32px;
  }
  .info {
    display: flex;
    align-items: center;
    font-size: 12px;
    margin-top: 10px;
    span {
      margin-left: 10px;
      color: rgba(grey, 0.5);
    }
    .circle-dot {
      width: 4px;
      height: 4px;
      border-radius: 100%;
      background: rgba(grey, 0.3);
      margin-left: 10px;
    }
  }
}
::v-deep.z-discuss-container {
  text-align: start;
  background: white;
  box-shadow: 0 2px 8px rgb(0 0 0 / 8%), 0 1px 2px rgb(0 0 0 / 10%);
  border-radius: 8px;
  padding: 20px;
}
.question-main {
  text-align: left;
  background: rgba(#f2f2f3, 1);
  padding: 10px 15px;
  color: rgba(grey, 1);
  line-height: 1.6;

  border-radius: 3px;
  span {
    &:nth-child(1) {
      font-size: 14px;
      font-weight: 600;
    }
    &:nth-child(2) {
      font-size: 13px;
    }
  }
  .edit-btn {
    float: right;
    margin-right: 10px;
    height: 30px;
    text-align: center;
    line-height: 20px;
    cursor: pointer;
    opacity: 1;
    background-color: #007aff;
    color: #fff;
    font-size: 12px;
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
}
.community-main {
  display: flex;
  justify-content: center;
  .z-row {
    width: 100%;
    height: 100%;
    .z-col {
      cursor: pointer;
      box-shadow: 0 1px 2px rgba(grey, 0.8), 0 2px 8px rgba(grey, 0.8);
      display: inline-block;
      width: 100%;
      height: 200px;
      background-color: #fff;
      border-radius: 10px;
      margin-bottom: 10px;
      position: relative;
      .wrap {
        height: 28px;
        width: 100%;
        position: relative;
        margin-top: 10px;
        .col-item {
          color: #848b9f;
          font-size: 14px;
          position: absolute;
          top: 20px;
          left: 20px;
        }
        .num {
          background: rgba(#848b9f, 0.1);
          color: #1f1c1c;
          border-radius: 3px;
          padding: 0 5px;
          font-size: 14px;
          line-height: 20px;
          position: absolute;
          top: 20px;
          right: 20px;
        }
      }
      .wrap-line {
        width: 80%;
        height: 1px;
        background: rgba(#848b9f, 0.2);
        margin: 18px auto;
      }
      .wrap-tag {
        height: 28px;
        width: 100%;
        position: relative;
        margin-top: 0px;
        .col-item {
          color: #848b9f;
          font-size: 14px;
          position: absolute;
          top: 0px;
          left: 20px;
        }
        .tag-item {
          color: #848b9f;
          font-size: 14px;
          position: absolute;
          top: 30px;
          left: 20px;
          span {
            float: left;
            font-size: 12px;
            transform: scale(0.8);
            line-height: 20px;
            background: rgba(#848b9f, 0.2);
            padding: 5px;
            color: rgb(48, 45, 45);
            border-radius: 15px;
          }
        }
      }
    }
  }
}
.community-main-left {
  margin: 10px 10px;
  flex: 0 0 50%;
  width: 50%;
}
.community-main-right {
  margin: 10px 50px;
  flex: 0 0 20%;
  width: 20%;
}
.article-info i {
  font-size: 14px;
}
.article-info {
  font-size: 14px;
  line-height: 1.75;
  display: inline-block;
}
.article-container:hover {
  box-shadow: 0 4px 12px 12px rgba(7, 17, 27, 0.15);
}

.article-content {
  word-break: break-word;
  line-height: 1.8;
  margin-top: 20px;
}
.article-operation {
  display: flex;
  align-items: center;
}
.article-category a {
  color: #fff !important;
}
.tag-container a {
  display: inline-block;
  margin: 0.5rem 0.5rem 0.5rem 0;
  padding: 0 0.75rem;
  width: fit-content;
  border: 1px solid #49b1f5;
  border-radius: 1rem;
  color: #49b1f5 !important;
  font-size: 12px;
  line-height: 2;
}
.tag-container a:hover {
  color: #fff !important;
  background: #49b1f5;
  transition: all 0.5s;
}

.dis-button {
  display: -webkit-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  display: flex;
  -webkit-align-items: center;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding: 14px 0px;
  margin-top: 12px;
  .icon-main {
    width: 25%;
    display: flex;
    -webkit-box-align: center;
    align-items: center;
    margin-top: 8px;
    div {
      flex: 0 0 50%;
      .iconfont {
        font-size: 18px;
        padding: 5px;
      }
      span {
        font-size: 13px;
      }
    }
  }
  .btn {
    border: none;
    border-radius: 3px;
    line-height: 20px;
    outline: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    -webkit-text-decoration: none;
    text-decoration: none;
    display: -webkit-inline-box;
    display: -webkit-inline-flex;
    display: -ms-inline-flexbox;
    display: inline-flex;
    -webkit-align-items: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    -webkit-justify-content: center;
    justify-content: center;
    transition-property: color, box-shadow, background-color, opacity;
    transition-duration: 0.3s;
    overflow: hidden;
    cursor: pointer;
    opacity: 1;
    color: #fff;
    font-size: 12px;
    padding: 4px 8px;
    margin-left: auto;
    margin-right: 12px;
    background-color: #007aff;
  }
}

.article-copyright {
  position: relative;
  margin-top: 40px;
  margin-bottom: 10px;
  font-size: 0.875rem;
  line-height: 2;
  padding: 0.625rem 1rem;
  border: 1px solid #eee;
}
.article-copyright span {
  color: #49b1f5;
  font-weight: bold;
}
.article-copyright a {
  text-decoration: underline !important;
  color: #99a9bf !important;
}
.article-copyright:before {
  position: absolute;
  top: 0.7rem;
  right: 0.7rem;
  width: 1rem;
  height: 1rem;
  border-radius: 1rem;
  background: #49b1f5;
  content: "";
}
.article-copyright:after {
  position: absolute;
  top: 0.95rem;
  right: 0.95rem;
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 0.5em;
  background: #fff;
  content: "";
}
.article-reward {
  margin: 5rem 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
.reward-btn {
  position: relative;
  display: inline-block;
  width: 100px;
  background: #49b1f5;
  margin: 0 1rem;
  color: #fff !important;
  text-align: center;
  line-height: 36px;
  font-size: 0.875rem;
}
.reward-btn:hover .reward-main {
  display: block;
}
.reward-main {
  display: none;
  position: absolute;
  bottom: 40px;
  left: 0;
  margin: 0;
  padding: 0 0 15px;
  width: 100%;
}
.reward-all {
  display: inline-block;
  margin: 0 0 0 -110px;
  padding: 20px 10px 8px !important;
  width: 320px;
  border-radius: 4px;
  background: #f5f5f5;
}
.reward-all:before {
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 100%;
  height: 20px;
  content: "";
}
.reward-all:after {
  content: "";
  position: absolute;
  right: 0;
  bottom: 2px;
  left: 0;
  margin: 0 auto;
  width: 0;
  height: 0;
  border-top: 13px solid #f5f5f5;
  border-right: 13px solid transparent;
  border-left: 13px solid transparent;
}
.reward-item {
  display: inline-block;
  padding: 0 8px;
  list-style-type: none;
}
.reward-img {
  width: 130px;
  height: 130px;
  display: block;
}
.reward-desc {
  margin: -5px 0;
  color: #858585;
  text-align: center;
}
.like-btn {
  display: inline-block;
  width: 100px;
  background: #969696;
  color: #fff !important;
  text-align: center;
  line-height: 36px;
  font-size: 0.875rem;
}
.like-btn-active {
  display: inline-block;
  width: 100px;
  background: #ec7259;
  color: #fff !important;
  text-align: center;
  line-height: 36px;
  font-size: 0.875rem;
}
pre.hljs {
  padding: 12px 2px 12px 40px !important;
  border-radius: 5px !important;
  position: relative;
  font-size: 14px !important;
  line-height: 22px !important;
  overflow: hidden !important;
  &:hover .copy-btn {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  code {
    display: block !important;
    margin: 0 10px !important;
    overflow-x: auto !important;
    &::-webkit-scrollbar {
      z-index: 11;
      width: 6px;
    }
    &::-webkit-scrollbar:horizontal {
      height: 6px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 5px;
      width: 6px;
      background: #666;
    }
    &::-webkit-scrollbar-corner,
    &::-webkit-scrollbar-track {
      background: #1e1e1e;
    }
    &::-webkit-scrollbar-track-piece {
      background: #1e1e1e;
      width: 6px;
    }
  }
  .line-numbers-rows {
    position: absolute;
    pointer-events: none;
    top: 12px;
    bottom: 12px;
    left: 0;
    font-size: 100%;
    width: 40px;
    text-align: center;
    letter-spacing: -1px;
    border-right: 1px solid rgba(0, 0, 0, 0.66);
    user-select: none;
    span {
      pointer-events: none;
      display: block;
      &:before {
        content: counter(linenumber);
        color: #999;
        display: block;
        text-align: center;
      }
    }
  }
  b.name {
    position: absolute;
    top: 7px;
    right: 45px;
    z-index: 1;
    color: #999;
    pointer-events: none;
  }
  .copy-btn {
    position: absolute;
    top: 6px;
    right: 6px;
    z-index: 1;
    color: #ccc;
    background-color: #525252;
    border-radius: 6px;
    display: none;
    font-size: 14px;
    width: 32px;
    height: 24px;
    outline: none;
  }
}
.z-edit-main-full {
  height: 100vh !important;
}
.z-edit-main {
  position: fixed;
  left: 0px;
  right: 0px;
  bottom: 0px;
  background: white;
  box-shadow: 0 2px 8px rgb(0 0 0 / 8%), 0 1px 2px rgb(0 0 0 / 10%);
  padding: 16px 16px 0px 16px;
  display: flex;
  flex-direction: column;
  z-index: 1;
  transition-property: transform, opacity, height;
  transition-duration: 0.3s;
  pointer-events: auto;
  opacity: 1;
  transform: translateY(0%);
  height: 50vh;
  cursor: auto;
  .z-header-to-user {
    margin-top: 20px;
    font-size: 16px;
    line-height: 22px;
    color: rgba(grey, 0.6);
    text-align: left;
    margin-right: 80px;
  }
  .z-header-btn {
    display: flex;
    margin-top: 20px;
    .btn-close {
      margin-left: 20px;
      line-height: 20px;
      user-select: none;
      display: inline-flex;
      -webkit-box-align: center;
      align-items: center;
      -webkit-box-pack: center;
      justify-content: center;
      transition-property: color, box-shadow, background-color, opacity;
      transition-duration: 0.3s;
      cursor: pointer;
      opacity: 1;
      box-shadow: inset 0px 0px 0px 1px rgba(grey, 0.4);
      background-color: transparent;
      color: rgba(grey, 0.8);
      font-size: 12px;
      border-width: initial;
      border-style: none;
      border-color: initial;
      border-image: initial;
      border-radius: 3px;
      outline: none;
      text-decoration: none;
      overflow: hidden;
      padding: 4px 8px;
      &:hover {
        color: rgba(grey, 0.5);
      }
    }
    .btn-add {
      border: none;
      border-radius: 3px;
      line-height: 20px;
      outline: none;
      -webkit-user-select: none;
      -moz-user-select: none;
      -ms-user-select: none;
      user-select: none;
      -webkit-text-decoration: none;
      text-decoration: none;
      display: -webkit-inline-box;
      display: -webkit-inline-flex;
      display: -ms-inline-flexbox;
      display: inline-flex;
      -webkit-align-items: center;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      -webkit-box-pack: center;
      -ms-flex-pack: center;
      -webkit-justify-content: center;
      justify-content: center;
      transition-property: color, box-shadow, background-color, opacity;
      transition-duration: 0.3s;
      overflow: hidden;
      cursor: pointer;
      opacity: 1;
      color: #fff;
      font-size: 12px;
      padding: 4px 8px;
      margin-left: auto;
      margin-right: 12px;
      background-color: #007aff;
    }
    .full-screen {
      margin-left: 20px;
      display: inline-flex;
      vertical-align: middle;
      -webkit-box-pack: center;
      justify-content: center;
      -webkit-box-align: center;
      align-items: center;
      line-height: 20px;
      cursor: pointer;
      color: rgba(grey, 0.6);
      height: 32px;
      font-size: 13px;
      border-width: 0px;
      border-style: initial;
      border-color: initial;
      border-image: initial;
      border-radius: 3px;
      transition: all 0.18s ease-in-out 0s;
      outline: 0px;
      margin-right: 0px;
      background: transparent;
      padding: 0px 9px;
      &:hover {
        color: rgba(grey, 0.3);
      }
    }
  }
}
::v-deep .my-edit-down {
  margin-top: 20px;
  height: 100%;
}
</style>
