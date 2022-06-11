<template>
  <div class="z-comments-item">
    <div class="z-header">
      <img :src="commentChild.createBy.avatar" alt="" />
      <span class="z-header-title"> {{ commentChild.createBy.userName }}</span>
      <span>发表于七天前</span>
    </div>
    <div class="main">
      <ContentArticle :article="commentChild" />
    </div>
    <div class="z-foot">
      <div class="z-foot-icon-main">
        <div @click="handleLike">
          <i class="iconfont icon-icon"></i>
          <span>{{ conversionNumber(commentChild.likeNum) }}</span>
        </div>
        <div @click="handleDis">
          <i class="iconfont icon-pinglun"></i>
          <span
            >{{ commentChild.isShowComment ? "收起" : "回复" }}
            {{ conversionNumber(commentChild.discussNum) }}</span
          >
        </div>
      </div>
      <span class="btn" @click="handleInitiateComment"> 添加回复 </span>
    </div>
    <!-- <template v-for="comment in commentList">
      <CommentList
        :commentChild="comment"
        @handleChildDis="handleChildDis"
        @handleInitiateComment="handleInitiateComment"
        v-if="
          commentChild.isShowComment && comment.id === commentChild.parentId
        "
      ></CommentList>
    </template> -->
  </div>
</template>
<script>
import { unitConverter } from "@/utils/zeng";
import ContentArticle from "@/components/article";
import CommentList from "@/components/comments";

export default {
  data() {
    return {};
  },
  components: {
    ContentArticle,
    CommentList,
  },
  props: {
    commentChild: {
      type: Object,
      default: () => {},
    },
  },
  methods: {
    handleLike() {
      console.log("点赞");
    },
    // 回复
    handleDis() {
      this.$emit("handleChildDis", this.commentChild);
    },
    handleInitiateComment() {
      this.$emit("handleInitiateComment", this.commentChild);
    },
    conversionNumber(number) {
      return unitConverter(number);
    },
  },
};
</script>
<style lang="scss" scoped>
.z-comments-item {
  margin-bottom: 20px;
  text-align: left;
  background: #f7f8fa;
  border-radius: 8px;
  padding: 16px 16px 0px 16px;
}
.z-header {
  height: 50px;
  width: 100%;
  font-size: 14px;
  img {
    width: 40px;
    height: 40px;
    vertical-align: middle;
    border-radius: 50%;
    margin-right: 10px;
  }
  .z-header-title {
    color: rgba(grey, 1);
  }
  span {
    margin-left: 10px;
    color: rgba(grey, 0.5);
  }
}
.z-foot {
  display: -webkit-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  display: flex;
  -webkit-align-items: center;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: justify;
  -webkit-justify-content: space-between;
  justify-content: space-between;
  padding: 12px 0px 16px 0px;
  border-top: 1px solid rgba(grey, 0.2);
  .z-foot-icon-main {
    width: 25%;
    display: flex;
    -webkit-box-align: center;
    align-items: center;
    margin-top: 8px;
    div {
      flex: 0 0 40%;
      cursor: pointer;
      .iconfont {
        font-size: 13px;
        padding: 5px;
      }
      span {
        font-size: 13px;
      }
      &:hover {
        color: royalblue;
      }
    }
  }
  .btn {
    background-color: transparent;
    color: rgba(grey, 0.5);
    cursor: pointer;
    &:hover {
      color: royalblue;
    }
  }
}
</style>
