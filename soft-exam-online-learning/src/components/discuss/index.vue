<template>
  <div>
    <div class="z-discuss-item" v-for="(discuss, discussIndex) in discussList">
      <div class="z-header">
        <img :src="discuss.createBy.avatar" alt="" />
        <span class="z-header-title">{{ discuss.createBy.userName }}</span>
        <span>发表于七天前</span>
      </div>
      <div class="z-main">
        <ContentArticle :article="discuss" />
      </div>
      <div class="z-discuss-foot">
        <div class="z-foot-icon-main">
          <div @click="handleLike">
            <i class="iconfont icon-icon"></i>
            <span> {{ conversionNumber(discuss.likeNum) }}</span>
          </div>
          <div @click="handleDis(discuss)" v-if="commentList.length > 0">
            <i class="iconfont icon-pinglun"></i>
            <span
              >{{ discuss.isShowComment ? "收起" : "回复" }}
              {{ conversionNumber(discuss.discussNum) }}</span
            >
          </div>
        </div>
        <span class="btn" @click="handleInitiateComment(discuss)">
          添加回复
        </span>
      </div>
      <template v-for="item in commentList">
        <CommentList
          :commentChild="item"
          @handleChildDis="handleChildDis"
          @handleInitiateComment="handleInitiateComment"
          v-if="discuss.isShowComment && discuss.id === item.parentId"
        ></CommentList>
      </template>
    </div>
  </div>
</template>
<script>
import { unitConverter } from "@/utils/zeng";
import CommentList from "@/components/comments";
import ContentArticle from "@/components/article";
export default {
  data() {
    return {
      discussList: [],
      commentList: [],
    };
  },
  components: {
    CommentList,
    ContentArticle,
  },
  props: {
    discussChildList: {
      type: Array,
      default: () => [],
    },
    commentChildList: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    handleLike() {
      console.log("点赞");
    },
    // 回复
    handleDis(discuss) {
      discuss.isShowComment = !discuss.isShowComment;
      this.$emit("handleShowComment", discuss.id);
    },
    handleInitiateComment(commentChild) {
      console.log(commentChild);
      this.$emit("handleInitiateComment", commentChild);
    },
    handleChildDis(commentChild) {
      commentChild.isShowComment = !commentChild.isShowComment;
    },
    conversionNumber(number) {
      return unitConverter(number);
    },
  },
  watch: {
    discussChildList: {
      handler(val) {
        this.$nextTick(() => {
          this.discussList = JSON.parse(JSON.stringify(val));
          this.discussList.map((item) => {
            this.$set(item, "isShowComment", false);
          });
        });
      },
      deep: true,
    },
    commentChildList: {
      handler(val) {
        this.$nextTick(() => {
          this.commentList = JSON.parse(JSON.stringify(val));
          this.commentList.map((item) => {
            this.$set(item, "isShowComment", false);
          });
        });
      },
    },
  },
};
</script>
<style lang="scss" scoped>
.article-content {
  word-break: break-word;
  margin-top: 20px;
  font-size: 15px;
  line-height: 1.8;
}
.z-discuss-item {
  margin-bottom: 20px;
  text-align: left;
  background: white;
  box-shadow: 0 2px 8px rgb(0 0 0 / 8%), 0 1px 2px rgb(0 0 0 / 10%);
  border-radius: 8px;
  padding: 16px 16px 5px 16px;
  transition: 1s 2s;
}
.z-header {
  height: 50px;
  width: 100%;
  font-size: 12px;
  img {
    width: 40px;
    height: 40px;
    vertical-align: middle;
    border-radius: 50%;
    margin-right: 10px;
  }
  .z-header-title {
    font-size: 14px;
    color: rgba(grey, 1);
  }
  span {
    margin-left: 10px;
    color: rgba(grey, 0.5);
  }
}
.z-main {
  width: 70%;
}
.z-discuss-foot {
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
